package com.mrgrd56.httpclient;

import com.mrgrd56.httpclient.entity.HttpHeaders;
import com.mrgrd56.httpclient.response.HttpResponseEntity;
import com.mrgrd56.httpclient.response.HttpResponseEntityBuilder;
import com.mrgrd56.httpclient.response.HttpResponseParser;

import javax.net.SocketFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RawHttpClient implements Closeable {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final HttpResponseParser responseParser = new HttpResponseParser();

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public RawHttpClient(SocketFactory socketFactory, String host, int port) throws IOException {
        this.socket = socketFactory.createSocket(host, port);
        this.input = this.socket.getInputStream();
        this.output = this.socket.getOutputStream();
    }

    public HttpResponseEntity request(byte[] request) {
        sendBytes(request);
        return receiveResponse();
    }

    public HttpResponseEntity request(String request) {
        return request(stringToBytes(request));
    }

    private void sendBytes(byte[] bytes) {
        try {
            output.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponseEntity receiveResponse() {
        var responseBuilder = new HttpResponseEntityBuilder();

        try (var scanner = new Scanner(input, CHARSET)) {
            String startingLine = scanner.nextLine();
            responseParser.parseStartingLine(startingLine, responseBuilder);

            var headers = responseParser.parseHeaders(readRawHeaders(scanner));

            byte[] body = readBody(headers);

            return responseBuilder
                    .setHeaders(headers)
                    .setBody(body)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readRawHeaders(Scanner scanner) {
        List<String> headers = new ArrayList<>();

        String line;
        while (!"".equals(line = scanner.nextLine())) {
            headers.add(line);
        }

        return headers;
    }

    private byte[] readBody(HttpHeaders headers) throws IOException {
        Integer contentLength = headers.getContentLength();

        if (contentLength == null) {
            return input.readAllBytes();
        } else {
            return input.readNBytes(contentLength);
        }
    }

    private byte[] stringToBytes(String string) {
        return string.getBytes(CHARSET);
    }

    @Override
    public void close() throws IOException {
        try {
            input.close();
            output.close();
        } finally {
            socket.close();
        }
    }
}
