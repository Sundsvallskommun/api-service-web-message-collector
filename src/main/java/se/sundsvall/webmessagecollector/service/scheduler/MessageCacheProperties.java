package se.sundsvall.webmessagecollector.service.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scheduler")
record MessageCacheProperties(String familyId, String fixedRate, String initialDelay) {
}
