package be.orbinson.osgi.opentelemetry.it;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenTelemetryIT {

    private static final int SLING_PORT = Integer.getInteger("HTTP_PORT", 8080);

    @Test
    public void testOsgi() throws InterruptedException {
        System.out.println("http://localhost:" + SLING_PORT + "/system/console");
        if (System.getProperty("debug.osgi") != null) {
            Thread.sleep(1000000);
        }
    }

    @Test
    public void testOpenTelemetryBundlesActive() throws Exception {
        String url = "http://localhost:" + SLING_PORT + "/system/console/bundles.json";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-api')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-api-incubator')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-context')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-exporter-common')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-exporter-logging')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-exporter-otlp')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-exporter-otlp-common')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-exporter-sender-okhttp')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk-common')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk-extension-autoconfigure')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk-extension-autoconfigure-spi')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk-extension-incubator')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk-logs')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk-metrics')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-sdk-trace')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-instrumentation-annotations')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-instrumentation-api')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='opentelemetry-logback-appender-1.0')]")).get(0).get("state"));
        assertEquals("Active", ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='semantic-conventions-java')]")).get(0).get("state"));
    }

    @Test
    public void testOpenTelemetrySDK() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        createSpan(client);
        createSpan(client);
        createSpan(client);

        createMetric(client);

        createLog(client);
        createLog(client);

        String responseBody = getExportResult(client);
        assertEquals("3", JsonPath.read(responseBody, "$.spans"));
        assertEquals("1", JsonPath.read(responseBody, "$.metrics"));
        assertEquals("2", JsonPath.read(responseBody, "$.logs"));
    }

    private static String getExportResult(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SLING_PORT + "/open-telemetry?type=export"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private static void createSpan(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SLING_PORT + "/open-telemetry?type=spans"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void createMetric(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SLING_PORT + "/open-telemetry?type=metrics"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void createLog(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + SLING_PORT + "/open-telemetry?type=logs"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
