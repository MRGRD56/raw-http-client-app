package com.mrgrd56.httpclient.entity;

import java.util.ArrayList;
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
        var headerEntries = getHeaderEntries(name);
        if (headerEntries == null) {
            return null;
        }

        return headerEntries.get(headerEntries.size() - 1);
    }

    public List<String> getHeaderEntries(String name) {
        return get(name.toLowerCase());
    }

    public void addHeader(String name, String value) {
        this.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>())
                .add(value);
    }

    public void setHeader(String name, String value) {
        var newValue = new ArrayList<String>();
        newValue.add(value);
        this.put(name.toLowerCase(), newValue);
    }

    public void removeHeader(String name) {
        remove(name.toLowerCase());
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
                .collect(Collectors.joining("\r\n"));
    }
}
