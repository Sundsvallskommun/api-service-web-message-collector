package se.sundsvall.webmessagecollector.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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

		when(repository.findAll())
			.thenReturn(List.of(entity, MessageEntity.builder().build()));

		final var result = service.getMessages();

		assertThat(result).isNotNull().hasSize(2);
		assertThat(result.get(0)).hasNoNullFieldsOrProperties();
		assertThat(result.get(0)).usingRecursiveComparison()
			.isEqualTo(MessageMapper.toDTO(entity));

		verify(repository).findAll();
		verifyNoMoreInteractions(repository);
	}

	@Test
	void getMessages_EmptyList() {
		final var result = service.getMessages();

		assertThat(result).isNotNull().isEmpty();

		verify(repository).findAll();
		verifyNoMoreInteractions(repository);
	}

	@Test
	void deleteMessages() {
		service.deleteMessages(List.of(1));
		verify(repository).deleteAllById(anyList());
		verifyNoMoreInteractions(repository);
	}
}
