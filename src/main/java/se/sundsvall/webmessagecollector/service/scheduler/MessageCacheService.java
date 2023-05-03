package se.sundsvall.webmessagecollector.service.scheduler;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.opene.OpenEIntegration;

@Configuration
@EnableScheduling
class MessageCacheService {
    
    private final OpenEIntegration integration;
    private final MessageRepository messageRepository;
    private final MessageCacheProperties messageCacheProperties;
    
    private final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    
    MessageCacheService(OpenEIntegration integration, MessageRepository messageRepository, MessageCacheProperties messageCacheProperties) {
        this.integration = integration;
        this.messageRepository = messageRepository;
        this.messageCacheProperties = messageCacheProperties;
    }
    
    @Scheduled(initialDelayString = "${scheduler.initialDelay}",
        fixedRateString = "${scheduler.fixedRate}")
    private void cacheMessages() {
        
        Arrays.stream(messageCacheProperties.getFamilyId().split(",")).forEach(familyid ->
            messageRepository.saveAll(integration.getMessages(familyid,
                OffsetDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)), "")));
    }
}
