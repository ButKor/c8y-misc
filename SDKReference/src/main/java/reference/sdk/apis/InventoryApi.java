package reference.sdk.apis;

import com.mashape.unirest.http.HttpResponse;
import reference.sdk.C8YClient;
import reference.sdk.Request;
import reference.sdk.RequestBuilder;
import reference.sdk.C8yApiException;
import reference.sdk.C8YApi;

import java.util.HashMap;
import java.util.Map;

public class InventoryApi {

    private final C8YClient apiClient;

    public InventoryApi(C8YClient apiClient) {
        this.apiClient = apiClient;
    }

    public String getManagedObjectsByType(String type) throws C8yApiException {
        String endpointUrl = String.format("/inventory/managedObjects?type=%s", type);
        Map<String, String> headers = new HashMap<>(); // Authorization is always set by the apiClient
        Request request = RequestBuilder.buildGetRequest(endpointUrl, headers);
        HttpResponse<String> response = apiClient.execApiCall(request);
        if (response.getStatus() != C8YApi.SUCCESS_GET_OBJECT_STATUS) {
            throw new C8yApiException(
                    "Received invalid status code " + response.getStatus() + " while requesting Managed Object for type " + type,
                    "Request: " + request);
        }
        // cast string response, loop through pages, etc...
        return response.getBody();
    }
}
