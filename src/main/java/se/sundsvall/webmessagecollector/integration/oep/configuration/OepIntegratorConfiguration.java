package se.sundsvall.webmessagecollector.integration.oep.configuration;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
public class OepIntegratorConfiguration {

	public static final String CLIENT_ID = "oep-integrator";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final OepIntegratorProperties oepIntegratorProperties, final ClientRegistrationRepository clientRepository) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
			.withRequestTimeoutsInSeconds(oepIntegratorProperties.connectTimeout(), oepIntegratorProperties.readTimeout())
			.withRetryableOAuth2InterceptorForClientRegistration(clientRepository.findByRegistrationId(CLIENT_ID))
			.composeCustomizersToOne();
	}
}
