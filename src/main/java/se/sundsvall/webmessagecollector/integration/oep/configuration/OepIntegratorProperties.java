package se.sundsvall.webmessagecollector.integration.oep.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.oep-integrator")
public record OepIntegratorProperties(int connectTimeout, int readTimeout) {
}
