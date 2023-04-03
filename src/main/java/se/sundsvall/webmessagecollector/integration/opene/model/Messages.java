package se.sundsvall.webmessagecollector.integration.opene.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Messages {
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<ExternalMessage> ExternalMessage;
}

