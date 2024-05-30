package se.sundsvall.webmessagecollector.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

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
		assertThat(properties.familyIds()).isEqualTo(Map.of("internal", List.of("123", "456")));
	}

}
