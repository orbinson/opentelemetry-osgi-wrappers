# OpenTelemetry OSGi Wrappers

Provide OSGi wrappers for OpenTelemetry artifacts.

Every Maven module maps to a specific GitHub repository in the `open-telemetry` namespace that has a fixed `opentelemetry.groupId` and `opentelemetry.version` that is defined by the code repository.

Example: the `opentelemetry-java` module will contain artifacts that are generated and maintained in the https://github.com/open-telemetry/opentelemetry-java repository. If the repository has a version `1.40.0` the resulting bundle will have version `1.40.0.000` which can still be incremented for bugfixes or issues.

## OpenTelemetry Java

Bundle with artifacts from the [opentelemetry-java](https://github.com/open-telemetry/opentelemetry-java) libraries:

* [opentelemetry-api](https://github.com/open-telemetry/opentelemetry-java/tree/main/api/all)
* [opentelemetry-sdk](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk/all)
* [opentelemetry-sdk-metrics](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk/metrics)
* [opentelemetry-sdk-extension-autoconfigure](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk-extensions/autoconfigure)
* [opentelemetry-sdk-extension-autoconfigure-spi](https://github.com/open-telemetry/opentelemetry-java/tree/main/sdk-extensions/autoconfigure-spi)
* [opentelemetry-exporter-otlp](https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/logging-otlp)
* [opentelemetry-exporter-logging](https://github.com/open-telemetry/opentelemetry-java/tree/main/exporters/logging)

## OpenTelemetry Java Instrumentation

Bundle with a subset of the [java-instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation) libraries:

* [logback-appender-1.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/logback/logback-appender-1.0/library)
* [opentelemetry-instrumentation-annotations](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation-annotations/src/main/java/io/opentelemetry/instrumentation/annotations)

## Semantic Conventions Java

Bundle with artifacts from the [semantic-conventions-java](https://github.com/open-telemetry/semantic-conventions-java) libraries:

* [opentelemetry-semconv](https://github.com/open-telemetry/semantic-conventions-java/tree/main/semconv)
