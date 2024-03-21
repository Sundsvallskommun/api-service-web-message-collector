package se.sundsvall.webmessagecollector.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.hc.client5.http.utils.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private MessageAttachmentRepository attachmentRepository;

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
		when(messageRepository.findAllByFamilyId(anyString()))
			.thenReturn(List.of(entity, MessageEntity.builder().build()));
		// Act
		final var result = service.getMessages("someFamilyId");
		// Assert
		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.getFirst()).hasNoNullFieldsOrProperties();
		assertThat(result.getFirst()).usingRecursiveComparison()
			.isEqualTo(MessageMapper.toMessageDTO(entity));
		// Verify
		verify(messageRepository).findAllByFamilyId(anyString());
		verifyNoMoreInteractions(messageRepository);
	}

	@Test
	void getMessages_EmptyList() {
		// Act
		final var result = service.getMessages("someFamilyId");
		// Assert & Verify
		assertThat(result).isNotNull().isEmpty();
		verify(messageRepository).findAllByFamilyId(anyString());
		verifyNoMoreInteractions(messageRepository);
	}

	@Test
	void deleteMessages() {
		//Act
		service.deleteMessages(List.of(1));
		//Verify
		verify(messageRepository).deleteAllById(anyList());
		verifyNoMoreInteractions(messageRepository);
	}

	@Test
	void getAttachment() throws SQLException {
		// Arrange
		final var attachmentId = 1;
		final var file = new SerialBlob(new byte[]{1, 2, 3});
		final var fileName = "fileName";
		final var entity = MessageAttachmentEntity.builder()
			.withFileName(fileName)
			.withFile(file)
			.withAttachmentId(attachmentId)
			.build();
		// Mock
		when(attachmentRepository.findById(1)).thenReturn(Optional.ofNullable(entity));
		// Act
		final var result = service.getAttachment(attachmentId);
		// Assert
		assertThat(result).isNotNull().isEqualTo(Base64.encodeBase64String(file.getBytes(1, (int) file.length())));
		// Verify
		verify(attachmentRepository).findById(attachmentId);
		verifyNoMoreInteractions(attachmentRepository);
		verifyNoInteractions(messageRepository);
	}

	@Test
	void getAttachment_NotFound() {
		// Mock
		when(attachmentRepository.findById(1)).thenReturn(Optional.empty());
		// Act
		assertThatThrownBy(() -> service.getAttachment(1))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Not Found: Attachment not found");
		// Verify
		verify(attachmentRepository).findById(1);
		verifyNoMoreInteractions(attachmentRepository);
		verifyNoInteractions(messageRepository);
	}

	@Test
	void getAttachment_EmptyFile() throws SQLException {
		// Arrange
		final var attachmentId = 1;
		final var entity = MessageAttachmentEntity.builder()
			.withFileName("fileName")
			.withFile(new SerialBlob(new byte[0]))
			.withAttachmentId(attachmentId)
			.build();
		// Mock
		when(attachmentRepository.findById(1)).thenReturn(Optional.ofNullable(entity));
		// Act & Assert
		assertThatThrownBy(() -> service.getAttachment(attachmentId))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("Internal Server Error: Could not fetch attachment");
		// Verify
		verify(attachmentRepository).findById(attachmentId);
		verifyNoMoreInteractions(attachmentRepository);
		verifyNoInteractions(messageRepository);
	}

	@Test
	void deleteAttachment() {
		//Act
		service.deleteAttachment(1);
		//Verify
		verify(attachmentRepository).deleteById(1);
		verifyNoMoreInteractions(attachmentRepository);
		verifyNoInteractions(messageRepository);
	}

}
