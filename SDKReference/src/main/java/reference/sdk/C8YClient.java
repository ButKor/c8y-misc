package reference.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import reference.sdk.util.AuthMethod;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class C8YClient {

    private final AuthMethod authMethod;
    private String authString = "";
    // these you have from your env vars
    private final String baseUrl;
    private final String tenant;
    private final String user;
    private final String pass;

    public C8YClient(AuthMethod authMethod) throws C8yApiException {
        this.authMethod = authMethod;
        // extract Tenant Info and User Credentials from env vars + request and store OAI Token
        this.baseUrl = System.getenv("C8Y_BASEURL");
        this.tenant = System.getenv("C8Y_TENANT");
        this.user = System.getenv("C8Y_USER");
        this.pass = System.getenv("C8Y_PASS");
        initAuthString();
    }

    private void initAuthString() throws C8yApiException {
        switch (authMethod) {
            case BASIC:
                authString = Base64.getEncoder()
                        .encodeToString((this.tenant + "/" + this.user + ":" + this.pass).getBytes());
                break;
            case OAUTH:
                authString = requestOAuthToken();
                break;
        }
    }


    private String requestOAuthToken() throws C8yApiException {
        String authString = Base64.getEncoder()
                .encodeToString((this.tenant + "/" + this.user + ":" + this.pass).getBytes());
        Map<String, String> headers = Map.ofEntries(
                entry("Authorization", "Basic " + authString),
                entry("Content-Type", "application/x-www-form-urlencoded"),
                entry("Accept", "application/json")
        );
        Map<String, Object> fields = Map.ofEntries(
                entry("grant_type", "PASSWORD"),
                entry("username", user),
                entry("password", pass)
        );
        try {
            HttpResponse<String> response = execRestCall(
                    RequestBuilder.buildPostFieldsRequest("/tenant/oauth/token", headers, fields));
            if (response.getStatus() != 200) {
                throw new C8yApiException(
                        "Received invalid status code '" + response.getStatus() + "' while requesting OAuth token");
            }
            Map<String, String> castedResponse = new ObjectMapper().readValue(response.getBody(), HashMap.class);
            return castedResponse.get("access_token");
        } catch (JsonProcessingException e) {
            throw new C8yApiException(
                    "Error while casting OAuth Server response towards a HashMap", e.getMessage());
        }
    }


    public HttpResponse<String> execApiCall(Request request) throws C8yApiException, IllegalArgumentException {
        switch (authMethod) {
            case BASIC:
                request.getHeaders().put("Authorization", "Basic " + this.authString);
                return execRestCall(request);
            case OAUTH:
                request.getHeaders().put("Authorization", "Bearer " + this.authString);
                HttpResponse<String> response = execRestCall(request);
                if (response.getStatus() == 401) {
                    // 1. refresh current token (note it's a class variable, thus will be used for all further calls)
                    // 2. replace in current request
                    // 3. retry the original request
                    this.authString = requestOAuthToken();
                    request.getHeaders().put("Authorization", "Bearer " + authString);
                    response = execRestCall(request);
                }
                return response;
        }
        throw new IllegalArgumentException("Auth Method '" + authMethod + "' not supported by API Client");
    }

    private HttpResponse<String> execRestCall(Request request) throws IllegalArgumentException {
        try {
            String fullUrl = StringUtils.removeEnd(this.baseUrl, "/") + "/" + StringUtils.removeStart(
                    request.getEndpointUrl(), "/");
            switch (request.getMethod()) {
                case GET:
                    return Unirest.get(fullUrl)
                            .headers(request.getHeaders())
                            .asString();
                case POST_FIELDS:
                    return Unirest.post(fullUrl)
                            .headers(request.getHeaders())
                            .fields(request.getFields())
                            .asString();
                case POST_BODY:
                    return Unirest.post(fullUrl)
                            .headers(request.getHeaders())
                            .body(request.getBody())
                            .asString();
                case PUT:
                    // return Unirest.put(url) ...
                    break;
                case DELETE:
                    // return Unirest.delete(url) ...
                    break;
            }
            throw new IllegalArgumentException("HTTP Method '" + request.getMethod() + "' not supported by API Client");
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }


}
