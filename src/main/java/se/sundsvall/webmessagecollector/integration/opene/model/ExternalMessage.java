package se.sundsvall.webmessagecollector.integration.opene.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class ExternalMessage {
    public boolean postedByManager;
    public int messageID;
    public String message;
    public poster poster;
    public String added;
    public int flowInstanceID;
}
