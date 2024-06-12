package se.sundsvall.webmessagecollector.integration.opene;

import static se.sundsvall.webmessagecollector.integration.opene.configuration.InternalOpenEConfiguration.OEP_INTERNAL_CLIENT;

import org.springframework.cloud.openfeign.FeignClient;

import se.sundsvall.webmessagecollector.integration.opene.configuration.InternalOpenEConfiguration;

@FeignClient(name = "internal" + OEP_INTERNAL_CLIENT, url = "${integration.open-e.internal.url}", configuration = InternalOpenEConfiguration.class)
public interface InternalOpenEClient extends OpenEClient {

}
