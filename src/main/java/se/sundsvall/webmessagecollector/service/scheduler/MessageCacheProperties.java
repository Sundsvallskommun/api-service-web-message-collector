package se.sundsvall.webmessagecollector.service.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "scheduler")
class MessageCacheProperties {
    private String familyId;
    private String fixedRate;
    private String initialDelay;
}
