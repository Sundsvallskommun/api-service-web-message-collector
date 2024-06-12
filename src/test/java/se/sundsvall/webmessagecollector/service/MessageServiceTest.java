package se.sundsvall.webmessagecollector.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

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
		// Arrange
		final var entity = MessageEntity.builder()
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
		// Mock
		when(messageRepositoryMock.findAllByFamilyIdAndInstance("someFamilyId", Instance.INTERNAL))
			.thenReturn(List.of(entity, MessageEntity.builder().build()));
		// Act
		final var result = service.getMessages("someFamilyId", Instance.INTERNAL.name());
		// Assert
		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.getFirst()).hasNoNullFieldsOrProperties();
		assertThat(result.getFirst()).usingRecursiveComparison()
			.isEqualTo(MessageMapper.toMessageDTO(entity));
		// Verify
		verify(messageRepositoryMock).findAllByFamilyIdAndInstance("someFamilyId", Instance.INTERNAL);
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void getMessages_EmptyList() {
		// Act
		final var result = service.getMessages("someFamilyId", Instance.EXTERNAL.name());
		// Assert & Verify
		assertThat(result).isNotNull().isEmpty();
		verify(messageRepositoryMock).findAllByFamilyIdAndInstance("someFamilyId", Instance.EXTERNAL);
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void deleteMessages() {
		//Arrange
		final var list = List.of(1, 2);
		//Act
		service.deleteMessages(list);
		//Verify
		verify(messageRepositoryMock).deleteAllById(list);
		verifyNoMoreInteractions(messageRepositoryMock);
	}

	@Test
	void getMessageAttachmentStreamed() throws Exception {
		final var attachmentId = 12;
		final var content = "content";
		final var contentType = "contentType";
		final var fileName = "fileName";
		final var inputStream = IOUtils.toInputStream(content, UTF_8);

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
	void deleteAttachment() {
		//Act
		service.deleteAttachment(1);
		//Verify
		verify(attachmentRepositoryMock).deleteById(1);
		verifyNoMoreInteractions(attachmentRepositoryMock);
		verifyNoInteractions(messageRepositoryMock);
	}

}
