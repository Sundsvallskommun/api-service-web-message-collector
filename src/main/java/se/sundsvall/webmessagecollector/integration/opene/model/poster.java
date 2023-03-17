package se.sundsvall.webmessagecollector.integration.opene.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class poster {
    public int userID;
    public String username;
    public String firstname;
    public String lastname;
    public String email;
}
