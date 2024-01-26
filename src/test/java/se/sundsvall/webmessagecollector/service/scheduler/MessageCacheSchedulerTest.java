package se.sundsvall.webmessagecollector.service.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.OpenEIntegration;

@ExtendWith(MockitoExtension.class)
class MessageCacheSchedulerTest {

	@Mock
	private OpenEIntegration integrationMock;

	@Mock
	private MessageRepository messageRepositoryMock;

	@Mock
	private MessageCacheProperties messageCachePropertiesMock;

	@InjectMocks
	private MessageCacheScheduler scheduler;

	@Test
	void cacheMessagesWhenNoFamilyIdsDefined() {
		scheduler.cacheMessages();

		verify(messageCachePropertiesMock).familyId();
		verifyNoMoreInteractions(messageCachePropertiesMock, integrationMock, messageCachePropertiesMock);
	}

	@Test
	void cacheMessages() {
		final var messages = List.of(MessageEntity.builder().build());

		when(messageCachePropertiesMock.familyId()).thenReturn("123");
		when(integrationMock.getMessages(eq("123"), any(), eq(""))).thenReturn(messages);

		scheduler.cacheMessages();

		verify(messageCachePropertiesMock).familyId();
		verify(integrationMock).getMessages(eq("123"), any(), eq(""));
		verify(messageRepositoryMock).saveAll(messages);

		verifyNoMoreInteractions(messageCachePropertiesMock, integrationMock, messageCachePropertiesMock);
	}
}
