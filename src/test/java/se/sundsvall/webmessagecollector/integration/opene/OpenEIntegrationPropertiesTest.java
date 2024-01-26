package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.webmessagecollector.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class OpenEIntegrationPropertiesTest {

	@Autowired
	private OpenEIntegrationProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.getBaseUrl()).isEqualTo("base-url");
		assertThat(properties.getConnectTimeout()).isEqualTo(Duration.ofSeconds(15));
		assertThat(properties.getReadTimeout()).isEqualTo(Duration.ofSeconds(20));
		assertThat(properties.getErrandPath()).isEqualTo("someErrandPath");
		assertThat(properties.getMessagesPath()).isEqualTo("someMessagesPath");
		assertThat(properties.getPort()).isEqualTo(1234);
		assertThat(properties.getScheme()).isEqualTo("http");
		assertThat(properties.getBasicAuth()).isNotNull().satisfies(basicAuth -> {
			assertThat(basicAuth.getPassword()).isEqualTo("somePassword");
			assertThat(basicAuth.getUsername()).isEqualTo("someUsername");
		});
	}
}
