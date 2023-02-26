package com.mrgrd56;

import com.mrgrd56.httpclient.HttpClient;
import com.mrgrd56.httpclient.RawHttpClient;
import com.mrgrd56.httpclient.request.HttpRequestEntity;
import com.mrgrd56.httpclient.response.HttpResponseEntity;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

public class Program {
    public static void main(String[] args) throws Exception {
//        performRequest(HttpRequestEntity.get("https://stackoverflow.com/questions/605696/get-file-name-from-url"));
//        performRequest(HttpRequestEntity.get("https://styles.redditmedia.com/t5_3jtm62/styles/communityIcon_zdd7b6gax1aa1.png"));
        performRequest(HttpRequestEntity.get()
                .url("http://localhost:9999")
                .protocol("HTTP/1.1")
                .header("Accept", "text/html,*/*")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")
                .build()
        );
    }

    private static void performRequest(HttpRequestEntity request) throws IOException {
        HttpClient httpClient = new HttpClient();

        System.out.println("-----------\n" + request + "\n-----------");

        HttpResponseEntity response = httpClient.request(request);

        String uniqueName = Objects.toString(Instant.now().toEpochMilli());
        String fileName = new File(request.getUrl().getPath()).getName();
        if (fileName.length() > 0) {
            uniqueName += "__";
        }

        if (response.getBody() != null) {
            Files.write(Path.of("C:\\Users\\SU\\mg56_requests\\" + uniqueName + fileName), response.getBody());
        }

        System.out.println(response + "\n-----------\n");
    }

    @Deprecated
    private static void performRawRequest() throws IOException {
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