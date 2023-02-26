package com.mrgrd56.httpclient.response;

import com.mrgrd56.httpclient.entity.HttpHeaders;

public class HttpResponseEntityBuilder {
    private String protocol;
    private int statusCode;
    private String statusMessage;
    private HttpHeaders headers;
    private byte[] body;

    public HttpResponseEntityBuilder setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public HttpResponseEntityBuilder setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponseEntityBuilder setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public HttpResponseEntityBuilder setHeaders(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponseEntityBuilder setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public HttpResponseEntity build() {
        return new HttpResponseEntity(protocol, statusCode, statusMessage, headers, body);
    }
}