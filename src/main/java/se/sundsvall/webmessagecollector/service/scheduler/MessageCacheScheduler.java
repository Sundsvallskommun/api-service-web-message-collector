package se.sundsvall.webmessagecollector.service.scheduler;

import static java.util.Optional.ofNullable;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
class MessageCacheScheduler {
    
	private static final Logger LOG = LoggerFactory.getLogger(MessageCacheScheduler.class);

    private final MessageCacheProperties messageCacheProperties;
	private final MessageCacheService messageCacheService;
    
	MessageCacheScheduler(MessageCacheService messageCacheService, MessageCacheProperties messageCacheProperties) {
		this.messageCacheService = messageCacheService;
        this.messageCacheProperties = messageCacheProperties;
    }
    
	@Scheduled(cron = "${scheduler.cron}")
	@SchedulerLock(name = "cacheMessages", lockAtMostFor = "${scheduler.lock-at-most-for}")
	public void cacheMessages() {
		Arrays.stream(ofNullable(messageCacheProperties.familyId()).orElse("")
			.split(","))
			.filter(StringUtils::isNotBlank)
			.map(String::trim)
			.forEach(this::fetchMessages);
	}

	private void fetchMessages(String familyId) {
		try {
			messageCacheService.fetchMessages(familyId);
		} catch (Exception e) {
			LOG.error("Unable to process messages for familyId {}", familyId, e);
		}
	}
}
