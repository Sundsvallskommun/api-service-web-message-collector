package se.sundsvall.webmessagecollector.service.scheduler;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEProperties;

import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockConfigurationExtractor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;

@ExtendWith(MockitoExtension.class)
class MessageCacheSchedulerTest {

	@Mock
	private OpenEProperties openEPropertiesMock;

	@Mock
	private MessageCacheService messageCacheServiceMock;

	@Mock
	private MessageCacheFactory messageCacheFactoryMock;

	@Mock
	private TaskScheduler taskSchedulerMock;

	@Mock
	private LockProvider lockProviderMock;

	@Mock
	private CronTrigger cronTriggerMock;

	@Mock
	private LockableTaskScheduler lockableTaskSchedulerMock;

	@Mock
	private MessageCacheScheduler.CacheMessagesTask cacheMessagesTaskMock;

	@Mock
	private DefaultLockManager lockManagerMock;

	@Mock
	private LockConfigurationExtractor lockConfigurationExtractorMock;

	@Mock
	private LockConfiguration lockConfigurationMock;

	@Mock
	private DefaultLockingTaskExecutor executorMock;

	@InjectMocks
	private MessageCacheScheduler messageCacheScheduler;

	@Test
	void initializeEnvironments() {
		final var spy = Mockito.spy(messageCacheScheduler);
		final var properties = createProperties(List.of("2281", "2260"));

		when(messageCacheFactoryMock.createLockConfiguration(any(Instant.class), anyString(), any(Duration.class), eq(Duration.ZERO))).thenReturn(lockConfigurationMock);
		when(messageCacheFactoryMock.createDefaultLockingTaskExecutor(lockProviderMock)).thenReturn(executorMock);
		when(messageCacheFactoryMock.createLockConfigurationExtractor(lockConfigurationMock)).thenReturn(lockConfigurationExtractorMock);
		when(messageCacheFactoryMock.createDefaultLockManager(executorMock, lockConfigurationExtractorMock)).thenReturn(lockManagerMock);
		when(messageCacheFactoryMock.createCronTrigger("0 0 0 1 1 ?")).thenReturn(cronTriggerMock);
		when(messageCacheFactoryMock.createLockableTaskScheduler(lockableTaskSchedulerMock, lockManagerMock)).thenReturn(lockableTaskSchedulerMock);
		when(messageCacheFactoryMock.createCacheMessagesTask(eq(messageCacheServiceMock), anyString(), any(OpenEProperties.OpenEEnvironment.class))).thenReturn(cacheMessagesTaskMock);

		spy.initializeEnvironments(properties);

		verify(spy).initializeEnvironments(properties);
		verify(spy).scheduleTasks("2281", properties.environments().get("2281"));
		verify(spy).scheduleTasks("2260", properties.environments().get("2260"));
		verifyNoMoreInteractions(spy);
	}

	@Test
	void scheduleTasks() {
		var properties = createProperties(List.of("2281"));
		final var municipalityId = "2281";
		final var environment = properties.environments().get(municipalityId);
		var instant = Instant.now();
		try (MockedStatic<Instant> instantMock = mockStatic(Instant.class)) {
			instantMock.when(Instant::now).thenReturn(instant);
			when(messageCacheFactoryMock.createLockConfiguration(Instant.now(), "lock-" + municipalityId, environment.scheduler().lockAtMostFor(), Duration.ZERO)).thenReturn(lockConfigurationMock);
			when(messageCacheFactoryMock.createDefaultLockingTaskExecutor(lockProviderMock)).thenReturn(executorMock);
			when(messageCacheFactoryMock.createLockConfigurationExtractor(lockConfigurationMock)).thenReturn(lockConfigurationExtractorMock);
			when(messageCacheFactoryMock.createDefaultLockManager(executorMock, lockConfigurationExtractorMock)).thenReturn(lockManagerMock);
			when(messageCacheFactoryMock.createCronTrigger(environment.scheduler().cron())).thenReturn(cronTriggerMock);
			when(messageCacheFactoryMock.createLockableTaskScheduler(lockableTaskSchedulerMock, lockManagerMock)).thenReturn(lockableTaskSchedulerMock);
			when(messageCacheFactoryMock.createCacheMessagesTask(messageCacheServiceMock, municipalityId, environment)).thenReturn(cacheMessagesTaskMock);

			messageCacheScheduler.scheduleTasks(municipalityId, environment);

			verify(messageCacheFactoryMock).createLockConfiguration(Instant.now(), "lock-" + municipalityId, environment.scheduler().lockAtMostFor(), Duration.ZERO);
			verify(messageCacheFactoryMock).createDefaultLockingTaskExecutor(lockProviderMock);
			verify(messageCacheFactoryMock).createLockConfigurationExtractor(lockConfigurationMock);
			verify(messageCacheFactoryMock).createDefaultLockManager(executorMock, lockConfigurationExtractorMock);
			verify(messageCacheFactoryMock).createCronTrigger(environment.scheduler().cron());
			verify(messageCacheFactoryMock).createLockableTaskScheduler(lockableTaskSchedulerMock, lockManagerMock);
			verify(messageCacheFactoryMock).createCacheMessagesTask(messageCacheServiceMock, municipalityId, environment);
			verify(lockableTaskSchedulerMock).schedule(cacheMessagesTaskMock, cronTriggerMock);

		}
	}


	private OpenEProperties createProperties(List<String> municipalityIds) {
		var openEProperties = new HashMap<String, OpenEProperties.OpenEEnvironment>();

		for (var id : municipalityIds) {
			openEProperties.put(id, new OpenEProperties.OpenEEnvironment(
				new OpenEProperties.OpenEEnvironment.SchedulerProperties(true, "0 0 0 1 1 ?", Duration.of(2, SECONDS)),
				new OpenEProperties.OpenEEnvironment.OpenEInstance("baseUrl", id + "-username", id + "-password", List.of("123"), 5, 60),
				new OpenEProperties.OpenEEnvironment.OpenEInstance("baseUrl", id + "-username", id + "-password", List.of("321"), 5, 60)));
		}
		return new OpenEProperties(openEProperties);
	}

}
