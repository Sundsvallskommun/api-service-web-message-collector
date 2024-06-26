package se.sundsvall.webmessagecollector.integration.opene.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.open-e")
public record OpenEProperties(
	String username,
	String internalPassword,
	String externalPassword,
	int connectTimeout,
	int readTimeout
) {

}
