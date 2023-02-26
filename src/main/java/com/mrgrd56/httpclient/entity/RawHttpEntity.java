package com.mrgrd56.httpclient.entity;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RawHttpEntity {
    private final String startingLine;
    private final List<String> headers;
    private final byte[] body;

    public RawHttpEntity(String startingLine, List<String> headers, byte[] body) {
        this.startingLine = startingLine;
        this.headers = headers;
        this.body = body;
    }

    public String getStartingLine() {
        return startingLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
