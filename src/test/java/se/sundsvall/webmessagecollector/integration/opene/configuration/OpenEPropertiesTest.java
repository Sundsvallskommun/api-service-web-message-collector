package se.sundsvall.webmessagecollector.integration.opene.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.webmessagecollector.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class OpenEPropertiesTest {

	@Autowired
	private OpenEProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(15);
		assertThat(properties.readTimeout()).isEqualTo(20);
		assertThat(properties.username()).isEqualTo("someUsername");
		assertThat(properties.password()).isEqualTo("somePassword");
	}
}
