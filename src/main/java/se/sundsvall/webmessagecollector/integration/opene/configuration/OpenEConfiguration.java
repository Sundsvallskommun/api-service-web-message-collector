package se.sundsvall.webmessagecollector.integration.opene.configuration;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

import feign.auth.BasicAuthRequestInterceptor;
import feign.soap.SOAPErrorDecoder;

@Import(FeignConfiguration.class)
public class OpenEConfiguration {

	public static final String CLIENT_ID = "-open-e";

	@Bean
	FeignBuilderCustomizer internalFeignBuilderCustomizer(final OpenEProperties properties) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new SOAPErrorDecoder())
			.withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
			.withRequestInterceptor(new BasicAuthRequestInterceptor(properties.username(), properties.internalPassword()))
			.composeCustomizersToOne();
	}

	@Bean
	FeignBuilderCustomizer externalFeignBuilderCustomizer(final OpenEProperties properties) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new SOAPErrorDecoder())
			.withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
			.withRequestInterceptor(new BasicAuthRequestInterceptor(properties.username(), properties.externalPassword()))
			.composeCustomizersToOne();
	}

}
