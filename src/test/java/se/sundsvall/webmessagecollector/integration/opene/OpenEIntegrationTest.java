package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

@ExtendWith(MockitoExtension.class)
class OpenEIntegrationTest {

	@Mock
	private OpenEClient clientMock;

	@Mock
	private OpenEMapper mapperMock;

	@InjectMocks
	private OpenEIntegration integration;

	@Test
	void getMessages() {
		// Arrange
		final var familyId = "familyId";
		final var from = "from";
		final var tom = "tom";
		final var response = "some response".getBytes();

		when(clientMock.getMessages(familyId, from, tom)).thenReturn(response);
		when(mapperMock.mapMessages(response, familyId)).thenReturn(List.of(MessageEntity.builder().withFamilyId(familyId).build()));

		// Act assert and verify
		assertThat(integration.getMessages(familyId, from, tom)).isEqualTo(List.of(MessageEntity.builder().withFamilyId(familyId).build()));
		verify(clientMock).getMessages(familyId, from, tom);
		verify(mapperMock).mapMessages(response, familyId);
	}

	@Test
	void getMessagesThrowsException() {
		// Arrange
		final var familyId = "familyId";
		final var from = "from";
		final var tom = "tom";

		when(clientMock.getMessages(any(), any(), any())).thenThrow(Problem.valueOf(Status.I_AM_A_TEAPOT, "Big and stout"));

		// Act, assert and verify
		assertThat(integration.getMessages(familyId, from, tom)).isEmpty();
		verify(clientMock).getMessages(familyId, from, tom);
		verify(mapperMock, never()).mapMessages(any(), any());
	}
}
