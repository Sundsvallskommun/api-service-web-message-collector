package se.sundsvall.webmessagecollector.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class MessageAttachment {

	@Schema(description = "The Id for the attachment", examples = "1")
	private final Integer attachmentId;

	@Schema(description = "The name of the file", examples = "file.txt")
	private final String name;

	@Schema(description = "The extension of the file", examples = "txt")
	private final String extension;

	@Schema(description = "The mime type of the file", examples = "text/plain")
	private final String mimeType;

}
