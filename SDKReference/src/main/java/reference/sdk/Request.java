package reference.sdk;

import reference.sdk.util.RequestMethod;

import java.util.Map;

public class Request {
    private String endpointUrl;
    private RequestMethod method;
    private Map<String, String> headers;
    private Map<String, Object> fields;
    private String body;

    public Request(String endpointUrl, RequestMethod method, Map<String, String> headers, Map<String, Object> fields, String body) {
        this.endpointUrl = endpointUrl;
        this.method = method;
        this.headers = headers;
        this.fields = fields;
        this.body = body;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public String getBody() {
        return body;
    }
}
