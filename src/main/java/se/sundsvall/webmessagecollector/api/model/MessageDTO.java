package se.sundsvall.webmessagecollector.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class MessageDTO {

	@Schema(description = "The webMessageCollector Id for the message", examples = "1")
	private final Integer id;

	@Enumerated(EnumType.STRING)
	@Schema(description = "If the message is inbound or outbound from the perspective of " +
		"case-data/e-service.", examples = "INBOUND")
	private final Direction direction;

	@Schema(description = "The municipality id", examples = "2281")
	private final String municipalityId;

	@Schema(description = "What E-service the message was found in", examples = "501")
	private final String familyId;

	@Schema(description = "The external caseID ", examples = "caa230c6-abb4-4592-ad9a-34e263c2787b")
	private final String externalCaseId;

	@Schema(description = "The message ", examples = "Hello World")
	private final String message;

	@Schema(description = "The unique messageId from openE for the message", examples = "12")
	private final String messageId;

	@Schema(description = "Time and date the message was sent ", examples = "2023-02-23 17:26:23")
	private final String sent;

	@Schema(description = "Username for the poster", examples = "te01st")
	private final String username;

	@Schema(description = "Firstname of the poster ", examples = "Test")
	private final String firstName;

	@Schema(description = "Lastname of the poster", examples = "Testsson")
	private final String lastName;

	@Schema(description = "Email for the poster", examples = "test@sundsvall.se")
	private final String email;

	@Schema(description = "The userId for the poster", examples = "123")
	private final String userId;

	@ArraySchema(schema = @Schema(description = "List of attachments for the message", examples = "attachment1, attachment2"))
	private final List<MessageAttachment> attachments;

	@Schema(description = "The instance of the message", examples = "external")
	private final String instance;

}
