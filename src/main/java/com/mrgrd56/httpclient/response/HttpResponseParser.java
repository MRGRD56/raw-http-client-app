package com.mrgrd56.httpclient.response;

import com.google.common.primitives.Bytes;
import com.mrgrd56.httpclient.entity.HttpHeaders;
import org.apache.commons.lang.NotImplementedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class HttpResponseParser {
    private static final String HEADERS_SEPARATOR = "\r\n\r\n";

    public HttpResponseEntity parseResponse(InputStream input, Charset charset) {
        try {
            var responseBuilder = new HttpResponseEntityBuilder();

            var headStream = new ByteArrayOutputStream();
            var bodyStream = new ByteArrayOutputStream();

            boolean isHeadRead = false;
            Integer contentLength = null;

            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = input.read(buffer)) != -1) {
                if (isHeadRead) {
                    bodyStream.write(buffer, 0, bytesRead);

                    if (contentLength != null && bodyStream.size() >= contentLength) {
                        break;
                    }
                } else {
                    headStream.write(buffer, 0, bytesRead);

                    byte[] headBytes = headStream.toByteArray();
                    int headersEndIndex = Bytes.indexOf(headBytes, HEADERS_SEPARATOR.getBytes(charset));

                    if (headersEndIndex != -1) {
                        byte[] actualHeadBytes = Arrays.copyOfRange(headBytes, 0, headersEndIndex);
                        String head = new String(actualHeadBytes, charset);
                        List<String> headLines = Arrays.asList(head.split("\r?\n"));
                        parseStartingLine(headLines.get(0), responseBuilder);
                        HttpHeaders headers = parseHeaders(headLines.subList(1, headLines.size()));
                        contentLength = headers.getContentLength();
                        responseBuilder.setHeaders(headers);

                        isHeadRead = true;

                        int bodyBeginningIndex = headersEndIndex + HEADERS_SEPARATOR.length();
                        if (bodyBeginningIndex <= headBytes.length - 1) {
                            byte[] particularBodyBytes = Arrays.copyOfRange(headBytes, bodyBeginningIndex, headBytes.length);
                            bodyStream.write(particularBodyBytes);
                        }

                        if (contentLength != null && bodyStream.size() >= contentLength) {
                            break;
                        }
                    }
                }
            }

            return responseBuilder
                    .setBody(bodyStream.toByteArray())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseStartingLine(String startingLine, HttpResponseEntityBuilder responseBuilder) {
        String[] parts = startingLine.split("\\s", 3);
        String protocol = parts[0];
        int statusCode = Integer.parseInt(parts[1]);
        String statusMessage = parts.length >= 3 ? parts[2] : null;

        responseBuilder.setProtocol(protocol);
        responseBuilder.setStatusCode(statusCode);
        responseBuilder.setStatusMessage(statusMessage);
    }

    private HttpHeaders parseHeaders(List<String> headers) {
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
