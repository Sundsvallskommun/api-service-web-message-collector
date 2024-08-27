package se.sundsvall.webmessagecollector.service.scheduler;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEProperties;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

import net.javacrumbs.shedlock.core.LockProvider;

@Component
public class MessageCacheScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(MessageCacheScheduler.class);

	private final MessageCacheService messageCacheService;
	private final MessageCacheFactory messageCacheFactory;
	private final TaskScheduler taskScheduler;
	private final LockProvider lockProvider;

	MessageCacheScheduler(final OpenEProperties openEProperties, final MessageCacheService messageCacheService, final MessageCacheFactory messageCacheFactory, final TaskScheduler taskScheduler, final LockProvider lockProvider) {
		this.messageCacheService = messageCacheService;
		this.messageCacheFactory = messageCacheFactory;
		this.taskScheduler = taskScheduler;
		this.lockProvider = lockProvider;
		initializeEnvironments(openEProperties);
	}

	void initializeEnvironments(final OpenEProperties openEProperties) {
		openEProperties.environments().forEach(this::scheduleTasks);
	}

	void scheduleTasks(final String municipalityId, final OpenEProperties.OpenEEnvironment environment) {
		var lockConfiguration = messageCacheFactory.createLockConfiguration(Instant.now(), "lock-" + municipalityId, environment.scheduler().lockAtMostFor(), Duration.ZERO);

		var executor = messageCacheFactory.createDefaultLockingTaskExecutor(lockProvider);
		var lockManager = messageCacheFactory.createDefaultLockManager(executor, messageCacheFactory.createLockConfigurationExtractor(lockConfiguration));
		var cronTrigger = messageCacheFactory.createCronTrigger(environment.scheduler().cron());
		var lockableTaskScheduler = messageCacheFactory.createLockableTaskScheduler(taskScheduler, lockManager);
		var task = messageCacheFactory.createCacheMessagesTask(messageCacheService, municipalityId, environment);
		lockableTaskScheduler.schedule(task, cronTrigger);
	}

	record CacheMessagesTask(MessageCacheService messageCacheService, String municipalityId,
	                         OpenEProperties.OpenEEnvironment environment) implements Runnable {

		@Override
		public void run() {
			LOG.info("Message caching for municipalityId {} started", municipalityId);

			// Cache messages from the external instance, if it is configured
			ofNullable(environment.external())
				.map(OpenEProperties.OpenEEnvironment.OpenEInstance::familyIds)
				.orElse(emptyList())
				.forEach(familyId -> fetchMessages(municipalityId, EXTERNAL, familyId));

			// Cache messages from the internal instance, if it is configured
			ofNullable(environment.internal())
				.map(OpenEProperties.OpenEEnvironment.OpenEInstance::familyIds)
				.orElse(emptyList())
				.forEach(familyId -> fetchMessages(municipalityId, INTERNAL, familyId));

			LOG.info("Message caching for municipalityId {} finished", municipalityId);
		}

		private void fetchMessages(final String municipalityId, final Instance instance, final String familyId) {
			try {
				messageCacheService.fetchMessages(municipalityId, instance, familyId)
					.forEach(message -> ofNullable(message.getAttachments()).orElse(emptyList())
						.forEach(attachmentEntity -> messageCacheService.fetchAttachment(municipalityId, instance, attachmentEntity)));
			} catch (Exception e) {
				LOG.error("Unable to process messages for familyId {} (municipalityId: {}, {})", familyId, municipalityId, instance, e);
			}
		}
	}
}
