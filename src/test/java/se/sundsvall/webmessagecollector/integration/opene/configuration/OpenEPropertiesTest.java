package se.sundsvall.webmessagecollector.integration.opene.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
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
		assertThat(properties.environments()).hasSize(1);
		assertThat(properties.environments()).allSatisfy((municipalityId, environment) -> {
			assertThat(municipalityId).isEqualTo("1984");

			assertThat(environment.scheduler()).isNotNull().satisfies(scheduler -> {
				assertThat(scheduler.enabled()).isTrue();
				assertThat(scheduler.cron()).isEqualTo("0 */5 * * * *");
				assertThat(scheduler.lockAtMostFor()).isEqualTo(Duration.ofMinutes(3));
			});

			assertThat(environment.internal()).isNotNull().satisfies(internalInstance -> {
				assertThat(internalInstance.baseUrl()).isEqualTo("internal-base-url");
				assertThat(internalInstance.username()).isEqualTo("someInternalUsername");
				assertThat(internalInstance.password()).isEqualTo("someInternalPassword");
				assertThat(internalInstance.familyIds()).containsExactlyInAnyOrder("123", "456");
				assertThat(internalInstance.connectTimeout()).isEqualTo(15);
				assertThat(internalInstance.readTimeout()).isEqualTo(20);
			});

			assertThat(environment.external()).isNotNull().satisfies(externalInstance -> {
				assertThat(externalInstance.baseUrl()).isEqualTo("external-base-url");
				assertThat(externalInstance.username()).isEqualTo("someExternalUsername");
				assertThat(externalInstance.password()).isEqualTo("someExternalPassword");
				assertThat(externalInstance.familyIds()).containsExactlyInAnyOrder("789");
				assertThat(externalInstance.connectTimeout()).isEqualTo(16);
				assertThat(externalInstance.readTimeout()).isEqualTo(21);
			});
		});
	}
}
