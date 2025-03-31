package se.sundsvall.webmessagecollector.integration.oep;

import generated.se.sundsvall.oepintegrator.Webmessage;
import generated.se.sundsvall.oepintegrator.WebmessageAttachmentData;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;

@Component
public class OepIntegratorIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(OepIntegratorIntegration.class);

	private final OepIntegratorClient oepIntegratorClient;

	public OepIntegratorIntegration(OepIntegratorClient oepIntegratorClient) {
		this.oepIntegratorClient = oepIntegratorClient;
	}

	public List<Webmessage> getWebmessageByFamilyId(final String municipalityId, final Instance instance, final String familyId, final String fromDate, final String toDate) {
		LOG.info("Fetching messages for municipalityId {}, instance {}, familyId {}, fromDate {}, toDate {}", municipalityId, instance, familyId, fromDate, toDate);
		return oepIntegratorClient.getWebmessageByFamilyId(municipalityId, instance.name(), familyId, fromDate, toDate);
	}

	public WebmessageAttachmentData getAttachmentById(final String municipalityId, final Instance instance, final String flowInstanceId, final int attachmentId) {
		LOG.info("Fetching attachment with attachmentId {} for municipalityId {} and flowInstanceId {} and instance {}", attachmentId, municipalityId, flowInstanceId, instance);
		return oepIntegratorClient.getAttachmentById(municipalityId, instance.name(), flowInstanceId, attachmentId);
	}

	public List<Webmessage> getWebmessagesByFlowInstanceId(final String municipalityId, final Instance instance, final String flowInstanceId, final String fromDate, final String toDate) {
		LOG.info("Fetching messages for municipalityId {}, instance {}, flowInstanceId {}, fromDate {}, toDate {}", municipalityId, instance, flowInstanceId, fromDate, toDate);
		return oepIntegratorClient.getWebmessagesByFlowInstanceId(municipalityId, instance.name(), flowInstanceId, fromDate, toDate);
	}

}
