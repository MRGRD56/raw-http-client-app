package com.mrgrd56.httpclient.entity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders extends LinkedHashMap<String, List<String>> {
    public HttpHeaders() {
    }

    public HttpHeaders(Map<? extends String, ? extends List<String>> m) {
        super(m);
    }

    public String getHeader(String name) {
        var headerEntries = get(name.toLowerCase());
        return headerEntries.get(headerEntries.size() - 1);
    }

    public Integer getContentLength() {
        var rawContentLength = getHeader("Content-Length");

        if (rawContentLength == null) {
            return null;
        }

        return Integer.parseUnsignedInt(rawContentLength);
    }

    public String formatHeaderName(String headerName) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : headerName.toCharArray()) {
            if (c == '-') {
                sb.append('-');
                capitalizeNext = true;
            } else {
                sb.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
                capitalizeNext = false;
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.entrySet().stream()
                .flatMap(header -> {
                    return header.getValue().stream().map(headerValue -> {
                        return String.format("%s: %s", formatHeaderName(header.getKey()), headerValue);
                    });
                })
                .collect(Collectors.joining("\n"));
    }
}
