package com.mrgrd56.httpclient.response;

import com.mrgrd56.httpclient.entity.HttpEntityFormatter;
import com.mrgrd56.httpclient.entity.HttpHeaders;

public class HttpResponseEntity {
    private static final HttpEntityFormatter httpEntityFormatter = new HttpEntityFormatter();

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
        return String.format("%s %s %s\r\n%s\r\n\r\n%s",
                protocol,
                statusCode,
                statusMessage,
                headers,
                httpEntityFormatter.formatBody(body));
    }
}
