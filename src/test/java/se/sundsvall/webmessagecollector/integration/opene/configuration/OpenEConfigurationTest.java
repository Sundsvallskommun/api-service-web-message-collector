package se.sundsvall.webmessagecollector.integration.opene.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.dept44.configuration.TruststoreConfiguration;

import feign.Client;
import feign.Logger;

@SpringBootTest(classes = {
	TruststoreConfiguration.class, OpenEConfiguration.class
})
@ActiveProfiles("junit")
class OpenEConfigurationTest {

	@Autowired
	private OpenEProperties openEProperties;

	@Autowired
	private Logger.Level feignLoggerLevel;

	@Autowired
	private Client feignOkHttpClient;

	@Test
	void testAutowiring() {
		assertThat(openEProperties).isNotNull();
		assertThat(feignLoggerLevel).isNotNull();
		assertThat(feignOkHttpClient).isNotNull();
	}
}
