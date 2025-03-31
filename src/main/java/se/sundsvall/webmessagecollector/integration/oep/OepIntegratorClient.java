package se.sundsvall.webmessagecollector.integration.oep;

import static se.sundsvall.webmessagecollector.integration.oep.configuration.OepIntegratorConfiguration.CLIENT_ID;

import generated.se.sundsvall.oepintegrator.Webmessage;
import generated.se.sundsvall.oepintegrator.WebmessageAttachmentData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.webmessagecollector.integration.oep.configuration.OepIntegratorConfiguration;

@FeignClient(
	name = CLIENT_ID,
	url = "${integration.oep-integrator.url}",
	configuration = OepIntegratorConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface OepIntegratorClient {

	@GetMapping(path = "/{municipalityId}/{instanceType}/webmessages/flow-instances/{flowInstanceId}/attachments/{attachmentId}")
	WebmessageAttachmentData getAttachmentById(
		@PathVariable(name = "municipalityId") final String municipalityId,
		@PathVariable(name = "instanceType") final String instanceType,
		@PathVariable(name = "flowInstanceId") final String flowInstanceId,
		@PathVariable(name = "attachmentId") final Integer attachmentId);

	@GetMapping(path = "/{municipalityId}/{instanceType}/webmessages/families/{familyId}")
	List<Webmessage> getWebmessageByFamilyId(
		@PathVariable(name = "municipalityId") final String municipalityId,
		@PathVariable(name = "instanceType") final String instanceType,
		@PathVariable(name = "familyId") final String familyId,
		@RequestParam(name = "fromDate") final String fromDate,
		@RequestParam(name = "toDate") final String toDate);

	@GetMapping(path = "/{municipalityId}/{instanceType}/webmessages/flow-instances/{flowInstanceId}")
	List<Webmessage> getWebmessagesByFlowInstanceId(
		@PathVariable(name = "municipalityId") final String municipalityId,
		@PathVariable(name = "instanceType") final String instanceType,
		@PathVariable(name = "flowInstanceId") final String flowInstanceId,
		@RequestParam(name = "fromDate") final String fromDate,
		@RequestParam(name = "toDate") final String toDate);

}
