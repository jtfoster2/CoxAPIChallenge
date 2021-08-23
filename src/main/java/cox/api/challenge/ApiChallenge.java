package cox.api.challenge;
import com.google.gson.Gson;

import cox.api.challenge.data_interfaces.AddDealer;
import cox.api.challenge.data_interfaces.Dealers;
import cox.api.challenge.data_interfaces.Vehicles;
import cox.api.challenge.data_interfaces.VehicleIDs;
import cox.api.challenge.rest_utility.RestBuilder;
import cox.api.challenge.data_workers.DealerWorker;
import cox.api.challenge.data_workers.VehicleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ApiChallenge {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiChallenge.class);
    private static final int READ_TIMEOUT = 4500;
    private static final int CONN_TIMEOUT = 5000;
    private static final String MAX_CONNECTION = "10";

    public static void main(final String[] args) {
        SpringApplication.run(ApiChallenge.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        System.setProperty("http.maxConnections", MAX_CONNECTION);

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(READ_TIMEOUT);
        requestFactory.setConnectTimeout(CONN_TIMEOUT);

        return new RestTemplate(requestFactory);
    }

    @Bean
    public RestBuilder restHelper() {
        return new RestBuilder(restTemplate());
    }

    @Bean
    public String run(final RestTemplate restTemplate) throws InterruptedException {
        final Gson g = new Gson();

        // Retrieving dataSetID + VehicleID from API Server
        final String dataSetId = restHelper().getDataSetId();
        LOGGER.info("Retrieved Dataset ID:" + dataSetId);
        final VehicleIDs vehicleIds = restHelper().getVehicleIds(dataSetId);
        LOGGER.info("Retrieved Vehicle IDs:" + vehicleIds);

        // Get id for each Vehicle
        final List<Vehicles> vehicleList = new ArrayList<>();
        final List<Callable<VehicleWorker>> vehicleWorkerCallableList = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(vehicleIds.getVehicleIds().length);

        for (final String id : vehicleIds.getVehicleIds()) {
            final VehicleWorker newV = new VehicleWorker(restTemplate, dataSetId, id);
            vehicleWorkerCallableList.add(newV);
        }

        executor.invokeAll(vehicleWorkerCallableList);

        for (final Callable<VehicleWorker> vw : vehicleWorkerCallableList) {
            vehicleList.add(g.fromJson(((VehicleWorker) vw).getVehicleInfo(), Vehicles.class));
        }

        // create dealer to vehicles list <FAST NO CALLS>
        final Map<String, List<Vehicles>> dealerIdToVehicle = new HashMap<>();
        for (final Vehicles v : vehicleList) {
            final String dealerId = v.getDealerId();
            dealerIdToVehicle.computeIfAbsent(dealerId, k -> new ArrayList<>()).add(v);
        }

        // Get Dealer Name and create answer <SLOW BC of Dealer name>
        final AddDealer addDealer = new AddDealer();
        final List<Callable<DealerWorker>> dealerWorkerCallableList = new ArrayList<>();
        for (final Map.Entry<String, List<Vehicles>> entry : dealerIdToVehicle.entrySet()) {
            final DealerWorker newD = new DealerWorker(restTemplate, dataSetId, entry.getKey());
            dealerWorkerCallableList.add(newD);
        }

        executor.invokeAll(dealerWorkerCallableList);
        executor.shutdown();

        for (final Callable<DealerWorker> dW : dealerWorkerCallableList) {
            final Dealers dealer = ((DealerWorker) dW).getDealer();
            dealer.setVehicles(dealerIdToVehicle.get(dealer.getDealerId()));
            addDealer.addDealer(dealer);
        }

        // Submit answer and get results
        final String completeRez = restHelper().submitAnswer(dataSetId, g.toJson(addDealer));
        LOGGER.info("Answer: " + completeRez);

        return completeRez;
    }
}
