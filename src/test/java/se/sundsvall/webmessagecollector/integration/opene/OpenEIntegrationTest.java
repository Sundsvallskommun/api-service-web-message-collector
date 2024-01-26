package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;

@ExtendWith({ MockitoExtension.class, ResourceLoaderExtension.class })
class OpenEIntegrationTest {

	@Spy
	private OpenEMapper mapper;

	@Mock
	private OpenEClient client;

	@InjectMocks
	private OpenEIntegration integration;

	@Test
	void getMessages(@Load("/messages.xml") final String input) throws IOException {

		final var bytes = input.getBytes(StandardCharsets.ISO_8859_1);

		when(client.getMessages(any(String.class), any(String.class), any(String.class))).thenReturn(bytes);

		final var result = integration.getMessages("", "", "");

		assertThat(result).isNotNull();
		assertThat(result.size()).isNotZero();
		assertThat(result.get(0)).hasNoNullFieldsOrPropertiesExcept("id");

		verify(client, times(1))
			.getMessages(any(String.class), any(String.class), any(String.class));

		verify(mapper).mapMessages(any(byte[].class), any(String.class));
	}

	@Test
	void getMessages_OpenEReturnsNull() throws IOException {

		when(client.getMessages(any(String.class), any(String.class), any(String.class))).thenReturn(null);

		final var result = integration.getMessages("", "", "");

		assertThat(result).isNotNull().isEmpty();

		verify(client, times(1))
			.getMessages(any(String.class), any(String.class), any(String.class));

		verify(mapper).mapMessages(any(), any(String.class));
	}

	@Test
	void getMessages_throwsException() throws IOException {

		when(client.getMessages(any(String.class), any(String.class), any(String.class)))
			.thenThrow(new IOException());

		final var result = integration.getMessages("", "", "");

		assertThat(result).isNotNull().isEmpty();

		verify(client).getMessages(any(String.class), any(String.class), any(String.class));

		verifyNoInteractions(mapper);

	}
}
