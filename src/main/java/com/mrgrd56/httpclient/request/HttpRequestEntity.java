package com.mrgrd56.httpclient.request;

import com.mrgrd56.httpclient.entity.HttpEntityFormatter;
import com.mrgrd56.httpclient.entity.HttpHeaders;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class HttpRequestEntity {
    private static final HttpEntityFormatter httpEntityFormatter = new HttpEntityFormatter();

    private final HttpMethod method;
    private final URL url;
    private final HttpHeaders headers;
    private final byte[] body;

    private String protocol = "HTTP/1.2";

    public HttpRequestEntity(HttpMethod method, String url, HttpHeaders headers, byte[] body) {
        this.method = Objects.requireNonNull(method);
        try {
            this.url = new URL(Objects.requireNonNull(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.headers = Objects.requireNonNullElseGet(headers, HttpHeaders::new);
        this.headers.setHeader("Host", this.url.getAuthority());
        this.body = body;
    }

    public HttpRequestEntity(HttpMethod method, String url) {
        this(method, url, null, null);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = Objects.requireNonNull(protocol);
    }

    public static HttpRequestEntityBuilder builder() {
        return new HttpRequestEntityBuilder();
    }

    public static HttpRequestEntityBuilder get() {
        return builder().method(HttpMethod.GET);
    }

    public static HttpRequestEntity get(String url) {
        return get().url(url).build();
    }

    public static HttpRequestEntityBuilder post() {
        return builder().method(HttpMethod.POST);
    }

    public static HttpRequestEntityBuilder put() {
        return builder().method(HttpMethod.PUT);
    }

    public static HttpRequestEntityBuilder patch() {
        return builder().method(HttpMethod.PATCH);
    }

    public static HttpRequestEntityBuilder delete() {
        return builder().method(HttpMethod.DELETE);
    }

    public static HttpRequestEntityBuilder head() {
        return builder().method(HttpMethod.HEAD);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s\r\n%s\r\n\r\n%s",
                method.name(),
                httpEntityFormatter.getUrlPath(url),
                protocol,
                headers,
                httpEntityFormatter.formatBody(body));
    }
}
