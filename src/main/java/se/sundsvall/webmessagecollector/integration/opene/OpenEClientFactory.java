package se.sundsvall.webmessagecollector.integration.opene;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.soap.SOAPErrorDecoder;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEProperties;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@Component
class OpenEClientFactory {

	private static final Logger LOG = LoggerFactory.getLogger(OpenEClientFactory.class);

	private final Map<ClientKey, OpenEClient> clients = new HashMap<>();

	OpenEClientFactory(final ApplicationContext applicationContext, final OpenEProperties openEProperties) {
		openEProperties.environments().forEach((municipalityId, environment) -> {
			// Create a client for the external instance, if any
			ofNullable(environment.external()).ifPresent(externalInstance -> createClient(applicationContext, municipalityId, externalInstance, EXTERNAL));

			// Create a client for the internal instance, if any
			ofNullable(environment.internal()).ifPresent(internalInstance -> createClient(applicationContext, municipalityId, internalInstance, INTERNAL));
		});
	}

	OpenEClient getClient(final String municipalityId, final Instance instance) {
		return ofNullable(clients.get(new ClientKey(municipalityId, instance)))
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, String.format("No %s OpenE client exists for municipalityId %s", instance, municipalityId)));
	}

	void createClient(final ApplicationContext applicationContext, final String municipalityId,
		final OpenEProperties.OpenEEnvironment.OpenEInstance environment, final Instance instance) {
		var clientName = "oep-%s-%s".formatted(instance.name().toLowerCase(), municipalityId);
		var client = new FeignClientBuilder(applicationContext)
			.forType(OpenEClient.class, clientName)
			.customize(builder -> builder
				.errorDecoder(new SOAPErrorDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(environment.username(), environment.password()))
				.options(new Request.Options(environment.connectTimeout(), SECONDS, environment.readTimeout(), SECONDS, true)))
			.url(environment.baseUrl())
			.build();
		clients.put(new ClientKey(municipalityId, instance), client);

		LOG.info("Created {} OpenE client for municipalityId {} ({})", instance, municipalityId, clientName);
	}

	private record ClientKey(String municipalityId, Instance instance) {}
}
