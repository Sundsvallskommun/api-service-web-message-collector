package se.sundsvall.webmessagecollector.integration.opene;

import static se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;

import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEConfiguration;

@FeignClient(name = "external" + CLIENT_ID, url = "${integration.open-e.external.url}", configuration = OpenEConfiguration.class, qualifiers = "externalFeignBuilderCustomizer")
public interface ExternalOpenEClient extends OpenEClient {

}
