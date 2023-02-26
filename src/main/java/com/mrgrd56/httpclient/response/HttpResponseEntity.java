package com.mrgrd56.httpclient.response;

import com.mrgrd56.httpclient.entity.HttpHeaders;

import java.nio.charset.StandardCharsets;

public class HttpResponseEntity {
    private final String protocol;
    private final int statusCode;
    private final String statusMessage;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpResponseEntity(
            String protocol,
            int statusCode,
            String statusMessage,
            HttpHeaders headers,
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

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s\n%s\n\n%s",
                protocol,
                statusCode,
                statusMessage,
                headers,
                new String(body, StandardCharsets.UTF_8));
    }
}
