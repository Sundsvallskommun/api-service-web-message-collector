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
	private final String name;

	@Schema(description = "The extension of the file", example = "txt")
	private final String extension;

	@Schema(description = "The mime type of the file", example = "text/plain")
	private final String mimeType;

}
