package se.sundsvall.webmessagecollector.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository repository;

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
			.build();
		// Mock
		when(repository.findAllByFamilyId(anyString()))
			.thenReturn(List.of(entity, MessageEntity.builder().build()));
		// Act
		final var result = service.getMessages("someFamilyId");
		// Assert
		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.getFirst()).hasNoNullFieldsOrProperties();
		assertThat(result.getFirst()).usingRecursiveComparison()
			.isEqualTo(MessageMapper.toDTO(entity));
		// Verify
		verify(repository).findAllByFamilyId(anyString());
		verifyNoMoreInteractions(repository);
	}

	@Test
	void getMessages_EmptyList() {
		// Act
		final var result = service.getMessages("someFamilyId");
		// Assert & Verify
		assertThat(result).isNotNull().isEmpty();
		verify(repository).findAllByFamilyId(anyString());
		verifyNoMoreInteractions(repository);
	}

	@Test
	void deleteMessages() {
		//Act
		service.deleteMessages(List.of(1));
		//Verify
		verify(repository).deleteAllById(anyList());
		verifyNoMoreInteractions(repository);
	}
}
