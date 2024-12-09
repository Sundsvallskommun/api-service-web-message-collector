package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

@ExtendWith(MockitoExtension.class)
class OpenEIntegrationTest {

	@Mock
	private OpenEClient openEClientMock;
	@Mock
	private OpenEClientFactory openEClientFactoryMock;

	@InjectMocks
	private OpenEIntegration openEIntegration;

	@Test
	void getMessagesWhenNoConfiguredClientExists() {
		var municipalityId = "1984";
		var familyId = "123";
		var instance = INTERNAL;
		var fromDate = LocalDate.now().minusDays(30).toString();
		var toDate = LocalDate.now().toString();

		when(openEClientFactoryMock.getClient(municipalityId, instance)).thenThrow(Problem.valueOf(INTERNAL_SERVER_ERROR));

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> openEIntegration.getMessages(municipalityId, instance, familyId, fromDate, toDate))
			.satisfies(problem -> assertThat(problem.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR));

		verify(openEClientFactoryMock).getClient(municipalityId, instance);
		verifyNoInteractions(openEClientMock);
		verifyNoMoreInteractions(openEClientFactoryMock);
	}

	@Test
	void getInternalMessages() {
		var municipalityId = "1984";
		var familyId = "123";
		var instance = INTERNAL;
		var fromDate = LocalDate.now().minusDays(30).toString();
		var toDate = LocalDate.now().toString();

		when(openEClientFactoryMock.getClient(municipalityId, instance)).thenReturn(openEClientMock);
		when(openEClientMock.getMessages(familyId, fromDate, toDate)).thenReturn(new byte[0]);

		var result = openEIntegration.getMessages(municipalityId, instance, familyId, fromDate, toDate);

		assertThat(result).isNotNull();

		verify(openEClientFactoryMock).getClient(municipalityId, instance);
		verify(openEClientMock).getMessages(familyId, fromDate, toDate);
		verifyNoMoreInteractions(openEClientFactoryMock, openEClientMock);
	}

	@Test
	void getExternalMessages() {
		var municipalityId = "1984";
		var familyId = "456";
		var instance = EXTERNAL;
		var fromDate = LocalDate.now().minusDays(30).toString();
		var toDate = LocalDate.now().toString();

		when(openEClientFactoryMock.getClient(municipalityId, instance)).thenReturn(openEClientMock);
		when(openEClientMock.getMessages(familyId, fromDate, toDate)).thenReturn(new byte[0]);

		var result = openEIntegration.getMessages(municipalityId, instance, familyId, fromDate, toDate);

		assertThat(result).isNotNull();

		verify(openEClientFactoryMock).getClient(municipalityId, instance);
		verify(openEClientMock).getMessages(familyId, fromDate, toDate);
		verifyNoMoreInteractions(openEClientFactoryMock, openEClientMock);
	}

	@Test
	void getInternalAttachment() {
		var municipalityId = "1984";
		var attachmentId = 123;
		var instance = INTERNAL;

		when(openEClientFactoryMock.getClient(municipalityId, instance)).thenReturn(openEClientMock);
		when(openEClientMock.getAttachment(attachmentId)).thenReturn(new byte[0]);

		var result = openEIntegration.getAttachment(municipalityId, instance, attachmentId);

		assertThat(result).isNotNull();

		verify(openEClientFactoryMock).getClient(municipalityId, instance);
		verify(openEClientMock).getAttachment(attachmentId);
		verifyNoMoreInteractions(openEClientFactoryMock, openEClientMock);
	}

	@Test
	void getExternalAttachment() {
		var municipalityId = "1984";
		var attachmentId = 123;
		var instance = EXTERNAL;

		when(openEClientFactoryMock.getClient(municipalityId, instance)).thenReturn(openEClientMock);
		when(openEClientMock.getAttachment(attachmentId)).thenReturn(new byte[0]);

		var result = openEIntegration.getAttachment(municipalityId, instance, attachmentId);

		assertThat(result).isNotNull();

		verify(openEClientFactoryMock).getClient(municipalityId, instance);
		verify(openEClientMock).getAttachment(attachmentId);
		verifyNoMoreInteractions(openEClientFactoryMock, openEClientMock);
	}
}
