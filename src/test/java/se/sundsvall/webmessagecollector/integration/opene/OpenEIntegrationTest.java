package se.sundsvall.webmessagecollector.integration.opene;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.webmessagecollector.api.model.Direction;

@ExtendWith({MockitoExtension.class, ResourceLoaderExtension.class})
class OpenEIntegrationTest {

	@Mock
	private OpenEClient clientMock;

	@InjectMocks
	private OpenEIntegration integration;

	@Test
	void getMessages(@Load("/messages.xml") final String input) {
		// Arrange
		final var familyId = "familyId";
		final var from = "from";
		final var tom = "tom";
		final var bytes = input.getBytes(ISO_8859_1);

		when(clientMock.getMessages(familyId, from, tom)).thenReturn(bytes);

		// Act
		final var result = integration.getMessages(familyId, from, tom);

		// Assert and verify
		assertThat(result).hasSize(1)
			.allSatisfy(entity -> {
				assertThat(entity.getDirection()).isEqualTo(Direction.INBOUND);
				assertThat(entity.getEmail()).isEqualTo("email_1@test.se");
				assertThat(entity.getExternalCaseId()).isEqualTo("102251");
				assertThat(entity.getFamilyId()).isEqualTo(familyId);
				assertThat(entity.getFirstName()).isEqualTo("firstName_1");
				assertThat(entity.getId()).isNull();
				assertThat(entity.getLastName()).isEqualTo("lastName_1");
				assertThat(entity.getMessage()).isEqualTo("Inbound message");
				assertThat(entity.getMessageId()).isEqualTo("10");
				assertThat(entity.getSent()).isEqualTo(LocalDateTime.of(2022, 5, 25, 11, 20));
				assertThat(entity.getUserId()).isEqualTo("1");
				assertThat(entity.getUsername()).isEqualTo("userName_1");
			});

		verify(clientMock).getMessages(familyId, from, tom);
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
	}
}
