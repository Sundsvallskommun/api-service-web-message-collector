package se.sundsvall.webmessagecollector.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.DELETE;
import static se.sundsvall.webmessagecollector.TestDataFactory.createMessageDTO;

import jakarta.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.webmessagecollector.Application;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.api.model.MessageAttachment;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.service.MessageService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class MessageResourceTest {

	private static final String PATH = "/1984/messages";

	@MockitoBean
	private MessageService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getMessages() {
		when(serviceMock.getMessages("1984", "123", "EXTERNAL")).thenReturn(List.of(MessageDTO.builder()
			.withId(1)
			.withMunicipalityId("someMunicipalityId")
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
			.withInstance("someInstance")
			.withAttachments(List.of(MessageAttachment.builder().build()))
			.build()));

		var response = webTestClient.get()
			.uri(PATH + "/123/external")
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(MessageDTO.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull().hasSize(1)
			.allSatisfy(message -> assertThat(message).hasNoNullFieldsOrProperties());
		verify(serviceMock).getMessages("1984", "123", "EXTERNAL");
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void deleteMessages() {
		webTestClient.method(DELETE)
			.uri(PATH)
			.bodyValue(List.of(1, 2, 3))
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		verify(serviceMock).deleteMessages(List.of(1, 2, 3));
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void deleteAttachment() {
		webTestClient.method(DELETE)
			.uri(PATH + "/attachments/1")
			.exchange()
			.expectStatus().isNoContent()
			.expectBody().isEmpty();

		verify(serviceMock).deleteAttachment(1);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void getMessagesByFlowInstanceId() {
		when(serviceMock.getMessagesByFlowInstanceId("1984", "EXTERNAL", "123", null, null))
			.thenReturn(List.of(createMessageDTO(), createMessageDTO()));

		var response = webTestClient.get()
			.uri(PATH + "/external/flow-instances/123")
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(MessageDTO.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull().hasSize(2);
		verify(serviceMock).getMessagesByFlowInstanceId("1984", "EXTERNAL", "123", null, null);
		verifyNoMoreInteractions(serviceMock);
	}

	@Test
	void getAttachmentById() {
		webTestClient.get()
			.uri(PATH + "/external/attachments/1")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).streamAttachmentById(eq("1984"), eq("EXTERNAL"), eq(1), any(HttpServletResponse.class));
		verifyNoMoreInteractions(serviceMock);
	}
}
