package se.sundsvall.webmessagecollector.api.model;

import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class MessageDTO {

	@Schema(description = "The webMessageCollector Id for the message", example = "1")
	private final Integer id;

	@Enumerated(EnumType.STRING)
	@Schema(description = "If the message is inbound or outbound from the perspective of " +
		"case-data/e-service.", example = "INBOUND")
	private final Direction direction;

	@Schema(description = "The municipality id", example = "2281")
	private final String municipalityId;

	@Schema(description = "What E-service the message was found in", example = "501")
	private final String familyId;

	@Schema(description = "The external caseID ", example = "caa230c6-abb4-4592-ad9a-34e263c2787b")
	private final String externalCaseId;

	@Schema(description = "The message ", example = "Hello World")
	private final String message;

	@Schema(description = "The unique messageId from openE for the message", example = "12")
	private final String messageId;

	@Schema(description = "Time and date the message was sent ", example = "2023-02-23 17:26:23")
	private final String sent;

	@Schema(description = "Username for the poster", example = "te01st")
	private final String username;

	@Schema(description = "Firstname of the poster ", example = "Test")
	private final String firstName;

	@Schema(description = "Lastname of the poster", example = "Testsson")
	private final String lastName;

	@Schema(description = "Email for the poster", example = "test@sundsvall.se")
	private final String email;

	@Schema(description = "The userId for the poster", example = "123")
	private final String userId;

	@ArraySchema(schema = @Schema(description = "List of attachments for the message", example = "attachment1, attachment2"))
	private final List<MessageAttachment> attachments;

	@Schema(description = "The instance of the message", example = "external")
	private final String instance;

}
