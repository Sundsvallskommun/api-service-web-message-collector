package se.sundsvall.webmessagecollector.service.scheduler;

import static java.util.Optional.ofNullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.opene.OpenEIntegration;

@Component
class MessageCacheScheduler {
    
    private final OpenEIntegration integration;
    private final MessageRepository messageRepository;
    private final MessageCacheProperties messageCacheProperties;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    
    MessageCacheScheduler(OpenEIntegration integration, MessageRepository messageRepository, MessageCacheProperties messageCacheProperties) {
        this.integration = integration;
        this.messageRepository = messageRepository;
        this.messageCacheProperties = messageCacheProperties;
    }
    
	@Scheduled(initialDelayString = "${scheduler.initialDelay}", fixedRateString = "${scheduler.fixedRate}")
	@SchedulerLock(name = "cacheMessages", lockAtMostFor = "${scheduler.lock-at-most-for}")
	public void cacheMessages() {
		Arrays.stream(ofNullable(messageCacheProperties.familyId()).orElse("")
			.split(","))
			.filter(StringUtils::isNotBlank)
			.forEach(familyid -> messageRepository
				.saveAll(integration.getMessages(familyid, OffsetDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)), "")));
    }
}
