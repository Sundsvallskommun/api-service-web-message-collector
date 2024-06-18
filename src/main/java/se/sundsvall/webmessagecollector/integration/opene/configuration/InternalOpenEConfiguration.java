package se.sundsvall.webmessagecollector.integration.opene.configuration;

import jakarta.xml.soap.SOAPConstants;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

import feign.auth.BasicAuthRequestInterceptor;
import feign.jaxb.JAXBContextFactory;
import feign.soap.SOAPDecoder;
import feign.soap.SOAPEncoder;
import feign.soap.SOAPErrorDecoder;

public class InternalOpenEConfiguration {

	public static final String OEP_INTERNAL_CLIENT = "oep-internal";

	@Bean
	FeignBuilderCustomizer internalFeignBuilderCustomizer(final OpenEProperties properties) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new SOAPErrorDecoder())
			.withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
			.withRequestInterceptor(new BasicAuthRequestInterceptor(properties.username(), properties.internalPassword()))
			.composeCustomizersToOne();
	}

}
