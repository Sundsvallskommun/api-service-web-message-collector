package se.sundsvall.webmessagecollector.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class MessageAttachment {

	@Schema(description = "The Id for the attachment", example = "1")
	private final Integer attachmentId;

	@Schema(description = "The name of the file", example = "file.txt")
	private final String fileName;

	@Schema(description = "The file as a base64 encoded string", example = "dGhpcyBpcyBhIGJhc2U2NCBlbmNvZGVkIHN0cmluZw==")
	private final String file;

}
