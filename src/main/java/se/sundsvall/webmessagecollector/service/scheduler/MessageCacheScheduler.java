package se.sundsvall.webmessagecollector.service.scheduler;

import static java.util.Collections.emptyMap;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
class MessageCacheScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(MessageCacheScheduler.class);

	private final MessageCacheProperties messageCacheProperties;

	private final MessageCacheService messageCacheService;

	MessageCacheScheduler(final MessageCacheService messageCacheService, final MessageCacheProperties messageCacheProperties) {
		this.messageCacheService = messageCacheService;
		this.messageCacheProperties = messageCacheProperties;
	}

	@Scheduled(cron = "${scheduler.cron}")
	@SchedulerLock(name = "cacheMessages", lockAtMostFor = "${scheduler.lock-at-most-for}")
	public void cacheMessages() {
		LOG.info("Caching messages");

		Optional.ofNullable(messageCacheProperties.familyIds())
			.orElse(emptyMap())
			.forEach((instance, familyIds) -> familyIds.forEach(familyId -> fetchMessages(instance, familyId)));
	}

	private void fetchMessages(final String instance, final String familyId) {

		final var instanceEnum = Instance.fromString(instance);
		try {
			messageCacheService.fetchMessages(instanceEnum, familyId)
				.forEach(message -> Optional.ofNullable(message.getAttachments())
					.orElse(Collections.emptyList())
					.forEach(attachmentEntity -> messageCacheService.fetchAttachment(instanceEnum, attachmentEntity)));
		} catch (final Exception e) {
			LOG.error("Unable to process messages for familyId {}", familyId, e);
		}
	}

}
