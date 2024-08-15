package se.sundsvall.webmessagecollector.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

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

		when(messageRepositoryMock.findAllByMunicipalityIdAndFamilyIdAndInstance("someMunicipalityId", "someFamilyId", Instance.INTERNAL))
			.thenReturn(List.of(entity, MessageEntity.builder().build()));

		var result = service.getMessages("someMunicipalityId", "someFamilyId", Instance.INTERNAL.name());

		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.getFirst()).hasNoNullFieldsOrProperties();
		assertThat(result.getFirst()).usingRecursiveComparison()
			.isEqualTo(MessageMapper.toMessageDTO(entity));
		// Verify
		verify(messageRepositoryMock).findAllByMunicipalityIdAndFamilyIdAndInstance("someMunicipalityId", "someFamilyId", Instance.INTERNAL);
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void getMessages_EmptyList() {
		var result = service.getMessages("someMunicipalityId", "someFamilyId", Instance.EXTERNAL.name());

		assertThat(result).isNotNull().isEmpty();
		verify(messageRepositoryMock).findAllByMunicipalityIdAndFamilyIdAndInstance("someMunicipalityId", "someFamilyId", Instance.EXTERNAL);
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void deleteMessages() {
		var list = List.of(1, 2);

		service.deleteMessages(list);

		verify(messageRepositoryMock).deleteAllById(list);
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
}
