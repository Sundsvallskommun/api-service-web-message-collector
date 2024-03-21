package se.sundsvall.webmessagecollector.integration.opene.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalMessage {

	private boolean postedByManager;

	private int messageID;

	private String message;

	private Poster poster;

	private String added;

	private int flowInstanceID;

	@JacksonXmlElementWrapper(localName = "attachments")
	private List<ExternalMessageAttachment> attachments;

}
