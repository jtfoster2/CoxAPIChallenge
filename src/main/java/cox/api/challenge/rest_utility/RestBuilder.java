package cox.api.challenge.rest_utility;

import com.google.gson.Gson;
import cox.api.challenge.data_interfaces.DatasetIDs;
import cox.api.challenge.data_interfaces.VehicleIDs;
import lombok.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cox.api.challenge.rest_utility.EndpointVars.ANSWER_API;
import static cox.api.challenge.rest_utility.EndpointVars.DATA_SET_API;
import static cox.api.challenge.rest_utility.EndpointVars.VEHICLE_IDS_API;

public class RestBuilder {
    private final RestTemplate restTemplate;

    public RestBuilder(final RestTemplate template) {
        this.restTemplate = template;
    }

    public String submitAnswer(final String dataSetId, final String asnwer) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final Map<String, String> paramsForAnswer = new HashMap<>();
        paramsForAnswer.put(EndpointVars.Paths.DATASET_ID, dataSetId);

        final HttpEntity<String> entity = new HttpEntity<>(asnwer, headers);
        final String completeRez = restTemplate.exchange(ANSWER_API, HttpMethod.POST, entity, String.class, paramsForAnswer).getBody();

        return completeRez;
    }

    public VehicleIDs getVehicleIds(final String dataSetId) {
        final Gson g = new Gson();
        // Get Vehicle IDs for the data set <FAST>
        final Map<String, Object> params = new HashMap<>();
        params.put(EndpointVars.Paths.DATASET_ID, dataSetId);
        final String jsonVechIds = restTemplate.getForObject(VEHICLE_IDS_API, String.class, params);

        return g.fromJson(jsonVechIds, VehicleIDs.class);
    }

    public String getDataSetId() {
        return Objects.requireNonNull(restTemplate.getForObject(DATA_SET_API, DatasetIDs.class)).getDataSetId();
    }

}
