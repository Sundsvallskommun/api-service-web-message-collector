package se.sundsvall.webmessagecollector.integration.oep;

import static se.sundsvall.webmessagecollector.integration.oep.configuration.OepIntegratorConfiguration.CLIENT_ID;

import generated.se.sundsvall.oepintegrator.Webmessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
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

	@GetMapping(path = "/{municipalityId}/{instanceType}/webmessages/attachments/{attachmentId}")
	ResponseEntity<InputStreamResource> getAttachmentById(
		@PathVariable(name = "municipalityId") final String municipalityId,
		@PathVariable(name = "instanceType") final String instanceType,
		@PathVariable(name = "attachmentId") final Integer attachmentId);

	@GetMapping(path = "/{municipalityId}/{instanceType}/webmessages/families/{familyId}")
	List<Webmessage> getWebmessageByFamilyId(
		@PathVariable(name = "municipalityId") final String municipalityId,
		@PathVariable(name = "instanceType") final String instanceType,
		@PathVariable(name = "familyId") final String familyId,
		@RequestParam(name = "fromDateTime") final LocalDateTime fromDate,
		@RequestParam(name = "toDateTime") final LocalDateTime toDate);

	@GetMapping(path = "/{municipalityId}/{instanceType}/webmessages/cases/{flowInstanceId}")
	List<Webmessage> getWebmessagesByFlowInstanceId(
		@PathVariable(name = "municipalityId") final String municipalityId,
		@PathVariable(name = "instanceType") final String instanceType,
		@PathVariable(name = "flowInstanceId") final String flowInstanceId,
		@RequestParam(name = "fromDateTime") final LocalDateTime fromDate,
		@RequestParam(name = "toDateTime") final LocalDateTime toDate);

}
