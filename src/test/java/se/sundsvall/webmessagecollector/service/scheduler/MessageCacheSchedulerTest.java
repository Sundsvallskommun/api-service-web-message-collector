package se.sundsvall.webmessagecollector.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.webmessagecollector.integration.opene.model.Scope;

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
	void cacheMessagesWhenNoFamilyIdsDefined(final Map<String, List<String>> familyIds) {
		// Arrange
		when(messageCachePropertiesMock.familyIds()).thenReturn(familyIds);

		// Act
		scheduler.cacheMessages();

		// Assert and verify
		verify(messageCachePropertiesMock).familyIds();
		verifyNoMoreInteractions(messageCachePropertiesMock);
		verifyNoInteractions(messageCacheServiceMock);
	}

	@Test
	void cacheMessages() {
		// Arrange
		when(messageCachePropertiesMock.familyIds()).thenReturn(Map.of("INTERNAL", List.of("123", "456")));

		// Act
		scheduler.cacheMessages();

		// Assert and verify
		verify(messageCachePropertiesMock).familyIds();
		verify(messageCacheServiceMock, times(2)).fetchMessages(eq(Scope.INTERNAL), familyIdCaptor.capture());
		verifyNoMoreInteractions(messageCachePropertiesMock, messageCacheServiceMock);
		assertThat(familyIdCaptor.getAllValues()).hasSize(2)
			.containsExactly("123", "456");
	}

	@Test
	void cacheMessagesOneThrowsException() {
		// Arrange
		when(messageCachePropertiesMock.familyIds()).thenReturn(Map.of("INTERNAL", List.of("123", "456")));
		doThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Test")).when(messageCacheServiceMock).fetchMessages(eq(Scope.INTERNAL), anyString());

		// Act
		scheduler.cacheMessages();

		// Assert and verify
		verify(messageCachePropertiesMock).familyIds();
		verify(messageCacheServiceMock, times(2)).fetchMessages(eq(Scope.INTERNAL), familyIdCaptor.capture());
		verifyNoMoreInteractions(messageCachePropertiesMock, messageCacheServiceMock);
		assertThat(familyIdCaptor.getAllValues()).hasSize(2)
			.containsExactly("123", "456");
	}

}
