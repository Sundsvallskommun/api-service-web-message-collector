package se.sundsvall.webmessagecollector.integration;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractIntegrationProperties {
    
    private String baseUrl;
    private Duration readTimeout = Duration.ofSeconds(300);
    private Duration connectTimeout = Duration.ofSeconds(30);
    
    @Getter
    @Setter
    public static final class BasicAuth {
        
        private String username;
        private String password;
    }
    
    @Getter
    @Setter
    public static final class OAuth2 {
        
        private String tokenUri;
        private String clientId;
        private String clientSecret;
        private String grantType = "client_credentials";
    }
}
