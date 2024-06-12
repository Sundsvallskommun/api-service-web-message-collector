package se.sundsvall.webmessagecollector.integration.opene.configuration;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.soap.SOAPErrorDecoder;

@ExtendWith(MockitoExtension.class)
class InternalOpenEConfigurationTest {

	@Mock
	private OpenEProperties propertiesMock;

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Captor
	private ArgumentCaptor<ErrorDecoder> errorDecoderCaptor;

	@Captor
	private ArgumentCaptor<RequestInterceptor> requestInterceptorCaptor;

	@InjectMocks
	private InternalOpenEConfiguration configuration;

	@Test
	void testInternalFeignBuilderCustomizer() {

		final var connectTimeout = 123;
		final var readTimeout = 321;
		final var username = "myUsername";
		final var password = "myPassword";
		final var encodedAuth = new String(Base64.encode((username + ":" + password).getBytes()), ISO_8859_1);

		when(propertiesMock.connectTimeout()).thenReturn(connectTimeout);
		when(propertiesMock.readTimeout()).thenReturn(readTimeout);
		when(propertiesMock.username()).thenReturn(username);
		when(propertiesMock.internalPassword()).thenReturn(password);

		// Mock static FeignMultiCustomizer to enable spy and to verify that static method is being called
		try (final MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			configuration.internalFeignBuilderCustomizer(propertiesMock);

			feignMultiCustomizerMock.verify(FeignMultiCustomizer::create);
		}

		// Verifications
		verify(propertiesMock).connectTimeout();
		verify(propertiesMock).readTimeout();
		verify(feignMultiCustomizerSpy).withErrorDecoder(errorDecoderCaptor.capture());
		verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(connectTimeout, readTimeout);
		verify(feignMultiCustomizerSpy).withRequestInterceptor(requestInterceptorCaptor.capture());
		verify(feignMultiCustomizerSpy).composeCustomizersToOne();

		// Assert captors
		assertThat(errorDecoderCaptor.getValue())
			.isInstanceOf(SOAPErrorDecoder.class);

		assertThat(requestInterceptorCaptor.getValue()).isInstanceOf(BasicAuthRequestInterceptor.class)
			.hasFieldOrPropertyWithValue("headerValue", "Basic " + encodedAuth);
	}

}
