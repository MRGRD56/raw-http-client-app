package com.mrgrd56;

import com.mrgrd56.httpclient.RawHttpClient;

import java.nio.charset.StandardCharsets;

public class Program {
    public static void main(String[] args) throws Exception {
        try (var client = new RawHttpClient("crudcrud.com", 80)) {
            var response = client.request("""
                    GET /api/75ec2079dc05476681d2e5726ead626e/people HTTP/2
                    Host: crudcrud.com
                    
                    """);

            String responseBody = new String(response.getBody(), StandardCharsets.UTF_8);

            System.out.println(response);
        }
    }
}