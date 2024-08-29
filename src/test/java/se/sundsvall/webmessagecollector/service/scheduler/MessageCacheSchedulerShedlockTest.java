package se.sundsvall.webmessagecollector.service.scheduler;


import static java.time.Clock.systemUTC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@SpringBootTest(properties = {
	"spring.flyway.enabled=true",
	"integration.db.case-status.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
	"integration.db.case-status.url=jdbc:tc:mariadb:10.6.4:////",
	"integration.open-e.environments.2000.scheduler.enabled=true",
	"integration.open-e.environments.2000.scheduler.cron=* * * * * *",
	"integration.open-e.environments.2000.internal.base-url=any-url",
	"integration.open-e.environments.2000.internal.username=any-username",
	"integration.open-e.environments.2000.internal.password=any-password",
	"integration.open-e.environments.2000.internal.family-ids[0]=123",
	"server.shutdown=immediate",
	"spring.lifecycle.timeout-per-shutdown-phase=0s"
})
@ActiveProfiles("junit")
class MessageCacheSchedulerShedlockTest {

	@TestConfiguration
	public static class ShedlockTestConfiguration {
		@Bean
		@Primary
		public MessageCacheService createMock() {

			final var mockedBean = Mockito.mock(MessageCacheService.class);

			// Let mock hang forever for simulating long-running task
			doAnswer(invocation -> {
				mockCalledTime = LocalDateTime.now();
				await().forever()
					.until(() -> false);
				return null;
			}).when(mockedBean).fetchMessages(any(), any(), any());

			return mockedBean;
		}
	}

	@Autowired
	private MessageCacheService messageCacheServiceMock;

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	private static LocalDateTime mockCalledTime;

	@Test
	void verifyShedlockIsCreatingALock() {

		await().until(() -> mockCalledTime != null && LocalDateTime.now().isAfter(mockCalledTime.plusSeconds(5)));

		await().atMost(5, SECONDS)
			.untilAsserted(() -> assertThat(getLockedAt("lock-2000"))
				.isCloseTo(LocalDateTime.now(systemUTC()), within(5, ChronoUnit.SECONDS)));

		verify(messageCacheServiceMock, times(1)).fetchMessages("2000", Instance.INTERNAL, "123");
		verifyNoMoreInteractions(messageCacheServiceMock);
	}

	private LocalDateTime getLockedAt(String lockName) {
		return jdbcTemplate.query(
			"SELECT locked_at FROM shedlock WHERE name = :name",
			Map.of("name", lockName),
			this::mapTimestamp);
	}

	private LocalDateTime mapTimestamp(final ResultSet rs) throws SQLException {
		if (rs.next()) {
			return LocalDateTime.parse(rs.getString("locked_at"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		}
		return null;
	}

}
