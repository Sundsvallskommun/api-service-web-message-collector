package se.sundsvall.webmessagecollector.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@ExtendWith(MockitoExtension.class)
class MessageCacheSchedulerTest {

	@Mock
	private MessageCacheProperties messageCachePropertiesMock;

	@Mock
	private MessageCacheService messageCacheServiceMock;

	@InjectMocks
	private MessageCacheScheduler scheduler;

	@Captor
	private ArgumentCaptor<String> familyIdCaptor;

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " " })
	void cacheMessagesWhenNoFamilyIdsDefined(String familyIds) {
		// Arrange
		when(messageCachePropertiesMock.familyId()).thenReturn(familyIds);

		// Act
		scheduler.cacheMessages();

		// Assert and verify
		verify(messageCachePropertiesMock).familyId();
		verifyNoMoreInteractions(messageCachePropertiesMock);
		verifyNoInteractions(messageCacheServiceMock);
	}

	@Test
	void cacheMessages() {
		// Arrange
		when(messageCachePropertiesMock.familyId()).thenReturn("123, 456");

		// Act
		scheduler.cacheMessages();

		// Assert and verify
		verify(messageCachePropertiesMock).familyId();
		verify(messageCacheServiceMock, times(2)).fetchMessages(familyIdCaptor.capture());
		verifyNoMoreInteractions(messageCachePropertiesMock, messageCacheServiceMock);
		assertThat(familyIdCaptor.getAllValues()).hasSize(2)
			.containsExactly("123", "456");
	}

	@Test
	void cacheMessagesOneThrowsException() {
		// Arrange
		when(messageCachePropertiesMock.familyId()).thenReturn("123, 456");
		doThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Test")).when(messageCacheServiceMock).fetchMessages("123");

		// Act
		scheduler.cacheMessages();

		// Assert and verify
		verify(messageCachePropertiesMock).familyId();
		verify(messageCacheServiceMock, times(2)).fetchMessages(familyIdCaptor.capture());
		verifyNoMoreInteractions(messageCachePropertiesMock, messageCacheServiceMock);
		assertThat(familyIdCaptor.getAllValues()).hasSize(2)
			.containsExactly("123", "456");
	}
}
