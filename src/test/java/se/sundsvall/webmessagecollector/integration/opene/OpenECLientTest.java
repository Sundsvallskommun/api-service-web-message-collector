package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.dockerjava.transport.DockerHttpClient.Request.Method;

import se.sundsvall.webmessagecollector.integration.AbstractIntegrationProperties.BasicAuth;

@ExtendWith(MockitoExtension.class)
class OpenECLientTest {
	@Mock
	private CloseableHttpClient httpClientMock;

	@Mock
	private OpenEIntegrationProperties propertiesMock;

	@Mock
	private BasicAuth basicAuthMock;

	@Mock
	private HttpClientBuilder httpClientBuilderMock;

	@Captor
	private ArgumentCaptor<HttpGet> httpGetCaptor;

	private OpenEClient openEClient;

	@Test
	@SuppressWarnings("unchecked")
	void getMessages() throws IOException {
		// Arrange
		final String password = "password";
		final String username = "username";
		final String scheme = "https";
		final String baseUrl = "baseurl.com";
		final int port = 443;
		final String messagePath = "messagePath/{familyid}";
		final var familyId = "123456";
		final var fromDate = "dateFrom";
		final var toDate = "dateTo";

		when(propertiesMock.getBaseUrl()).thenReturn(baseUrl);
		when(propertiesMock.getMessagesPath()).thenReturn(messagePath);
		when(propertiesMock.getPort()).thenReturn(port);
		when(propertiesMock.getScheme()).thenReturn(scheme);
		when(propertiesMock.getBasicAuth()).thenReturn(basicAuthMock);
		when(basicAuthMock.getPassword()).thenReturn(password);
		when(basicAuthMock.getUsername()).thenReturn(username);

		// Need to use static mocking for this class
		try (MockedStatic<HttpClients> httpClientsMock = Mockito.mockStatic(HttpClients.class)) {
			httpClientsMock.when(() -> HttpClients.custom()).thenReturn(httpClientBuilderMock);
			when(httpClientBuilderMock.setDefaultCredentialsProvider(any())).thenReturn(httpClientBuilderMock);
			when(httpClientBuilderMock.build()).thenReturn(httpClientMock);

			// Act
			openEClient = new OpenEClient(propertiesMock);
			openEClient.getMessages(familyId, fromDate, toDate);

			// Assert and verify
			verify(propertiesMock).getBaseUrl();
			verify(propertiesMock, times(2)).getBasicAuth();
			verify(propertiesMock).getMessagesPath();
			verify(propertiesMock).getPort();
			verify(propertiesMock).getScheme();
			verify(basicAuthMock).getPassword();
			verify(basicAuthMock).getUsername();
			verify(httpClientMock).execute(httpGetCaptor.capture(), any(HttpClientResponseHandler.class));

			final var httpGet = httpGetCaptor.getValue();
			assertThat(httpGet.getMethod()).isEqualTo(Method.GET.toString());
			assertThat(httpGet.getScheme()).isEqualTo(scheme);
			assertThat(httpGet.getPath()).isEqualTo("/messagePath/" + familyId + "?fromDate=" + fromDate + "&toDate=" + toDate);
			assertThat(httpGet.getAuthority()).isNotNull().satisfies(auth -> {
				assertThat(auth.getHostName()).isEqualTo(baseUrl);
				assertThat(auth.getPort()).isEqualTo(port);
			});
		} ;
	}
}
