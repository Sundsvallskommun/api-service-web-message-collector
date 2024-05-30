package se.sundsvall.webmessagecollector.integration.opene;

import static se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;

import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEConfiguration;

@FeignClient(name = "internal" + CLIENT_ID, url = "${integration.open-e.internal.url}", configuration = OpenEConfiguration.class, qualifiers = "internalFeignBuilderCustomizer")
public interface InternalOpenEClient extends OpenEClient {

}
