package se.sundsvall.webmessagecollector.api;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/{municipalityId}/messages")
@Tag(name = "messages", description = "Messages")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class MessageResource {

	private final MessageService service;

	MessageResource(final MessageService service) {
		this.service = service;
	}

	@GetMapping(value = "/{familyId}/{Instance}", produces = APPLICATION_JSON_VALUE)
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	@Operation(summary = "Get a list of messages related to a specific familyId", description = "Returns a list of messages found for the specified familyId")
	ResponseEntity<List<MessageDTO>> getMessages(
			@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
			@Parameter(name = "familyId", description = "FamilyId to fetch messages for", example = "123", required = true) @PathVariable("familyId") final String familyId,
			@Parameter(name = "Instance", description = "Instance to fetch messages for", example = "INTERNAL", required = true) @PathVariable("Instance") final String instance) {
		return ResponseEntity.ok(service.getMessages(municipalityId, familyId, instance));
	}

	@DeleteMapping
	@ApiResponse(responseCode = "204", description = "No Content", useReturnTypeSchema = true)
	@Operation(summary = "Delete a list of messages", description = "Deletes a list of messages with the ids provided")
	ResponseEntity<Void> deleteMessages(
			@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
			@RequestBody final List<Integer> ids) {
		service.deleteMessages(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "attachments/{attachmentId}", produces = {ALL_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true)
	@Operation(summary = "Get a messageAttachment", description = "Returns a messageAttachment as a stream for the specified attachmentId")
	void getAttachment(
			@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
			@Parameter(name = "attachmentId", description = "MessageId to fetch attachment for", example = "123", required = true) @PathVariable("attachmentId") final int attachmentId, final HttpServletResponse response) {
		service.getMessageAttachmentStreamed(attachmentId, response);
	}

	@DeleteMapping("attachments/{attachmentId}")
	@ApiResponse(responseCode = "204", description = "No Content", useReturnTypeSchema = true)
	@Operation(summary = "Delete a messageAttachment", description = "Deletes a messageAttachment with the specified id")
	ResponseEntity<Void> deleteAttachment(
			@Parameter(name = "municipalityId", description = "Municipality Id", example = "2281", required = true) @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
			@Parameter(name = "attachmentId", description = "Id of the attachment to delete", example = "123", required = true) @PathVariable("attachmentId") final int attachmentId) {
		service.deleteAttachment(attachmentId);
		return ResponseEntity.noContent().build();
	}
}
