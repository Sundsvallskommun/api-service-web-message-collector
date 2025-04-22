package se.sundsvall.webmessagecollector.integration.oep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.webmessagecollector.TestDataFactory.createAttachmentStream;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessage;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessageAttachment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;

@ExtendWith(MockitoExtension.class)
class OepIntegratorIntegrationTest {

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private OepIntegratorClient oepIntegratorClientMock;

	@InjectMocks
	private OepIntegratorIntegration oepIntegratorIntegration;

	@Test
	void getWebmessageByFamilyId() {
		var municipalityId = "municipalityId";
		var instanceType = "INTERNAL";
		var familyId = "familyId";
		var fromDate = "2000-01-01T00:00:00";
		var toDate = "2020-01-01T00:00:00";
		var webMessage = createWebMessage();
		when(oepIntegratorClientMock.getWebmessageByFamilyId(municipalityId, instanceType, familyId, fromDate, toDate)).thenReturn(List.of(webMessage));

		var result = oepIntegratorIntegration.getWebmessageByFamilyId(municipalityId, Instance.valueOf(instanceType), familyId, fromDate, toDate);

		assertThat(result).isNotNull().containsOnly(webMessage);
		verify(oepIntegratorClientMock).getWebmessageByFamilyId(municipalityId, instanceType, familyId, fromDate, toDate);
		verifyNoMoreInteractions(oepIntegratorClientMock);
	}

	@Test
	void getAttachmentStreamById() {
		var municipalityId = "municipalityId";
		var instanceType = "EXTERNAL";
		var attachmentId = 1;

		var attachment = createWebMessageAttachment();
		var attachmentStream = createAttachmentStream(attachment);
		when(oepIntegratorClientMock.getAttachmentById(municipalityId, instanceType, attachmentId)).thenReturn(attachmentStream);

		var result = oepIntegratorIntegration.getAttachmentStreamById(municipalityId, Instance.valueOf(instanceType), attachmentId);

		assertThat(result).isNotNull().satisfies(response -> {
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().getInputStream().readAllBytes()).isEqualTo(new byte[] {
				1, 2, 3
			});
			assertThat(response.getHeaders()).containsEntry("Content-Disposition", List.of("attachment; filename=someFile.pdf"));
			assertThat(response.getHeaders()).containsEntry("Content-Type", List.of("application/pdf"));
		});
		verify(oepIntegratorClientMock).getAttachmentById(municipalityId, instanceType, attachmentId);
		verifyNoMoreInteractions(oepIntegratorClientMock);
	}

	@Test
	void getWebmessagesByFlowInstanceId() {
		var municipalityId = "municipalityId";
		var instanceType = "EXTERNAL";
		var flowInstanceId = "flowInstanceId";
		var fromDate = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.MIN);
		var toDate = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.MAX);

		var webMessage = createWebMessage();
		when(oepIntegratorClientMock.getWebmessagesByFlowInstanceId(municipalityId, instanceType, flowInstanceId, fromDate, toDate)).thenReturn(List.of(webMessage));

		var result = oepIntegratorIntegration.getWebmessagesByFlowInstanceId(municipalityId, Instance.valueOf(instanceType), flowInstanceId, fromDate, toDate);

		assertThat(result).isNotNull().containsOnly(webMessage);
		verify(oepIntegratorClientMock).getWebmessagesByFlowInstanceId(municipalityId, instanceType, flowInstanceId, fromDate, toDate);
		verifyNoMoreInteractions(oepIntegratorClientMock);
	}

}
