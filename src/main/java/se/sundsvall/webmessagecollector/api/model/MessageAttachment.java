package se.sundsvall.webmessagecollector.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachment {

	@Schema(description = "The Id for the attachment", examples = "1")
	private Integer attachmentId;

	@Schema(description = "The name of the file", examples = "file.txt")
	private String name;

	@Schema(description = "The extension of the file", examples = "txt")
	private String extension;

	@Schema(description = "The mime type of the file", examples = "text/plain")
	private String mimeType;

}
