package com.mrgrd56.httpclient.entity;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class HttpEntityFormatter {
    public String getUrlPath(URL url) {
        var path = url.getPath();
        if (path == null || path.length() == 0) {
            return "/";
        }

        return path;
    }

    public String formatBody(byte[] body) {
        if (body == null) {
            return "";
        }

        if (isText(body, StandardCharsets.UTF_8)) {
            return new String(body, StandardCharsets.UTF_8);
        }

        return "<binary data>";
    }

    private boolean isText(byte[] data, Charset charset) {
        CharsetDecoder decoder = charset.newDecoder();
        try {
            decoder.decode(ByteBuffer.wrap(data));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }
}
