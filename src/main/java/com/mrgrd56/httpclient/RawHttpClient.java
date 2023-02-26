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

        try (var scanner = new Scanner(input, CHARSET).useDelimiter("\r\n\r\n")) {
            String startingLine = scanner.nextLine();
            responseParser.parseStartingLine(startingLine, responseBuilder);

            var headers = responseParser.parseHeaders(readRawHeaders(scanner));

            byte[] body = readBody(headers);
            if (body.length == 0) {
                body = null;
            }

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

// FIXME sometimes more bytes from the InputStream may be read than needed
//  and the `body` becomes empty
//  the following dialog with ChatGPT might help:

/*

I have an InputStream. How to properly read it? I want to read part until "\r\n\r\n" sequence as text and read the rest part as byte array. Maybe use Scanner for that.

You can use a Scanner with a specified delimiter to read the text data up until the "\r\n\r\n" sequence. Then, you can use a ByteArrayOutputStream to read the rest of the data into a byte array.

Here's an example code snippet that shows how to accomplish this:

java
Copy code
InputStream inputStream = ...; // your input stream
Scanner scanner = new Scanner(inputStream).useDelimiter("\\r\\n\\r\\n");

// Read text data until "\r\n\r\n" sequence
String textData = scanner.next();

// Read the rest of the data into a byte array
ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
byte[] buffer = new byte[1024];
int bytesRead;
while ((bytesRead = inputStream.read(buffer)) != -1) {
    byteArrayOutputStream.write(buffer, 0, bytesRead);
}
byte[] binaryData = byteArrayOutputStream.toByteArray();

// Use textData and binaryData as needed
Note that this code assumes that the "\r\n\r\n" sequence appears in the input stream at least once. If it doesn't, the Scanner will block until more data is available.




MRGRD56
But inputStream.read returns -1. However, I know that there is more data after "\r\n\r\n"!
When the InputStream.read() method returns -1, it means that the end of the stream has been reached. In the case of reading an HTTP response, this can happen if the entire response fits into the buffer provided to the InputStream.read() method.

To read beyond the end of the initial buffer, you can use a loop to repeatedly read from the input stream and append the results to a StringBuilder or ByteArrayOutputStream until you have read all the data you need. Here's an example implementation that reads until the "\r\n\r\n" sequence and then reads the rest of the response as a byte array:

scss
Copy code
InputStream inputStream = ...; // obtain input stream

ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
StringBuilder stringBuilder = new StringBuilder();

int b;
while ((b = inputStream.read()) != -1) {
    byteArrayOutputStream.write(b);
    stringBuilder.append((char) b);

    if (stringBuilder.length() >= 4 &&
        stringBuilder.substring(stringBuilder.length() - 4).equals("\r\n\r\n")) {
        break;
    }
}

byte[] remainingBytes = new byte[inputStream.available()];
inputStream.read(remainingBytes);

// use stringBuilder.toString() for the text part of the response
// use byteArrayOutputStream.toByteArray() for the binary part of the response
This implementation reads bytes one at a time, which can be slow. To improve performance, you can read larger chunks of data at a time by using the InputStream.read(byte[] b) method instead.




MRGRD56
But if I read by chunks, more bytes in InputStream can be read than I need. And I won't get then while getting the remaining bytes.
You can use a ByteArrayOutputStream to read the remaining bytes from the InputStream. Here's an example:

java
Copy code
ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

byte[] buffer = new byte[1024];
int bytesRead;

while ((bytesRead = inputStream.read(buffer)) != -1) {
    String chunk = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);

    int endOfHeaders = chunk.indexOf("\r\n\r\n");

    if (endOfHeaders != -1) {
        String headers = chunk.substring(0, endOfHeaders);
        // process headers

        // write remaining bytes to ByteArrayOutputStream
        byteArrayOutputStream.write(buffer, endOfHeaders + 4, bytesRead - endOfHeaders - 4);
        break;
    } else {
        // write chunk to ByteArrayOutputStream
        byteArrayOutputStream.write(buffer, 0, bytesRead);
    }
}

// read remaining bytes from ByteArrayOutputStream
byte[] remainingBytes = byteArrayOutputStream.toByteArray();
In this example, we use the ByteArrayOutputStream to collect the remaining bytes after the end of headers. We write the bytes to the ByteArrayOutputStream as we read them from the InputStream, and then use the toByteArray method to get the remaining bytes.

 */