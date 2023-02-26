package com.mrgrd56;

import com.mrgrd56.httpclient.RawHttpClient;

import javax.net.ssl.SSLSocketFactory;
import java.nio.file.Files;
import java.nio.file.Path;

public class Program {
    public static void main(String[] args) throws Exception {
        try (var client = new RawHttpClient(SSLSocketFactory.getDefault(), "styles.redditmedia.com", 443)) {
            var response = client.request("""
                    GET /t5_3jtm62/styles/communityIcon_zdd7b6gax1aa1.png HTTP/1.2
                    Host: styles.redditmedia.com
                    
                    """);

            Files.write(Path.of("C:\\Users\\SU\\test_mrgrd56_pic.png"), response.getBody());

            System.out.println(response);
        }
    }
}