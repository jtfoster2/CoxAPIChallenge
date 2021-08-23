package cox.api.challenge.data_workers;

import cox.api.challenge.data_interfaces.Dealers;
import cox.api.challenge.rest_utility.EndpointVars;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static cox.api.challenge.rest_utility.EndpointVars.DEALER_DETAIL_API;

public class DealerWorker implements Callable<DealerWorker> {
    private final String dataSetId;
    private final String dealerId;
    private final RestTemplate restTemplate;
    private Dealers dealer;

    public DealerWorker(final RestTemplate restTemplate, final String dataSetId, final String dealerId) {
        this.dataSetId = dataSetId;
        this.dealerId = dealerId;
        this.restTemplate = restTemplate;
    }

    public Dealers getDealer() {
        return dealer;
    }

    @Override
    public DealerWorker call() throws Exception {
        final Map<String, String> paramsDeaker = new HashMap<>();
        paramsDeaker.put(EndpointVars.Paths.DATASET_ID, dataSetId);
        paramsDeaker.put(EndpointVars.Paths.DEALER_ID, dealerId);
        this.dealer = restTemplate.getForEntity(DEALER_DETAIL_API, Dealers.class, paramsDeaker).getBody();
        return null;
    }
}
