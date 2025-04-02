package se.sundsvall.webmessagecollector.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessage;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;
import se.sundsvall.webmessagecollector.integration.oep.OepIntegratorIntegration;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository messageRepositoryMock;

	@Mock
	private MessageAttachmentEntity messageAttachmentEntityMock;

	@Mock
	private MessageAttachmentRepository attachmentRepositoryMock;

	@Mock
	private HttpServletResponse servletResponseMock;

	@Mock
	private ServletOutputStream servletOutputStreamMock;

	@Mock
	private Blob blobMock;

	@Mock
	private OepIntegratorIntegration oepIntegratorIntegrationMock;

	@Captor
	private ArgumentCaptor<List<MessageEntity>> messagesCaptor;

	@InjectMocks
	private MessageService service;

	@Test
	void getMessages() {
		var entity = MessageEntity.builder()
			.withMunicipalityId("someMunicipalityId")
			.withMessage("someMessage")
			.withMessageId("someMessageId")
			.withId(1)
			.withSent(LocalDateTime.now())
			.withExternalCaseId("someExternalCaseId")
			.withDirection(Direction.OUTBOUND)
			.withFamilyId("someFamilyId")
			.withUserId("someUserId")
			.withUsername("someUsername")
			.withLastName("someLastname")
			.withFirstName("someFirstname")
			.withEmail("someEmail")
			.withAttachments(List.of(MessageAttachmentEntity.builder().build()))
			.build();

		when(messageRepositoryMock.findAllByMunicipalityIdAndFamilyIdAndInstanceAndStatus("someMunicipalityId", "someFamilyId", Instance.INTERNAL, MessageStatus.COMPLETE))
			.thenReturn(List.of(entity, MessageEntity.builder().build()));

		var result = service.getMessages("someMunicipalityId", "someFamilyId", Instance.INTERNAL.name());

		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.getFirst()).hasNoNullFieldsOrProperties();
		assertThat(result.getFirst()).usingRecursiveComparison()
			.isEqualTo(MessageMapper.toMessageDTO(entity));
		// Verify
		verify(messageRepositoryMock).findAllByMunicipalityIdAndFamilyIdAndInstanceAndStatus("someMunicipalityId", "someFamilyId", Instance.INTERNAL, MessageStatus.COMPLETE);
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void getMessages_EmptyList() {
		var result = service.getMessages("someMunicipalityId", "someFamilyId", Instance.EXTERNAL.name());

		assertThat(result).isNotNull().isEmpty();
		verify(messageRepositoryMock).findAllByMunicipalityIdAndFamilyIdAndInstanceAndStatus("someMunicipalityId", "someFamilyId", Instance.EXTERNAL, MessageStatus.COMPLETE);
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void deleteMessages() {
		var list = List.of(1, 2);
		var messageMock1 = mock(MessageEntity.class);
		var messageMock2 = mock(MessageEntity.class);

		when(messageRepositoryMock.findAllById(any())).thenReturn(List.of(messageMock1, messageMock2));

		service.deleteMessages(list);

		verify(messageRepositoryMock).findAllById(list);
		verify(messageMock1).setStatus(MessageStatus.DELETED);
		verify(messageMock2).setStatus(MessageStatus.DELETED);
		verify(messageRepositoryMock).saveAll(messagesCaptor.capture());
		assertThat(messagesCaptor.getValue()).hasSameElementsAs(List.of(messageMock1, messageMock2));
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void getMessageAttachmentStreamed() throws Exception {
		var attachmentId = 12;
		var content = "content";
		var contentType = "contentType";
		var fileName = "fileName";
		var inputStream = IOUtils.toInputStream(content, UTF_8);

		when(attachmentRepositoryMock.findById(any())).thenReturn(Optional.of(messageAttachmentEntityMock));
		when(messageAttachmentEntityMock.getMimeType()).thenReturn(contentType);
		when(messageAttachmentEntityMock.getName()).thenReturn(fileName);
		when(messageAttachmentEntityMock.getFile()).thenReturn(blobMock);
		when(blobMock.length()).thenReturn((long) content.length());
		when(blobMock.getBinaryStream()).thenReturn(inputStream);
		when(servletResponseMock.getOutputStream()).thenReturn(servletOutputStreamMock);

		service.getMessageAttachmentStreamed(attachmentId, servletResponseMock);

		verify(attachmentRepositoryMock).findById(attachmentId);
		verify(messageAttachmentEntityMock).getFile();
		verify(blobMock).length();
		verify(blobMock).getBinaryStream();
		verify(servletResponseMock).addHeader(CONTENT_TYPE, contentType);
		verify(servletResponseMock).addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
		verify(servletResponseMock).setContentLength(content.length());
		verify(servletResponseMock).getOutputStream();
	}

	@Test
	void getMessageAttachmentStreamedWhenAttachmentIsNotFound() {
		when(attachmentRepositoryMock.findById(any())).thenReturn(Optional.empty());

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> service.getMessageAttachmentStreamed(123, servletResponseMock))
			.satisfies(problem -> assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND));

		verify(attachmentRepositoryMock).findById(123);
		verifyNoMoreInteractions(attachmentRepositoryMock);
		verifyNoInteractions(messageAttachmentEntityMock, blobMock, servletOutputStreamMock);
	}

	@Test
	void getMessageAttachmentStreamedWhenExceptionIsThrown() {
		when(attachmentRepositoryMock.findById(any())).thenReturn(Optional.of(messageAttachmentEntityMock));
		when(messageAttachmentEntityMock.getFile()).thenAnswer((t) -> { throw new IOException(); });

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> service.getMessageAttachmentStreamed(123, servletResponseMock))
			.satisfies(problem -> assertThat(problem.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR));

		verify(attachmentRepositoryMock).findById(123);
		verify(messageAttachmentEntityMock).getFile();
		verifyNoMoreInteractions(attachmentRepositoryMock, messageAttachmentEntityMock);
		verifyNoInteractions(blobMock, servletOutputStreamMock);
	}

	@Test
	void deleteAttachment() {
		service.deleteAttachment(1);

		verify(attachmentRepositoryMock).deleteById(1);
		verifyNoMoreInteractions(attachmentRepositoryMock);
		verifyNoInteractions(messageRepositoryMock);
	}

	@Test
	void getMessageByFlowInstanceId() {
		var municipalityId = "municipalityId";
		var instance = "EXTERNAL";
		var flowInstanceId = "flowInstanceId";
		var from = LocalDateTime.now();
		var to = LocalDateTime.now();
		var webmessage = createWebMessage();

		when(oepIntegratorIntegrationMock.getWebmessagesByFlowInstanceId(municipalityId, Instance.valueOf(instance), flowInstanceId, from, to))
			.thenReturn(List.of(webmessage));

		var result = service.getMessagesByFlowInstanceId(municipalityId, instance, flowInstanceId, from, to);

		assertThat(result).isNotNull().hasSize(1);
		verify(oepIntegratorIntegrationMock).getWebmessagesByFlowInstanceId(municipalityId, Instance.valueOf(instance.toUpperCase()), flowInstanceId, from, to);
	}

	@Test
	void streamAttachmentById() {
		var municipalityId = "municipalityId";
		var instance = "EXTERNAL";
		var attachmentId = 123;
		var mockHttpServletResponse = new MockHttpServletResponse();
		final var headers = Map.of(
			"Content-Type", List.of("application/pdf"),
			"Content-Disposition", List.of("attachment; filename=case.pdf"),
			"Content-Length", List.of("0"),
			"Last-Modified", List.of("Wed, 21 Oct 2015 07:28:00 GMT"));
		final var inputStreamResource = new InputStreamResource(new ByteArrayInputStream(new byte[10]));
		final var responseEntity = ResponseEntity.ok()
			.headers(httpHeaders -> httpHeaders.putAll(headers))
			.body(inputStreamResource);

		when(oepIntegratorIntegrationMock.getAttachmentStreamById(municipalityId, Instance.valueOf(instance), "flowInstanceId", attachmentId))
			.thenReturn(responseEntity);

		service.streamAttachmentById(municipalityId, instance, attachmentId, mockHttpServletResponse);

		assertThat(mockHttpServletResponse.getHeader("Content-Type")).isEqualTo("application/pdf");
		assertThat(mockHttpServletResponse.getHeader("Content-Disposition")).isEqualTo("attachment; filename=case.pdf");
		assertThat(mockHttpServletResponse.getHeader("Content-Length")).isEqualTo("0");
		assertThat(mockHttpServletResponse.getHeader("Last-Modified")).isEqualTo("Wed, 21 Oct 2015 07:28:00 GMT");

	}

}
