package se.sundsvall.webmessagecollector.service.scheduler;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageCacheSchedulerTest {
/*
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
		verify(messageCacheServiceMock, times(2)).fetchMessages(eq(Instance.INTERNAL), familyIdCaptor.capture());
		verifyNoMoreInteractions(messageCachePropertiesMock, messageCacheServiceMock);
		assertThat(familyIdCaptor.getAllValues()).hasSize(2)
			.containsExactly("123", "456");
	}

	@Test
	void cacheMessagesOneThrowsException() {
		// Arrange
		when(messageCachePropertiesMock.familyIds()).thenReturn(Map.of("INTERNAL", List.of("123", "456")));
		doThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Test")).when(messageCacheServiceMock).fetchMessages(eq(Instance.INTERNAL), anyString());

		// Act
		scheduler.cacheMessages();

		// Assert and verify
		verify(messageCachePropertiesMock).familyIds();
		verify(messageCacheServiceMock, times(2)).fetchMessages(eq(Instance.INTERNAL), familyIdCaptor.capture());
		verifyNoMoreInteractions(messageCachePropertiesMock, messageCacheServiceMock);
		assertThat(familyIdCaptor.getAllValues()).hasSize(2)
			.containsExactly("123", "456");
	}
*/
}
