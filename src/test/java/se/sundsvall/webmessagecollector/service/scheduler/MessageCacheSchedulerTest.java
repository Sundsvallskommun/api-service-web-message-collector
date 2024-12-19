package se.sundsvall.webmessagecollector.service.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import java.time.Duration;
import java.util.Collections;
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
		private OpenEProperties.OpenEEnvironment.OpenEInstance externalInstanceMock;
		private OpenEProperties.OpenEEnvironment.OpenEInstance internalInstanceMock;
		private OpenEProperties.OpenEEnvironment.SchedulerProperties schedulerPropertiesMock;
		private OpenEProperties.OpenEEnvironment openEEnvironmentMock;
		private MessageAttachmentEntity messageAttachmentEntityInternalMock;
		private MessageAttachmentEntity messageAttachmentEntityExternalMock;
		private MessageAttachmentEntity messageAttachmentEntityRetryMock;
		private MessageCacheScheduler.CacheMessagesTask cacheMessagesTask;

		@BeforeEach
		void setUp() {
			messageCacheServiceMock = mock(MessageCacheService.class);
			externalInstanceMock = mock(OpenEProperties.OpenEEnvironment.OpenEInstance.class);
			internalInstanceMock = mock(OpenEProperties.OpenEEnvironment.OpenEInstance.class);
			openEEnvironmentMock = mock(OpenEProperties.OpenEEnvironment.class);
			schedulerPropertiesMock = mock(OpenEProperties.OpenEEnvironment.SchedulerProperties.class);
			messageAttachmentEntityInternalMock = mock(MessageAttachmentEntity.class);
			messageAttachmentEntityExternalMock = mock(MessageAttachmentEntity.class);
			messageAttachmentEntityRetryMock = mock(MessageAttachmentEntity.class);

			cacheMessagesTask = new MessageCacheScheduler.CacheMessagesTask(messageCacheServiceMock, MUNICIPALITY_ID, openEEnvironmentMock);
		}

		@Test
		void run() {
			var clockSkew = Duration.ofMinutes(33);
			var keepDeletedAfterLastSuccessFor = Duration.ofHours(4);
			var internalMessage = MessageEntity.builder()
				.withMunicipalityId(MUNICIPALITY_ID).withInstance(INTERNAL)
				.withAttachments(List.of(messageAttachmentEntityInternalMock)).build();
			var externalMessage = MessageEntity.builder()
				.withMunicipalityId(MUNICIPALITY_ID).withInstance(EXTERNAL)
				.withAttachments(List.of(messageAttachmentEntityExternalMock)).build();
			var retryableMessage = MessageEntity.builder()
				.withMunicipalityId(MUNICIPALITY_ID).withInstance(EXTERNAL)
				.withAttachments(List.of(messageAttachmentEntityRetryMock)).build();

			when(openEEnvironmentMock.external()).thenReturn(externalInstanceMock);
			when(openEEnvironmentMock.internal()).thenReturn(internalInstanceMock);
			when(openEEnvironmentMock.scheduler()).thenReturn(schedulerPropertiesMock);
			when(schedulerPropertiesMock.clockSkew()).thenReturn(clockSkew);
			when(schedulerPropertiesMock.keepDeletedAfterLastSuccessFor()).thenReturn(keepDeletedAfterLastSuccessFor);
			when(externalInstanceMock.familyIds()).thenReturn(List.of("123", "456"));
			when(internalInstanceMock.familyIds()).thenReturn(List.of("789"));
			when(messageCacheServiceMock.fetchAndSaveMessages(any(), eq(INTERNAL), any(), any())).thenReturn(List.of(internalMessage));
			when(messageCacheServiceMock.fetchAndSaveMessages(any(), eq(EXTERNAL), any(), any())).thenReturn(List.of(externalMessage));
			when(messageCacheServiceMock.getRetryableMessages(any())).thenReturn(List.of(retryableMessage));

			cacheMessagesTask.run();

			verify(messageCacheServiceMock).fetchAndSaveMessages(eq(MUNICIPALITY_ID), eq(EXTERNAL), eq("123"), same(clockSkew));
			verify(messageCacheServiceMock).fetchAndSaveMessages(eq(MUNICIPALITY_ID), eq(EXTERNAL), eq("456"), same(clockSkew));
			verify(messageCacheServiceMock).fetchAndSaveMessages(eq(MUNICIPALITY_ID), eq(INTERNAL), eq("789"), same(clockSkew));
			verify(messageCacheServiceMock, times(2)).fetchAndSaveAttachment(eq(MUNICIPALITY_ID), eq(EXTERNAL), same(messageAttachmentEntityExternalMock));
			verify(messageCacheServiceMock).fetchAndSaveAttachment(eq(MUNICIPALITY_ID), eq(INTERNAL), same(messageAttachmentEntityInternalMock));
			verify(messageCacheServiceMock).fetchAndSaveAttachment(eq(MUNICIPALITY_ID), eq(EXTERNAL), same(messageAttachmentEntityRetryMock));
			verify(messageCacheServiceMock).getRetryableMessages(MUNICIPALITY_ID);
			verify(messageCacheServiceMock).complete(same(internalMessage));
			verify(messageCacheServiceMock, times(2)).complete(same(externalMessage));
			verify(messageCacheServiceMock).complete(same(retryableMessage));
			verify(externalInstanceMock, times(2)).familyIds();
			verify(internalInstanceMock, times(2)).familyIds();
			verify(openEEnvironmentMock).external();
			verify(openEEnvironmentMock).internal();
			verify(messageCacheServiceMock).cleanUpDeletedMessages(keepDeletedAfterLastSuccessFor, MUNICIPALITY_ID);
			verifyNoMoreInteractions(messageCacheServiceMock, externalInstanceMock, internalInstanceMock, openEEnvironmentMock);
		}

		@Test
		void runWithExceptionFetchingAttachments() {

			var clockSkew = Duration.ofMinutes(33);
			var keepDeletedAfterLastSuccessFor = Duration.ofHours(4);
			var internalMessage = MessageEntity.builder()
				.withMunicipalityId(MUNICIPALITY_ID).withInstance(INTERNAL)
				.withAttachments(List.of(messageAttachmentEntityInternalMock)).build();
			var externalMessage = MessageEntity.builder()
				.withMunicipalityId(MUNICIPALITY_ID).withInstance(EXTERNAL)
				.withAttachments(List.of(messageAttachmentEntityExternalMock)).build();

			when(openEEnvironmentMock.external()).thenReturn(externalInstanceMock);
			when(openEEnvironmentMock.internal()).thenReturn(internalInstanceMock);
			when(openEEnvironmentMock.scheduler()).thenReturn(schedulerPropertiesMock);
			when(schedulerPropertiesMock.clockSkew()).thenReturn(clockSkew);
			when(schedulerPropertiesMock.keepDeletedAfterLastSuccessFor()).thenReturn(keepDeletedAfterLastSuccessFor);
			when(externalInstanceMock.familyIds()).thenReturn(List.of("123", "456"));
			when(internalInstanceMock.familyIds()).thenReturn(List.of("789"));
			when(messageCacheServiceMock.fetchAndSaveMessages(any(), eq(INTERNAL), any(), any())).thenReturn(List.of(internalMessage));
			when(messageCacheServiceMock.fetchAndSaveMessages(any(), eq(EXTERNAL), any(), any())).thenReturn(List.of(externalMessage));
			when(messageCacheServiceMock.getRetryableMessages(any())).thenReturn(Collections.emptyList());
			doThrow(new RuntimeException("ERROR!")).when(messageCacheServiceMock).fetchAndSaveAttachment(any(), any(), any());

			cacheMessagesTask.run();

			verify(messageCacheServiceMock).fetchAndSaveMessages(eq(MUNICIPALITY_ID), eq(EXTERNAL), eq("123"), same(clockSkew));
			verify(messageCacheServiceMock).fetchAndSaveMessages(eq(MUNICIPALITY_ID), eq(EXTERNAL), eq("456"), same(clockSkew));
			verify(messageCacheServiceMock).fetchAndSaveMessages(eq(MUNICIPALITY_ID), eq(INTERNAL), eq("789"), same(clockSkew));
			verify(messageCacheServiceMock, times(2)).fetchAndSaveAttachment(eq(MUNICIPALITY_ID), eq(EXTERNAL), same(messageAttachmentEntityExternalMock));
			verify(messageCacheServiceMock).fetchAndSaveAttachment(eq(MUNICIPALITY_ID), eq(INTERNAL), same(messageAttachmentEntityInternalMock));
			verify(messageCacheServiceMock).failedAttachments(same(internalMessage));
			verify(messageCacheServiceMock, times(2)).failedAttachments(same(externalMessage));
			verify(externalInstanceMock, times(2)).familyIds();
			verify(internalInstanceMock, times(2)).familyIds();
			verify(openEEnvironmentMock).external();
			verify(openEEnvironmentMock).internal();
			verify(messageCacheServiceMock).getRetryableMessages(MUNICIPALITY_ID);
			verify(messageCacheServiceMock).cleanUpDeletedMessages(keepDeletedAfterLastSuccessFor, MUNICIPALITY_ID);
			verifyNoMoreInteractions(messageCacheServiceMock, externalInstanceMock, internalInstanceMock, openEEnvironmentMock);

		}
	}
}
