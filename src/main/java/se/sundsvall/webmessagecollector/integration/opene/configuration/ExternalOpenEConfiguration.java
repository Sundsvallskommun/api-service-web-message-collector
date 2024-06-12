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

public class ExternalOpenEConfiguration {


	static final JAXBContextFactory JAXB_FACTORY = new JAXBContextFactory.Builder().build();

	static final SOAPEncoder.Builder SOAP_ENCODER_BUILDER = new SOAPEncoder.Builder()
		.withFormattedOutput(false)
		.withJAXBContextFactory(JAXB_FACTORY)
		.withSOAPProtocol(SOAPConstants.SOAP_1_1_PROTOCOL)
		.withWriteXmlDeclaration(true);

	public static final String OEP_EXTERNAL_CLIENT = "oep-external";
	@Bean
	FeignBuilderCustomizer externalFeignBuilderCustomizer(final OpenEProperties properties) {
		return FeignMultiCustomizer.create()
			.withDecoder(new SOAPDecoder(JAXB_FACTORY))
			.withEncoder(SOAP_ENCODER_BUILDER.build())
			.withErrorDecoder(new SOAPErrorDecoder())
			.withRequestInterceptor(new BasicAuthRequestInterceptor(properties.username(), properties.externalPassword()))
			.withRequestTimeoutsInSeconds(properties.connectTimeout(), properties.readTimeout())
			.composeCustomizersToOne();
	}

}
