package se.sundsvall.webmessagecollector.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.service.MessageService;

@ExtendWith(MockitoExtension.class)
class MessageResourceTest {

	@Mock
	private MessageService service;

	@InjectMocks
	private MessageResource resource;

	@Test
	void getMessages() {
		when(service.getMessages()).thenReturn(List.of(MessageDTO.builder()
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
			.build()));

		final var result = resource.getMessages();

		assertThat(result).isNotNull();
		assertThat(result.getBody()).isNotNull();
		assertThat(result.getBody()).hasSize(1);
		final var body = result.getBody().get(0);
		assertThat(body).hasNoNullFieldsOrProperties();

		verify(service, times(1)).getMessages();
		verifyNoMoreInteractions(service);
	}

	@Test
	void deleteMessages() {
		resource.deleteMessages(List.of(1));
		verify(service, times(1)).deleteMessages(anyList());
		verifyNoMoreInteractions(service);
	}
}
