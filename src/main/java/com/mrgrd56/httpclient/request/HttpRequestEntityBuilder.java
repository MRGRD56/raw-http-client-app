package com.mrgrd56.httpclient.request;

import com.mrgrd56.httpclient.entity.HttpHeaders;

import java.nio.charset.StandardCharsets;

public class HttpRequestEntityBuilder {
    private HttpMethod method;
    private String url;
    private HttpHeaders headers = null;
    private byte[] body = null;
    private String protocol = null;

    HttpRequestEntityBuilder() {
    }

    public HttpRequestEntityBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestEntityBuilder url(String url) {
        this.url = url;
        return this;
    }

    public HttpRequestEntityBuilder headers(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestEntityBuilder header(String name, String value) {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
        }
        this.headers.addHeader(name, value);
        return this;
    }

    public HttpRequestEntityBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public HttpRequestEntityBuilder body(byte[] body) {
        this.body = body;
        return this;
    }

    public HttpRequestEntityBuilder body(String body) {
        return body(body.getBytes(StandardCharsets.UTF_8));
    }

    public HttpRequestEntity build() {
        var result = new HttpRequestEntity(method, url, headers, body);
        if (protocol != null) {
            result.setProtocol(protocol);
        }

        return result;
    }
}