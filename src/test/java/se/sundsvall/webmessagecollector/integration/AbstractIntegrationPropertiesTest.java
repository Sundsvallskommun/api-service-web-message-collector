package se.sundsvall.webmessagecollector.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class AbstractIntegrationPropertiesTest {

	@Test
	void testAbstractPropertiesClass() {
		final var baseUrl = "baseUrl";
		final var connectionTimeout = Duration.ofMinutes(1);
		final var readTimeout = Duration.ofMinutes(2);
		final var bean = new AbstractIntegrationProperties() {};

		bean.setBaseUrl(baseUrl);
		bean.setConnectTimeout(connectionTimeout);
		bean.setReadTimeout(readTimeout);

		assertThat(bean.getBaseUrl()).isEqualTo(baseUrl);
		assertThat(bean.getConnectTimeout()).isEqualTo(connectionTimeout);
		assertThat(bean.getReadTimeout()).isEqualTo(readTimeout);
	}

	@Test
	void testBasicAuthClass() {
		final var password = "password";
		final var username = "username";

		final var bean = new AbstractIntegrationProperties.BasicAuth();
		bean.setPassword(password);
		bean.setUsername(username);

		assertThat(bean.getPassword()).isEqualTo(password);
		assertThat(bean.getUsername()).isEqualTo(username);
	}

	@Test
	void testoAuth2Class() {
		final var clientId = "clientId";
		final var clientSecret = "clientSecret";
		final var grantType = "grantType";
		final var tokenUri = "tokenUri";
		final var bean = new AbstractIntegrationProperties.OAuth2();

		bean.setClientId(clientId);
		bean.setClientSecret(clientSecret);
		bean.setGrantType(grantType);
		bean.setTokenUri(tokenUri);

		assertThat(bean.getClientId()).isEqualTo(clientId);
		assertThat(bean.getClientSecret()).isEqualTo(clientSecret);
		assertThat(bean.getGrantType()).isEqualTo(grantType);
		assertThat(bean.getTokenUri()).isEqualTo(tokenUri);
	}

	@Test
	void testDefaultValues() {
		final var props = new AbstractIntegrationProperties() {};
		assertThat(props).hasAllNullFieldsOrPropertiesExcept("readTimeout", "connectTimeout");
		assertThat(props.getConnectTimeout()).isEqualTo(Duration.ofSeconds(30));
		assertThat(props.getReadTimeout()).isEqualTo(Duration.ofSeconds(300));

		final var oauth2 = new AbstractIntegrationProperties.OAuth2();
		assertThat(oauth2).hasAllNullFieldsOrPropertiesExcept("grantType");
		assertThat(oauth2.getGrantType()).isEqualTo("client_credentials");

		assertThat(new AbstractIntegrationProperties.BasicAuth()).hasAllNullFieldsOrProperties();
	}
}
