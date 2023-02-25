package com.mrgrd56.httpclient;

import com.mrgrd56.httpclient.response.HttpResponseEntity;
import com.mrgrd56.httpclient.response.HttpResponseParser;
import com.mrgrd56.httpclient.response.RawHttpEntity;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawHttpClient implements Closeable {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final HttpResponseParser responseParser = new HttpResponseParser();

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public RawHttpClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.input = this.socket.getInputStream();
        this.output = this.socket.getOutputStream();
    }

    public RawHttpEntity requestRaw(byte[] request) {
        sendBytes(request);
        return receiveResponse();
    }

    public RawHttpEntity requestRaw(String request) {
        return requestRaw(stringToBytes(request));
    }

    public HttpResponseEntity request(byte[] request) {
        return responseParser.parse(requestRaw(request));
    }

    public HttpResponseEntity request(String request) {
        return responseParser.parse(requestRaw(request));
    }

    private void sendBytes(byte[] bytes) {
        try {
            output.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RawHttpEntity receiveResponse() {
        try (var reader = new BufferedReader(new InputStreamReader(input, CHARSET))) {
            String startingLine = reader.readLine();
            List<String> headers = new ArrayList<>();

            String line;
            while (!"".equals(line = reader.readLine())) {
                headers.add(line);
            }

            byte[] body = readBody(reader);

            return new RawHttpEntity(startingLine, headers, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readBody(BufferedReader reader) throws IOException {
        List<byte[]> byteBuffers = new ArrayList<>();

        CharBuffer charBuffer = CharBuffer.allocate(8092);

        int totalBytesRead = 0;
        while (reader.read(charBuffer) != -1) {
            charBuffer.flip();
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
            byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
            byteBuffers.add(bytes);
            totalBytesRead += bytes.length;

            charBuffer = CharBuffer.allocate(8092);
        }

        byte[] body = new byte[totalBytesRead];

        int position = 0;
        for (var buffer : byteBuffers) {
            int bufferSize = buffer.length;

            System.arraycopy(buffer, 0, body, position, bufferSize);

            position += bufferSize;
        }

        return body;
    }

    private byte[] stringToBytes(String string) {
        return string.getBytes(CHARSET);
    }

    private String bytesToString(byte[] bytes) {
        return new String(bytes, CHARSET);
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
