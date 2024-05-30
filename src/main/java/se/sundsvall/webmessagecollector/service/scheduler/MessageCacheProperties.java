package se.sundsvall.webmessagecollector.service.scheduler;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scheduler")
record MessageCacheProperties(Map<String, List<String>> familyIds) {

}
