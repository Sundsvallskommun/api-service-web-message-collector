package se.sundsvall.webmessagecollector.integration.oep;

import generated.se.sundsvall.oepintegrator.Webmessage;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;

import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

@Component
public class OepIntegratorIntegration {

	private static final Logger LOG = LoggerFactory.getLogger(OepIntegratorIntegration.class);

	private final OepIntegratorClient oepIntegratorClient;

	public OepIntegratorIntegration(final OepIntegratorClient oepIntegratorClient) {
		this.oepIntegratorClient = oepIntegratorClient;
	}

	public List<Webmessage> getWebmessageByFamilyId(final String municipalityId, final Instance instance, final String familyId, final String fromDateTime, final String toDateTime) {
		LOG.info("Fetching messages for municipalityId {}, instance {}, familyId {}, fromDateTime {}, toDateTime {}", sanitizeForLogging(municipalityId), instance, sanitizeForLogging(familyId), fromDateTime, toDateTime);
		return oepIntegratorClient.getWebmessageByFamilyId(municipalityId, instance.name(), familyId, fromDateTime, toDateTime);
	}

	public ResponseEntity<InputStreamResource> getAttachmentStreamById(final String municipalityId, final Instance instance, final Integer attachmentId) {
		LOG.info("Fetching attachment with attachmentId {} for municipalityId {} and instance {}", attachmentId, sanitizeForLogging(municipalityId), instance);
		ResponseEntity<InputStreamResource> responseEntity = oepIntegratorClient.getAttachmentById(municipalityId, instance.name(), attachmentId);
		validateResponse(responseEntity);
		return responseEntity;
	}

	public List<Webmessage> getWebmessagesByFlowInstanceId(final String municipalityId, final Instance instance, final String flowInstanceId, final String fromDateTime, final String toDateTime) {
		LOG.info("Fetching messages for municipalityId {}, instance {}, flowInstanceId {}, fromDateTime {}, toDateTime {}", sanitizeForLogging(municipalityId), instance, sanitizeForLogging(flowInstanceId), fromDateTime, toDateTime);
		return oepIntegratorClient.getWebmessagesByFlowInstanceId(municipalityId, instance.name(), flowInstanceId, fromDateTime, toDateTime);
	}

	void validateResponse(final ResponseEntity<InputStreamResource> response) {
		if (!response.getStatusCode().is2xxSuccessful()) {
			throw Problem.valueOf(HttpStatus.valueOf(response.getStatusCode().value()), "Error while streaming attachment data from Oep-Integrator");
		}
	}

}
