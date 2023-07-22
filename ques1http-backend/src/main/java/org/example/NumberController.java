package org.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

@RestController
public class NumberController {

    private static final int TIMEOUT_MS = 500;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final CloseableHttpClient httpClient;


    public NumberController() {
        RequestConfig requestConfig = RequestConfig.custom()

                .setConnectTimeout(TIMEOUT_MS)

                .setConnectionRequestTimeout(TIMEOUT_MS)
                .setSocketTimeout(TIMEOUT_MS)
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(5);
        connManager.setMaxTotal(10);

        this.httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @GetMapping("/numbers")
    public ResponseEntity<Map<String, List<Integer>>> getNumbers(@RequestParam("uri") List<String> urls) {

        Map<String, List<Integer>> result = new HashMap<>();

        for (String url : urls) {
            List<Integer> numbers = fetchNumbersFromUrl(url);
            result.put(url, numbers);
        }

        return ResponseEntity.ok(result);
    }

    private List<Integer> fetchNumbersFromUrl(String url) {

        List<Integer> numbers = new ArrayList<>();

        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                HttpEntity entity = response.getEntity();
                JsonNode jsonNode = objectMapper.readTree(entity.getContent());
                if (jsonNode.has("numbers")) {
                    for (JsonNode numberNode : jsonNode.get("numbers")) {
                        numbers.add(numberNode.asInt());
                    }
                }
            }
        } catch (IOException e) {
            // Ignore and move to the next URL if an error occurs
        }

        return numbers;
    }
}
