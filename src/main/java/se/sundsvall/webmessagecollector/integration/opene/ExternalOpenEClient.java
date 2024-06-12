package se.sundsvall.webmessagecollector.integration.opene;

import static se.sundsvall.webmessagecollector.integration.opene.configuration.ExternalOpenEConfiguration.OEP_EXTERNAL_CLIENT;

import org.springframework.cloud.openfeign.FeignClient;

import se.sundsvall.webmessagecollector.integration.opene.configuration.ExternalOpenEConfiguration;

@FeignClient(name = "external" + OEP_EXTERNAL_CLIENT, url = "${integration.open-e.external.url}", configuration = ExternalOpenEConfiguration.class)
public interface ExternalOpenEClient extends OpenEClient {

}
