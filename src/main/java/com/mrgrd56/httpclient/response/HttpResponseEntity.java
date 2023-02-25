package com.mrgrd56.httpclient.response;

import java.util.List;
import java.util.Map;

public class HttpResponseEntity {
    private final String protocol;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    public HttpResponseEntity(
            String protocol,
            int statusCode,
            String statusMessage,
            Map<String, List<String>> headers,
            byte[] body) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
