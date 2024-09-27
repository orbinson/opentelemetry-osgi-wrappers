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
        assertEquals("Active", getState(responseBody, "opentelemetry-api"));
        assertEquals("Active", getState(responseBody, "opentelemetry-api-incubator"));
        assertEquals("Active", getState(responseBody, "opentelemetry-context"));
        assertEquals("Active", getState(responseBody, "opentelemetry-exporter-common"));
        assertEquals("Active", getState(responseBody, "opentelemetry-exporter-logging"));
        assertEquals("Active", getState(responseBody, "opentelemetry-exporter-otlp"));
        assertEquals("Active", getState(responseBody, "opentelemetry-exporter-otlp-common"));
        assertEquals("Active", getState(responseBody, "opentelemetry-exporter-sender-okhttp"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk-common"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk-extension-autoconfigure"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk-extension-autoconfigure-spi"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk-extension-incubator"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk-logs"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk-metrics"));
        assertEquals("Active", getState(responseBody, "opentelemetry-sdk-trace"));
        assertEquals("Active", getState(responseBody, "opentelemetry-instrumentation-annotations"));
        assertEquals("Active", getState(responseBody, "opentelemetry-instrumentation-api"));
        assertEquals("Active", getState(responseBody, "opentelemetry-logback-appender-1.0"));
        assertEquals("Active", getState(responseBody, "semantic-conventions-java"));
    }

    private static String getState(String responseBody, String bundleName) {
        return (String) ((List<Map<String, Object>>) JsonPath.read(responseBody, "$.data[?(@.name=='" + bundleName + "')]")).get(0).get("state");
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
