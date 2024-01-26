package se.sundsvall.webmessagecollector.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.webmessagecollector.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class MessageCachePropertiesTest {

	@Autowired
	private MessageCacheProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.familyId()).isEqualTo("someFamilyId");
		assertThat(properties.fixedRate()).isEqualTo("PT10M");
		assertThat(properties.initialDelay()).isEqualTo("PT11M");
	}
}
