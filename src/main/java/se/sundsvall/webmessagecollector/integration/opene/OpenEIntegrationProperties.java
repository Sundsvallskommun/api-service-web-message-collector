package se.sundsvall.webmessagecollector.integration.opene;

import org.springframework.boot.context.properties.ConfigurationProperties;


import se.sundsvall.webmessagecollector.integration.AbstractIntegrationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration.open-e")
class OpenEIntegrationProperties extends AbstractIntegrationProperties {
    
    private BasicAuth basicAuth = new BasicAuth();
    private int port;
    private String scheme;
    private String errandPath;
    private String messagesPath;
}
