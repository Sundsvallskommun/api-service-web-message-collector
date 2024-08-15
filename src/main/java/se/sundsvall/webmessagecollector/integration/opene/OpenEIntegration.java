package se.sundsvall.webmessagecollector.integration.opene;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;

import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEProperties;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.soap.SOAPErrorDecoder;

@Component
public class OpenEIntegration {

	private final Map<ClientKey, OpenEClient> clients = new HashMap<>();

	public OpenEIntegration(final ApplicationContext applicationContext, final OpenEProperties openEProperties) {
		openEProperties.environments().forEach((municipalityId, environment) -> {
			// Create a client for the external instance, if any
			ofNullable(environment.external()).ifPresent(externalInstance -> {
				var client = createClient(applicationContext, municipalityId, externalInstance, EXTERNAL);

				clients.put(new ClientKey(municipalityId, EXTERNAL), client);
			});

			// Create a client for the internal instance, if any
			ofNullable(environment.internal()).ifPresent(internalInstance -> {
				var client = createClient(applicationContext, municipalityId, internalInstance, INTERNAL);

				clients.put(new ClientKey(municipalityId, INTERNAL), client);
			});
		});
	}

	public byte[] getMessages(final String municipalityId, final Instance instance, final String familyId, final String fromDate, final String toDate) {
		return getClient(municipalityId, instance).getMessages(familyId, fromDate, toDate);
	}

	public byte[] getAttachment(final String municipalityId, final Instance instance, final int attachmentId) {
		return getClient(municipalityId, instance).getAttachment(attachmentId);
	}

	OpenEClient getClient(final String municipalityId, final Instance instance) {
		return ofNullable(clients.get(new ClientKey(municipalityId, instance)))
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, String.format("No %s OpenE client exists for municipalityId %s", instance, municipalityId)));
	}

	OpenEClient createClient(final ApplicationContext applicationContext, final String municipalityId,
			final OpenEProperties.OpenEEnvironment.OpenEInstance environment, final Instance instance) {
		return new FeignClientBuilder(applicationContext)
			.forType(OpenEClient.class, String.format("oep-%s-%s", instance.name().toLowerCase(), municipalityId))
			.customize(builder -> builder
				.errorDecoder(new SOAPErrorDecoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(environment.username(), environment.password()))
                .options(new Request.Options(environment.connectTimeout(), SECONDS, environment.readTimeout(), SECONDS, true)))
			.url(environment.baseUrl())
			.build();
	}

	private record ClientKey(String municipalityId, Instance instance) { }
}
