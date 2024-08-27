package se.sundsvall.webmessagecollector.service.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEProperties;

import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockConfigurationExtractor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;

@Component
public class MessageCacheFactory {

	public MessageCacheFactory() {
	}

	LockConfigurationExtractor createLockConfigurationExtractor(final LockConfiguration lockConfiguration) {
		return extractor -> Optional.of(lockConfiguration);
	}

	DefaultLockingTaskExecutor createDefaultLockingTaskExecutor(final LockProvider lockProvider) {
		return new DefaultLockingTaskExecutor(lockProvider);
	}

	LockConfiguration createLockConfiguration(final Instant instant, final String name, final Duration lockAtMostFor, final Duration lockAtLeastFor) {
		return new LockConfiguration(instant, name, lockAtMostFor, lockAtLeastFor);
	}

	DefaultLockManager createDefaultLockManager(final DefaultLockingTaskExecutor executor, final LockConfigurationExtractor lockConfigurationExtractor) {
		return new DefaultLockManager(executor, lockConfigurationExtractor);
	}

	CronTrigger createCronTrigger(final String cron) {
		return new CronTrigger(cron);
	}

	MessageCacheScheduler.CacheMessagesTask createCacheMessagesTask(final MessageCacheService messageCacheService, final String municipalityId, final OpenEProperties.OpenEEnvironment environment) {
		return new MessageCacheScheduler.CacheMessagesTask(messageCacheService, municipalityId, environment);
	}

	LockableTaskScheduler createLockableTaskScheduler(final TaskScheduler taskScheduler, final DefaultLockManager lockManager) {
		return new LockableTaskScheduler(taskScheduler, lockManager);
	}
}
