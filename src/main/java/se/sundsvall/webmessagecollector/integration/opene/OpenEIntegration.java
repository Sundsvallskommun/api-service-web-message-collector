package se.sundsvall.webmessagecollector.integration.opene;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

@Component
public class OpenEIntegration {
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenEIntegration.class);
    
    private final OpenEClient client;

    public OpenEIntegration(final OpenEClient client) {
        this.client = client;

    }
    
	public List<MessageEntity> getMessages(String familyId, String fromDate, String toDate) {
        try {
            return OpenEMapper.mapMessages(client.getMessages(familyId, fromDate, toDate), familyId);
        } catch (Exception e) {
            LOG.info("Unable to get errandIds for familyId {}", familyId, e);
            return List.of();
        }
    }
}
