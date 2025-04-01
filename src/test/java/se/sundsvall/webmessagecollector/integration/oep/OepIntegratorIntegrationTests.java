package se.sundsvall.webmessagecollector.integration.oep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessage;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessageAttachmentData;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;

@ExtendWith(MockitoExtension.class)
class OepIntegratorIntegrationTests {

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private OepIntegratorClient oepIntegratorClientMock;

	@InjectMocks
	private OepIntegratorIntegration oepIntegratorIntegration;

	@Test
	void getWebmessageByFamilyId() {
		var municipalityId = "municipalityId";
		var instanceType = "INTERNAL";
		var familyId = "familyId";
		var fromDate = "fromDate";
		var toDate = "toDate";
		var webMessage = createWebMessage();
		when(oepIntegratorClientMock.getWebmessageByFamilyId(municipalityId, instanceType, familyId, fromDate, toDate)).thenReturn(List.of(webMessage));

		var result = oepIntegratorIntegration.getWebmessageByFamilyId(municipalityId, Instance.valueOf(instanceType), familyId, fromDate, toDate);

		assertThat(result).isNotNull().containsOnly(webMessage);
		verify(oepIntegratorClientMock).getWebmessageByFamilyId(municipalityId, instanceType, familyId, fromDate, toDate);
		verifyNoMoreInteractions(oepIntegratorClientMock);
	}

	@Test
	void getAttachmentById() {
		var municipalityId = "municipalityId";
		var instanceType = "EXTERNAL";
		var flowInstanceId = "flowInstanceId";
		var attachmentId = 1;
		var webmessageAttachmentData = createWebMessageAttachmentData();
		when(oepIntegratorClientMock.getAttachmentById(municipalityId, instanceType, flowInstanceId, attachmentId)).thenReturn(webmessageAttachmentData);

		var result = oepIntegratorIntegration.getAttachmentById(municipalityId, Instance.valueOf(instanceType), flowInstanceId, attachmentId);

		assertThat(result).isNotNull().isEqualTo(webmessageAttachmentData);
		verify(oepIntegratorClientMock).getAttachmentById(municipalityId, instanceType, flowInstanceId, attachmentId);
		verifyNoMoreInteractions(oepIntegratorClientMock);
	}

	@Test
	void getWebmessagesByFlowInstanceId() {
		var municipalityId = "municipalityId";
		var instanceType = "EXTERNAL";
		var flowInstanceId = "flowInstanceId";
		var fromDate = "fromDate";
		var toDate = "toDate";

		var webMessage = createWebMessage();
		when(oepIntegratorClientMock.getWebmessagesByFlowInstanceId(municipalityId, instanceType, flowInstanceId, fromDate, toDate)).thenReturn(List.of(webMessage));

		var result = oepIntegratorIntegration.getWebmessagesByFlowInstanceId(municipalityId, Instance.valueOf(instanceType), flowInstanceId, fromDate, toDate);

		assertThat(result).isNotNull().containsOnly(webMessage);
		verify(oepIntegratorClientMock).getWebmessagesByFlowInstanceId(municipalityId, instanceType, flowInstanceId, fromDate, toDate);
		verifyNoMoreInteractions(oepIntegratorClientMock);
	}

}
