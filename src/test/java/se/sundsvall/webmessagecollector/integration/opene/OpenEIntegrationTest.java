package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@ExtendWith(MockitoExtension.class)
class OpenEIntegrationTest {

	@Mock
	ExternalOpenEClient externalOpenEClient;

	@Mock
	private InternalOpenEClient internalOpenEClient;

	@InjectMocks
	private OpenEIntegration openEIntegration;

	@Test
	void getInternalMessages() {

		// Arrange
		final var instance = Instance.INTERNAL;
		final var familyId = "123";
		final var fromDate = LocalDate.now().minusDays(30).toString();
		final var toDate = LocalDate.now().toString();

		when(internalOpenEClient.getMessages(familyId, fromDate, toDate)).thenReturn(new byte[0]);

		// Act
		final var result = openEIntegration.getMessages(instance, familyId, fromDate, toDate);

		// Assert
		assertThat(result).isNotNull();
		verify(internalOpenEClient).getMessages(familyId, fromDate, toDate);
		verifyNoInteractions(externalOpenEClient);
	}

	@Test
	void getExternalMessages() {

		// Arrange
		final var instance = Instance.EXTERNAL;
		final var familyId = "123";
		final var fromDate = LocalDate.now().minusDays(30).toString();
		final var toDate = LocalDate.now().toString();

		when(externalOpenEClient.getMessages(familyId, fromDate, toDate)).thenReturn(new byte[0]);

		// Act
		final var result = openEIntegration.getMessages(instance, familyId, fromDate, toDate);

		// Assert
		assertThat(result).isNotNull();
		verify(externalOpenEClient).getMessages(familyId, fromDate, toDate);
		verifyNoInteractions(internalOpenEClient);
	}


	@Test
	void getInternalAttachment() {

		// Arrange
		final var instance = Instance.INTERNAL;
		final var attachmentId = 123;

		when(internalOpenEClient.getAttachment(attachmentId)).thenReturn(new byte[0]);

		// Act
		final var result = openEIntegration.getAttachment(instance, attachmentId);

		// Assert
		assertThat(result).isNotNull();
		verify(internalOpenEClient).getAttachment(attachmentId);
		verifyNoInteractions(externalOpenEClient);
	}

	@Test
	void getExternalAttachment() {

		// Arrange
		final var instance = Instance.EXTERNAL;
		final var attachmentId = 123;

		when(externalOpenEClient.getAttachment(attachmentId)).thenReturn(new byte[0]);

		// Act
		final var result = openEIntegration.getAttachment(instance, attachmentId);

		// Assert
		assertThat(result).isNotNull();
		verify(externalOpenEClient).getAttachment(attachmentId);
		verifyNoInteractions(internalOpenEClient);
	}

}
