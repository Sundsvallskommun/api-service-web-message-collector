package se.sundsvall.webmessagecollector.service.scheduler;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.requestid.RequestId;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEProperties;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@Component
public class MessageCacheScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(MessageCacheScheduler.class);

	MessageCacheScheduler(final OpenEProperties openEProperties, final MessageCacheService messageCacheService, final TaskScheduler taskScheduler, final LockProvider lockProvider, final MessageProcessingHealthIndicator healthIndicator) {
		var executor = new DefaultLockingTaskExecutor(lockProvider);

		openEProperties.environments().forEach((municipalityId, environment) -> {
			// Ensure clockSkew does not cause message duplication. Add one extra minute to be safe.
			if (environment.scheduler().clockSkew().plusMinutes(1).toMinutes() > environment.scheduler().keepDeletedAfterLastSuccessFor().toMinutes()) {
				throw new IllegalArgumentException("Incompatible properties! scheduler.clockSkew cannot be greater than scheduler.keepDeletedAfterLastSuccessFor");
			}
			var lockConfiguration = new LockConfiguration(Instant.now(), "lock-" + municipalityId, environment.scheduler().lockAtMostFor(), Duration.ZERO);
			var lockManager = new DefaultLockManager(executor, lockConfigurationExtractor -> Optional.of(lockConfiguration));
			var cronTrigger = new CronTrigger(environment.scheduler().cron());
			var lockableTaskScheduler = new LockableTaskScheduler(taskScheduler, lockManager);
			var task = new CacheMessagesTask(messageCacheService, municipalityId, environment, healthIndicator);

			lockableTaskScheduler.schedule(task, cronTrigger);
		});
	}

	record CacheMessagesTask(MessageCacheService messageCacheService, String municipalityId,
		OpenEProperties.OpenEEnvironment environment, MessageProcessingHealthIndicator healthIndicator)
		implements
		Runnable {

		@Override
		public void run() {
			try {
				RequestId.init();
				LOG.info("Message caching for municipalityId {} started", municipalityId);
				healthIndicator.resetErrors();

				// Cache messages from the external instance, if it is configured
				ofNullable(environment.external())
					.map(peek(instance -> LOG.info("Handling external openE instance for municipalityId {} and familyIds {}", municipalityId, instance.familyIds())))
					.map(OpenEProperties.OpenEEnvironment.OpenEInstance::familyIds)
					.orElse(emptyList())
					.forEach(familyId -> fetchMessages(municipalityId, EXTERNAL, familyId, environment.scheduler().clockSkew()));

				// Cache messages from the internal instance, if it is configured
				ofNullable(environment.internal())
					.map(peek(instance -> LOG.info("Handling internal openE instance for municipalityId {} and familyIds {}", municipalityId, instance.familyIds())))
					.map(OpenEProperties.OpenEEnvironment.OpenEInstance::familyIds)
					.orElse(emptyList())
					.forEach(familyId -> fetchMessages(municipalityId, INTERNAL, familyId, environment.scheduler().clockSkew()));

				// Retry messages stuck in status PROCESSING or with status FAILED_ATTACHMENTS
				var retryableMessages = messageCacheService.getRetryableMessages(municipalityId);
				if (!retryableMessages.isEmpty()) {
					LOG.info("Retry fetching attachments for {} messages", retryableMessages.size());
					retryableMessages.forEach(this::fetchAttachments);
				}

				// Clean up deleted messages
				LOG.info("Checking for messages that can be permanently deleted");
				messageCacheService.cleanUpDeletedMessages(environment.scheduler().keepDeletedAfterLastSuccessFor(), municipalityId);

				if (!healthIndicator.hasErrors()) {
					healthIndicator.setHealthy();
				}
			} catch (Exception e) {
				healthIndicator.setUnhealthy("Error running scheduled task: " + e.getMessage());
			} finally {
				LOG.info("Message caching for municipalityId {} finished", municipalityId);
				RequestId.reset();
			}
		}

		private void fetchMessages(final String municipalityId, final Instance instance, final String familyId, final Duration clockSkew) {
			try {
				messageCacheService.fetchAndSaveMessages(municipalityId, instance, familyId, clockSkew)
					.forEach(this::fetchAttachments);
			} catch (Exception e) {
				LOG.error("Unable to process messages for familyId {} (municipalityId: {}, {})", familyId, municipalityId, instance, e);
				healthIndicator.setUnhealthy("Error fetching messages: " + e.getMessage());
			}
		}

		private void fetchAttachments(MessageEntity message) {
			try {
				ofNullable(message.getAttachments()).orElse(emptyList())
					.forEach(attachmentEntity -> messageCacheService.fetchAndSaveAttachment(message.getMunicipalityId(), message.getInstance(), attachmentEntity));
				messageCacheService.complete(message);
			} catch (Exception e) {
				LOG.error("Unable to fetch attachment(s) for message with id '{}'", message.getId(), e);
				messageCacheService.failedAttachments(message);
				healthIndicator.setUnhealthy("Error fetching message attachments: " + e.getMessage());
			}
		}

		static <T> UnaryOperator<T> peek(final Consumer<T> c) {
			return x -> {
				c.accept(x);
				return x;
			};
		}
	}
}
