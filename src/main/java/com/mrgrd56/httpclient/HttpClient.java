package com.mrgrd56.httpclient;

import com.mrgrd56.httpclient.entity.HttpEntityFormatter;
import com.mrgrd56.httpclient.entity.HttpHeaders;
import com.mrgrd56.httpclient.request.HttpRequestEntity;
import com.mrgrd56.httpclient.response.HttpResponseEntity;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpClient {
    private static final HttpEntityFormatter httpEntityFormatter = new HttpEntityFormatter();

    public HttpResponseEntity request(HttpRequestEntity request) {
        URL url = request.getUrl();

        SocketFactory socketFactory = getSocketFactory(url);
        String host = getHost(url);
        int port = getPort(url);
        String path = httpEntityFormatter.getUrlPath(url);

        try (var rawHttpClient = new RawHttpClient(socketFactory, host, port)) {
            HttpHeaders headers = request.getHeaders();

            byte[] requestHead = String.format("%s %s %s\n%s\n\n", request.getMethod().name(), path, request.getProtocol(), headers).getBytes(StandardCharsets.UTF_8);

            ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
            requestStream.write(requestHead);
            if (request.getBody() != null) {
                requestStream.write(request.getBody());
            }

            byte[] requestBytes = requestStream.toByteArray();

            return rawHttpClient.request(requestBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SocketFactory getSocketFactory(URL url) {
        var protocol = url.getProtocol();
        return switch (protocol) {
            case "http" -> SocketFactory.getDefault();
            case "https" -> SSLSocketFactory.getDefault();
            default -> throw new IllegalArgumentException("Invalid protocol");
        };
    }

    private String getHost(URL url) {
        return Objects.requireNonNull(url.getHost());
    }

    private int getPort(URL url) {
        if (url.getPort() != -1) {
            return url.getPort();
        }

        return url.getDefaultPort();
    }
}
