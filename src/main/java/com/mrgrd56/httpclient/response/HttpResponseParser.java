package com.mrgrd56.httpclient.response;

import com.mrgrd56.httpclient.entity.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponseParser {
    public void parseStartingLine(String startingLine, HttpResponseEntityBuilder responseBuilder) {
        String[] parts = startingLine.split("\\s", 3);
        String protocol = parts[0];
        int statusCode = Integer.parseInt(parts[1]);
        String statusMessage = parts.length >= 3 ? parts[2] : null;

        responseBuilder.setProtocol(protocol);
        responseBuilder.setStatusCode(statusCode);
        responseBuilder.setStatusMessage(statusMessage);
    }

    public HttpHeaders parseHeaders(List<String> headers) {
        HttpHeaders result = new HttpHeaders();

        for (var rawHeader : headers) {
            var header = parseHeader(rawHeader);
            result.computeIfAbsent(header.getKey().toLowerCase(), (k) -> new ArrayList<>()).addAll(header.getValue());
        }

        return result;
    }

    private Map.Entry<String, List<String>> parseHeader(String header) {
        String[] parts = header.split(": ", 2);
        return Map.entry(parts[0], List.of(parts[1]));
    }
}
