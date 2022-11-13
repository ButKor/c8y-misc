package reference.sdk;

import reference.sdk.util.RequestMethod;

import java.util.Map;

public class RequestBuilder {

    public static Request buildGetRequest(String endpointUrl, Map<String, String> headers){
        return new Request(endpointUrl, RequestMethod.GET, headers, null, null);
    }

    public static Request buildPostRequest(String endpointUrl, Map<String, String> headers, String body){
        return new Request(endpointUrl, RequestMethod.POST_BODY, headers, null, body);
    }

    public static Request buildPostFieldsRequest(String endpointUrl, Map<String, String> headers, Map<String, Object> fields){
        return new Request(endpointUrl, RequestMethod.POST_FIELDS, headers, fields, null);
    }

    public static Request buildPutRequest(String endpointUrl, Map<String, String> headers, String body){
        return new Request(endpointUrl, RequestMethod.PUT, headers, null, body);
    }

    public static Request buildDeleteRequest(String endpointUrl, Map<String, String> headers){
        return new Request(endpointUrl, RequestMethod.DELETE, headers, null, null);
    }

}
