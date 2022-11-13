package reference.sdk.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mashape.unirest.http.HttpResponse;
import reference.sdk.C8YApi;
import reference.sdk.C8YClient;
import reference.sdk.Request;
import reference.sdk.RequestBuilder;
import reference.sdk.model.Event;
import reference.sdk.model.Source;
import reference.sdk.C8yApiException;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class EventApi {

    private final C8YClient apiClient;
    private final ObjectMapper basicMapper = new ObjectMapper();

    public EventApi(C8YClient apiClient) {
        this.apiClient = apiClient;
    }

    public String createEvent(String source, String type, String text, String time) throws C8yApiException {
        try {
            // Setup Request (Authorization is in scope of apiClient)
            String endpointUrl = "/event/events";
            Map<String, String> headers = Map.ofEntries(
                    entry("Content-Type", "application/json"),
                    entry("Accept", "application/json")
            );
            Map<String, String> m = new HashMap<>(headers);

            // create event and convert to (json-)string
            Event event = new Event(new Source(source), type, text, time);
            ObjectWriter ow = basicMapper.writer().withDefaultPrettyPrinter();
            String body = ow.writeValueAsString(event);

            // send request via apiClient ...
            Request request = RequestBuilder.buildPostRequest(endpointUrl, m, body);
            HttpResponse<String> response = apiClient.execApiCall(request);
            if (response.getStatus() != C8YApi.SUCCESS_CREATE_OBJECT_STATUS) {
                throw new C8yApiException(
                        "Received invalid status code " + response.getStatus() + " while creating event",
                        "Request: " + request);
            }

            // now cast the response and return ID
            HashMap<String, Object> castedResponse = basicMapper.readValue(response.getBody(), HashMap.class);
            String res = (String) castedResponse.get("id");
            return res;
        } catch (JsonProcessingException e) {
            throw new C8yApiException("Event Object cannot be cast to JSON String", e.getMessage());
        }
    }
}
