package se.sundsvall.webmessagecollector.integration.opene;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import se.sundsvall.webmessagecollector.integration.opene.exception.OpenEException;

@Component
class OpenEClient {
    private final OpenEIntegrationProperties properties;
    
    private final CloseableHttpClient httpClient;
    
    OpenEClient(OpenEIntegrationProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClients.custom()
            .setDefaultCredentialsProvider(getCredentials())
            .build();
    }
    
    byte[] getMessages(String familyId, String fromDate, String toDate) throws IOException {
        return getBytes(buildUrl(properties.getMessagesPath(), Map.of("familyid", familyId), fromDate, toDate));
    }

    private byte[] getBytes(URI url) throws IOException, OpenEException {
        return httpClient.execute(new HttpGet(url), responseHandler -> {
            if (responseHandler.getCode() == 200) {
                return EntityUtils.toByteArray(responseHandler.getEntity());
            }

            throw new OpenEException(responseHandler.toString());
        });
    }
    
    private URI buildUrl(String path, Map<String, String> parameters, String fromDate, String toDate) {
        return UriComponentsBuilder.newInstance()
            .scheme(properties.getScheme())
            .host(properties.getBaseUrl())
            .port(properties.getPort())
            .path(path)
            .queryParam("fromDate", fromDate)
            .queryParam("toDate", toDate)
            .build(parameters);
    }
    
    private BasicCredentialsProvider getCredentials() {
        var user = properties.getBasicAuth().getUsername();
        var password = properties.getBasicAuth().getPassword().toCharArray();
        var credProvider = new BasicCredentialsProvider();
        
        credProvider.setCredentials(new AuthScope(null, -1),
            new UsernamePasswordCredentials(user, password));
        return credProvider;
    }
}
