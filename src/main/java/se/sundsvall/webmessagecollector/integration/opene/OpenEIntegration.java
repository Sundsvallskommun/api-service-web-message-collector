package se.sundsvall.webmessagecollector.integration.opene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@Component
public class OpenEIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(OpenEIntegration.class);

	private final OpenEClientFactory clientFactory;

	public OpenEIntegration(final OpenEClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public byte[] getMessages(final String municipalityId, final Instance instance, final String familyId, final String fromDate, final String toDate) {
		var client = clientFactory.getClient(municipalityId, instance);
		LOG.info("Fetching messages for municipalityId {}, instance {}, familyId {}, fromDate {}, toDate {}", municipalityId, instance, familyId, fromDate, toDate);
		return client.getMessages(familyId, fromDate, toDate);
	}

	public byte[] getAttachment(final String municipalityId, final Instance instance, final int attachmentId) {
		var client = clientFactory.getClient(municipalityId, instance);
		LOG.info("Fetching attachment with attachmentId {} for municipalityId {} and instance {}", attachmentId, municipalityId, instance);
		return client.getAttachment(attachmentId);
	}
}
