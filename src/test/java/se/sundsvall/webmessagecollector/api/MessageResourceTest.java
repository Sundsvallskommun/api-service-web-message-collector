package se.sundsvall.webmessagecollector.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.DELETE;

import java.time.OffsetDateTime;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.webmessagecollector.Application;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.api.model.MessageAttachment;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.service.MessageService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessageResourceTest {

	private static final String PATH = "/messages";

	@MockBean
	private MessageService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getMessages() {
		// Arrange & Mock
		when(serviceMock.getMessages(anyString())).thenReturn(List.of(MessageDTO.builder()
			.withId(1)
			.withMessageId("someMessageId")
			.withMessage("someMessage")
			.withExternalCaseId("someExternalCaseId")
			.withFamilyId("someFamilyId")
			.withDirection(Direction.OUTBOUND)
			.withSent(OffsetDateTime.now().toString())
			.withLastName("someLastName")
			.withFirstName("someFirstName")
			.withUsername("someUsername")
			.withEmail("someEmail")
			.withUserId("someUserid")
			.withAttachments(List.of(MessageAttachment.builder().build()))
			.build()));

		// Act
		final var response = webTestClient.get()
			.uri(PATH + "?familyid=123")
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(MessageDTO.class)
			.returnResult()
			.getResponseBody();

		// Assert and verify
		assertThat(response).isNotNull().hasSize(1).allSatisfy(message -> {
			assertThat(message).hasNoNullFieldsOrProperties();
		});

		verify(serviceMock).getMessages(anyString());
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void deleteMessages() {
		// Act
		webTestClient.method(DELETE)
			.uri(PATH)
			.bodyValue(List.of(1, 2, 3))
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		// Assert and verify
		verify(serviceMock).deleteMessages(List.of(1, 2, 3));
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void getAttachment() {
		// Arrange
		final var attachment = Base64.toBase64String(new byte[]{1, 2, 2, 3});
		when(serviceMock.getAttachment(1)).thenReturn(attachment);

		// Act
		final var result = webTestClient.get()
			.uri(PATH + "/attachments/1")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class)
			.returnResult().getResponseBody();

		// Assert and verify
		assertThat(result).isNotNull().isEqualTo(attachment);
		verify(serviceMock).getAttachment(1);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void deleteAttachment() {
		// Act
		webTestClient.method(DELETE)
			.uri(PATH + "/attachments/1")
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		// Assert and verify
		verify(serviceMock).deleteAttachment(1);
		verifyNoMoreInteractions(serviceMock);
	}

}
