package se.sundsvall.webmessagecollector.integration.opene.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalMessageAttachment {

	@JacksonXmlProperty(localName = "attachmentID")
	private int attachmentID;

	@JacksonXmlProperty(localName = "filename")
	private String fileName;

}
