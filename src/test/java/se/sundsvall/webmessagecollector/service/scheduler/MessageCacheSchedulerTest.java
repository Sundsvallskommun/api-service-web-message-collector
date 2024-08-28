package se.sundsvall.webmessagecollector.service.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEProperties;

@ExtendWith(MockitoExtension.class)
class MessageCacheSchedulerTest {

	@Nested
	class CacheMessagesTaskTest {

		private static final String MUNICIPALITY_ID = "1984";

		private MessageCacheService messageCacheServiceMock;
		private MessageEntity messageEntityMock;
		private OpenEProperties.OpenEEnvironment.OpenEInstance externalInstanceMock;
		private OpenEProperties.OpenEEnvironment.OpenEInstance internalInstanceMock;
		private OpenEProperties.OpenEEnvironment openEEnvironmentMock;
		private MessageCacheScheduler.CacheMessagesTask cacheMessagesTask;

		@BeforeEach
		void setUp() {
			messageCacheServiceMock = mock(MessageCacheService.class);
			messageEntityMock = mock(MessageEntity.class);
			externalInstanceMock = mock(OpenEProperties.OpenEEnvironment.OpenEInstance.class);
			internalInstanceMock = mock(OpenEProperties.OpenEEnvironment.OpenEInstance.class);
			openEEnvironmentMock = mock(OpenEProperties.OpenEEnvironment.class);

			cacheMessagesTask = new MessageCacheScheduler.CacheMessagesTask(messageCacheServiceMock, MUNICIPALITY_ID, openEEnvironmentMock);
		}

		@Test
		void run() {
			when(openEEnvironmentMock.external()).thenReturn(externalInstanceMock);
			when(openEEnvironmentMock.internal()).thenReturn(internalInstanceMock);
			when(externalInstanceMock.familyIds()).thenReturn(List.of("123", "456"));
			when(internalInstanceMock.familyIds()).thenReturn(List.of("789"));
			when(messageCacheServiceMock.fetchMessages(any(), any(), any())).thenReturn(List.of(messageEntityMock));
			when(messageEntityMock.getAttachments()).thenReturn(List.of(MessageAttachmentEntity.builder().build()));

			cacheMessagesTask.run();

			verify(messageCacheServiceMock, times(2)).fetchMessages(eq(MUNICIPALITY_ID), eq(EXTERNAL), any());
			verify(messageCacheServiceMock, times(1)).fetchMessages(eq(MUNICIPALITY_ID), eq(INTERNAL), any());
			verify(messageCacheServiceMock, times(3)).fetchAttachment(eq(MUNICIPALITY_ID), any(), any());
			verify(externalInstanceMock, times(2)).familyIds();
			verify(internalInstanceMock, times(2)).familyIds();
			verify(openEEnvironmentMock).external();
			verify(openEEnvironmentMock).internal();
			verifyNoMoreInteractions(messageCacheServiceMock, externalInstanceMock, internalInstanceMock, openEEnvironmentMock);
		}
	}
}
