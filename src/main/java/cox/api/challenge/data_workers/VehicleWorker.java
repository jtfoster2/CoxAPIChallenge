package cox.api.challenge.data_workers;

import cox.api.challenge.rest_utility.EndpointVars;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static cox.api.challenge.rest_utility.EndpointVars.VEHICLE_INFO_API;

public class VehicleWorker implements Callable<VehicleWorker> {
    private final String dataSetId;
    private final String vehicleid;
    private final RestTemplate restTemplate;
    private String vehicleInfo;

    public VehicleWorker(final RestTemplate restTemplate, final String dataSetId, final String vehicleid) {
        this.dataSetId = dataSetId;
        this.vehicleid = vehicleid;
        this.restTemplate = restTemplate;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    @Override
    public VehicleWorker call() {

        final Map<String, String> paramsForVechInfo = new HashMap<>();
        paramsForVechInfo.put(EndpointVars.Paths.DATASET_ID, dataSetId);
        paramsForVechInfo.put(EndpointVars.Paths.VEHICLE_ID, vehicleid);
        this.vehicleInfo = restTemplate.getForObject(VEHICLE_INFO_API, String.class, paramsForVechInfo);
        return null;
    }
}
