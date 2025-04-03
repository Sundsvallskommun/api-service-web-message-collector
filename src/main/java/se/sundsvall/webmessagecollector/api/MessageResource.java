package se.sundsvall.webmessagecollector.api;

import static com.nimbusds.jose.HeaderParameterNames.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.service.MessageService;

@RestController
@RequestMapping("/{municipalityId}/messages")
@Tag(name = "messages", description = "Messages")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class MessageResource {

	private final MessageService service;

	MessageResource(final MessageService service) {
		this.service = service;
	}

	@GetMapping(value = "/{familyId}/{Instance}", produces = APPLICATION_JSON_VALUE)
	@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MessageDTO.class))))
	@Operation(summary = "Get a list of messages related to a specific familyId", description = "Returns a list of messages found for the specified familyId")
	ResponseEntity<List<MessageDTO>> getMessages(
		@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "familyId", description = "FamilyId to fetch messages for", example = "123", required = true) @PathVariable("familyId") final String familyId,
		@Parameter(name = "Instance", description = "Instance to fetch messages for", example = "INTERNAL", required = true) @PathVariable("Instance") final String instance) {
		return ok(service.getMessages(municipalityId, familyId, instance.toUpperCase()));
	}

	@DeleteMapping
	@ApiResponse(responseCode = "204", description = "No Content", useReturnTypeSchema = true)
	@Operation(summary = "Delete a list of messages", description = "Deletes a list of messages with the ids provided")
	ResponseEntity<Void> deleteMessages(
		@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@RequestBody final List<Integer> ids) {
		service.deleteMessages(ids);

		return noContent().header(CONTENT_TYPE, ALL_VALUE).build();
	}

	@GetMapping(value = "/attachments/{attachmentId}", produces = ALL_VALUE)
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	@Operation(summary = "Get a messageAttachment", description = "Returns a messageAttachment as a stream for the specified attachmentId")
	void getAttachment(
		@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "attachmentId", description = "MessageId to fetch attachment for", example = "123", required = true) @PathVariable("attachmentId") final int attachmentId, final HttpServletResponse response) {
		service.getMessageAttachmentStreamed(attachmentId, response);
	}

	@DeleteMapping("/attachments/{attachmentId}")
	@ApiResponse(responseCode = "204", description = "No Content", useReturnTypeSchema = true)
	@Operation(summary = "Delete a messageAttachment", description = "Deletes a messageAttachment with the specified id")
	ResponseEntity<Void> deleteAttachment(
		@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "attachmentId", description = "Id of the attachment to delete", example = "123", required = true) @PathVariable("attachmentId") final int attachmentId) {
		service.deleteAttachment(attachmentId);

		return noContent().header(CONTENT_TYPE, ALL_VALUE).build();
	}

	@GetMapping(value = "/{instance}/flow-instances/{flowInstanceId}", produces = APPLICATION_JSON_VALUE)
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	@Operation(summary = "Get a list of messages related to a specific flow instance id", description = "Returns a list of messages found for the specified flow instance id")
	ResponseEntity<List<MessageDTO>> getMessagesByFlowInstanceId(
		@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "instance", description = "Instance to fetch messages for", example = "INTERNAL", required = true) @PathVariable("instance") final String instance,
		@Parameter(name = "flowInstanceId", description = "Flow instance id to fetch messages for", example = "1234567890", required = true) @PathVariable("flowInstanceId") final String flowInstanceId,
		@Parameter(name = "fromDateTime", description = "The start date and time for filtering web messages (optional)", example = "2024-01-31T12:00:00") @RequestParam(required = false) @DateTimeFormat(
			iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime fromDateTime,
		@Parameter(name = "toDateTime", description = "The end date and time for filtering web messages (optional).", example = "2024-01-31T12:00:00") @RequestParam(required = false) @DateTimeFormat(
			iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime toDateTime) {
		return ok(service.getMessagesByFlowInstanceId(municipalityId, instance.toUpperCase(), flowInstanceId, fromDateTime, toDateTime));
	}

	@GetMapping(value = "/{instance}/attachments/{attachmentId}", produces = ALL_VALUE)
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	@Operation(summary = "Get an attachment by id", description = "Returns an attachment as a stream for the specified attachmentId")
	void getAttachmentById(final HttpServletResponse response,
		@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "instance", description = "Instance to fetch messages for", example = "INTERNAL", required = true) @PathVariable("instance") final String instance,
		@Parameter(name = "attachmentId", description = "Id of the requested attachment", example = "1234567890", required = true) @PathVariable("attachmentId") final Integer attachmentId) {
		service.streamAttachmentById(municipalityId, instance.toUpperCase(), attachmentId, response);
	}
}
