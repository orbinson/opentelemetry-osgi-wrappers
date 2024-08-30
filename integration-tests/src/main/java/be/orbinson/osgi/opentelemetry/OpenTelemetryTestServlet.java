package be.orbinson.osgi.opentelemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.testing.exporter.InMemoryLogRecordExporter;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                "osgi.http.whiteboard.servlet.pattern=/open-telemetry",
                "osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=default)"
        }
)
public class OpenTelemetryTestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private OpenTelemetry openTelemetry;
    private InMemorySpanExporter spanExporter;
    private InMemoryMetricReader metricReader;
    private InMemoryLogRecordExporter logRecordExporter;


    @Activate
    public void activate() {
        spanExporter = InMemorySpanExporter.create();

        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
                        .build();

        metricReader = InMemoryMetricReader.create();

        SdkMeterProvider meterProvider =
                SdkMeterProvider.builder().registerMetricReader(metricReader).build();

        logRecordExporter = InMemoryLogRecordExporter.create();

        SdkLoggerProvider loggerProvider =
                SdkLoggerProvider.builder()
                        .addLogRecordProcessor(SimpleLogRecordProcessor.create(logRecordExporter))
                        .build();

        openTelemetry = OpenTelemetrySdk.builder()
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .setTracerProvider(tracerProvider)
                .setMeterProvider(meterProvider)
                .setLoggerProvider(loggerProvider)
                .build();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String type = req.getParameter("type");
        if (type == null) {
            resp.getWriter().println("unknown type");
        } else if (type.equals("metrics")) {
            openTelemetry.getMeter("integration-tests").counterBuilder("integration-tests").build().add(1);
            resp.getWriter().println("metered");
        } else if (type.equals("spans")) {
            Span span = openTelemetry.getTracer("integration-tests").spanBuilder("integration-tests").startSpan();
            resp.getWriter().println("traced");
            span.end();
        } else if (type.equals("logs")) {
            openTelemetry.getLogsBridge().get("integration-tests").logRecordBuilder().setBody("logged").emit();
            resp.getWriter().println("logged");
        } else if (type.equals("export")) {
            String jsonResponse = "{" +
                    "\"spans\": \"" + spanExporter.getFinishedSpanItems().size() + "\"," +
                    "\"metrics\": \"" + metricReader.collectAllMetrics().size() + "\"," +
                    "\"logs\": \"" + logRecordExporter.getFinishedLogRecordItems().size()+ "\"," +
                    "}";
            resp.getWriter().println(jsonResponse);
        }
    }
}
