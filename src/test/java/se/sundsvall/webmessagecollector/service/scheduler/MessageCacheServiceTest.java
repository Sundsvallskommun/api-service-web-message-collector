package se.sundsvall.webmessagecollector.service.scheduler;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.webmessagecollector.TestDataFactory.createAttachmentStream;
import static se.sundsvall.webmessagecollector.TestDataFactory.createEmptyResponse;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessage;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessageAttachment;
import static se.sundsvall.webmessagecollector.api.model.Direction.INBOUND;
import static se.sundsvall.webmessagecollector.integration.db.model.Instance.INTERNAL;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.sql.rowset.serial.SerialBlob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.webmessagecollector.integration.db.ExecutionInformationRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;
import se.sundsvall.webmessagecollector.integration.oep.OepIntegratorIntegration;

@ExtendWith(MockitoExtension.class)
class MessageCacheServiceTest {

	private static final String MUNICIPALITY_ID = "municipalityId";

	@Mock
	private OepIntegratorIntegration oepIntegratorIntegrationMock;

	@Mock
	private MessageRepository messageRepositoryMock;

	@Mock
	private MessageAttachmentRepository messageAttachmentRepositoryMock;

	@Mock
	private ExecutionInformationRepository executionInformationRepositoryMock;

	@InjectMocks
	private MessageCacheService service;

	@Captor
	private ArgumentCaptor<LocalDateTime> fromTimeStampCaptor;

	@Captor
	private ArgumentCaptor<List<MessageEntity>> messageEntityCaptor;

	@Captor
	private ArgumentCaptor<MessageAttachmentEntity> messageAttachmentEntityCaptor;

	@Captor
	private ArgumentCaptor<ExecutionInformationEntity> executionInformationEntityCaptor;

	@Test
	void fetchAndSaveMessagesWithResponse() {
		var municipalityId = "1984";
		var familyId = "123";
		var lastExecuted = OffsetDateTime.now().minusHours(new Random().nextInt());
		var clockSkew = Duration.ofMinutes(10);
		var webMessage = createWebMessage();
		webMessage.setFamilyId(familyId);
		var webMessages = List.of(webMessage);

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.of(ExecutionInformationEntity.builder().withFamilyId(familyId).withLastSuccessfulExecution(lastExecuted).build()));
		when(oepIntegratorIntegrationMock.getWebmessageByFamilyId(municipalityId, INTERNAL, familyId, lastExecuted.minus(clockSkew).toLocalDateTime(), null)).thenReturn(webMessages);
		when(messageRepositoryMock.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(familyId, INTERNAL, "messageId", "externalCaseId")).thenReturn(false);

		service.fetchAndSaveMessages(municipalityId, INTERNAL, familyId, clockSkew);
		verify(oepIntegratorIntegrationMock).getWebmessageByFamilyId(municipalityId, INTERNAL, familyId, lastExecuted.minus(clockSkew).toLocalDateTime(), null);
		verify(messageRepositoryMock).existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(familyId, INTERNAL, "messageId", "externalCaseId");
		verify(messageRepositoryMock).saveAllAndFlush(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());

		assertThat(messageEntityCaptor.getValue().getFirst()).satisfies(entity -> {
			assertThat(entity).hasNoNullFieldsOrPropertiesExcept("id", "email", "firstName", "lastName", "userId", "username");
			assertThat(entity.getDirection()).isEqualTo(INBOUND);
			assertThat(entity.getExternalCaseId()).isEqualTo("externalCaseId");
			assertThat(entity.getFamilyId()).isEqualTo(familyId);
			assertThat(entity.getMessage()).isEqualTo("message");
			assertThat(entity.getMessageId()).isEqualTo("messageId");
			assertThat(entity.getSent()).isEqualTo(LocalDateTime.MIN);
			assertThat(entity.getAttachments()).hasSize(1);
			assertThat(entity.getAttachments().getFirst().getAttachmentId()).isEqualTo(12345);
			assertThat(entity.getAttachments().getFirst().getName()).isEqualTo("someFile.pdf");
			assertThat(entity.getAttachments().getFirst().getMimeType()).isEqualTo("application/pdf");
			assertThat(entity.getAttachments().getFirst().getExtension()).isEqualTo("pdf");
			assertThat(entity.getAttachments().getFirst().getMessage()).isEqualTo(entity);
			assertThat(entity.getAttachments().getFirst().getFile()).isNull();
		});

		assertThat(executionInformationEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFamilyId()).isEqualTo(familyId);
			assertThat(entity.getLastSuccessfulExecution()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		});
	}

	@Test
	void fetchAndSaveMessagesWithResponseAlreadyFetched() {
		var municipalityId = "1984";
		var familyId = "123";
		var lastExecuted = OffsetDateTime.now().minusHours(new Random().nextInt());
		var clockSkew = Duration.ofMinutes(30);
		var webMessage = createWebMessage();
		webMessage.setFamilyId(familyId);
		var webMessages = List.of(webMessage);

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.of(ExecutionInformationEntity.builder().withFamilyId(familyId).withLastSuccessfulExecution(lastExecuted).build()));
		when(oepIntegratorIntegrationMock.getWebmessageByFamilyId(municipalityId, INTERNAL, familyId, lastExecuted.minus(clockSkew).toLocalDateTime(), null)).thenReturn(webMessages);
		when(messageRepositoryMock.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(familyId, INTERNAL, "messageId", "externalCaseId")).thenReturn(true);

		service.fetchAndSaveMessages(municipalityId, INTERNAL, familyId, clockSkew);

		verify(oepIntegratorIntegrationMock).getWebmessageByFamilyId(municipalityId, INTERNAL, familyId, lastExecuted.minus(clockSkew).toLocalDateTime(), null);
		verify(messageRepositoryMock).existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(familyId, INTERNAL, "messageId", "externalCaseId");
		verify(messageRepositoryMock).saveAllAndFlush(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());

		assertThat(messageEntityCaptor.getValue()).isEmpty();

		assertThat(executionInformationEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFamilyId()).isEqualTo(familyId);
			assertThat(entity.getLastSuccessfulExecution()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		});
	}

	@Test
	void fetchAndSaveMessagesFirstTime() {
		var spy = Mockito.spy(service);
		var municipalityId = "1984";
		var familyId = "123";
		var clockSkew = Duration.ofMinutes(20);
		var webMessage = createWebMessage();
		webMessage.setFamilyId(familyId);
		var webMessages = List.of(webMessage);

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.empty());
		when(oepIntegratorIntegrationMock.getWebmessageByFamilyId(eq(municipalityId), eq(INTERNAL), eq(familyId), any(), any())).thenReturn(webMessages);
		when(spy.initiateExecutionInfo(municipalityId, familyId)).thenCallRealMethod();

		var result = spy.fetchAndSaveMessages(municipalityId, INTERNAL, familyId, clockSkew);

		assertThat(result).isNotNull().hasSize(1);

		verify(spy).initiateExecutionInfo(municipalityId, familyId);
		verify(oepIntegratorIntegrationMock).getWebmessageByFamilyId(eq(municipalityId), eq(INTERNAL), eq(familyId), fromTimeStampCaptor.capture(), any());
		verify(messageRepositoryMock).saveAllAndFlush(any());
		verify(executionInformationRepositoryMock).save(any());
	}

	@Test
	void fetchAndSaveAttachment() {
		var municipalityId = "1984";
		var message = MessageEntity.builder().build();
		var attachmentEntity = MessageAttachmentEntity.builder()
			.withMessage(message)
			.withExtension("pdf")
			.withAttachmentId(123)
			.withMimeType("application/pdf")
			.withName("someFile.pdf")
			.build();
		var attachment = createWebMessageAttachment();
		var attachmentStream = createAttachmentStream(attachment);
		when(oepIntegratorIntegrationMock.getAttachmentStreamById(municipalityId, INTERNAL, 123)).thenReturn(attachmentStream);

		service.fetchAndSaveAttachment(municipalityId, INTERNAL, attachmentEntity);

		verify(oepIntegratorIntegrationMock).getAttachmentStreamById(municipalityId, INTERNAL, 123);
		verify(messageAttachmentRepositoryMock).saveAndFlush(messageAttachmentEntityCaptor.capture());
		assertThat(messageAttachmentEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getAttachmentId()).isEqualTo(123);
			assertThat(entity.getFile()).isEqualTo(new SerialBlob(new byte[] {
				1, 2, 3
			}));
			assertThat(entity.getMimeType()).isEqualTo("application/pdf");
			assertThat(entity.getName()).isEqualTo("someFile.pdf");
			assertThat(entity.getExtension()).isEqualTo("pdf");
			assertThat(entity.getMessage()).isEqualTo(message);
		});
	}

	@Test
	void fetchAndSaveAttachmentWhenAttachmentIsNull() {
		var municipalityId = "1984";
		var attachmentId = 123;
		var attachmentEntity = MessageAttachmentEntity.builder()
			.withExtension("pdf")
			.withAttachmentId(attachmentId)
			.withMimeType("application/pdf")
			.withName("someFile.pdf")
			.build();
		when(oepIntegratorIntegrationMock.getAttachmentStreamById(municipalityId, INTERNAL, attachmentId)).thenReturn(createEmptyResponse());

		service.fetchAndSaveAttachment(municipalityId, INTERNAL, attachmentEntity);

		verify(oepIntegratorIntegrationMock).getAttachmentStreamById(municipalityId, INTERNAL, attachmentId);
		verifyNoInteractions(messageAttachmentRepositoryMock);
	}

	@Test
	void fetchAndSaveAttachmentWhenExceptionIsThrown() {
		var municipalityId = "1984";
		var attachmentId = 123;
		var attachmentEntity = MessageAttachmentEntity.builder().withAttachmentId(attachmentId).build();
		var originalException = new RuntimeException("ERROR!");
		when(oepIntegratorIntegrationMock.getAttachmentStreamById(municipalityId, INTERNAL, attachmentId)).thenThrow(originalException);

		assertThatThrownBy(() -> service.fetchAndSaveAttachment(municipalityId, INTERNAL, attachmentEntity))
			.isInstanceOf(RuntimeException.class)
			.extracting("cause")
			.isSameAs(originalException);

		verify(oepIntegratorIntegrationMock).getAttachmentStreamById(municipalityId, INTERNAL, attachmentId);
		verifyNoInteractions(messageAttachmentRepositoryMock);
	}

	@Test
	void failedAttachments() {
		var messageMock = mock(MessageEntity.class);
		var timestampCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

		service.failedAttachments(messageMock);

		verify(messageMock).setStatus(MessageStatus.FAILED_ATTACHMENTS);
		verify(messageMock).setStatusTimestamp(timestampCaptor.capture());
		verify(messageRepositoryMock).save(same(messageMock));
		assertThat(timestampCaptor.getValue()).isCloseTo(LocalDateTime.now(), within(2, SECONDS));
	}

	@Test
	void complete() {
		var messageMock = mock(MessageEntity.class);
		var timestampCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

		service.complete(messageMock);

		verify(messageMock).setStatus(MessageStatus.COMPLETE);
		verify(messageMock).setStatusTimestamp(timestampCaptor.capture());
		verify(messageRepositoryMock).save(same(messageMock));
		assertThat(timestampCaptor.getValue()).isCloseTo(LocalDateTime.now(), within(2, SECONDS));
	}

	@Test
	void getRetryableMessages() {
		var message = mock(MessageEntity.class);
		when(messageRepositoryMock.findAllByStatusInAndMunicipalityId(any(), any())).thenReturn(List.of(message));

		var result = service.getRetryableMessages(MUNICIPALITY_ID);

		verify(messageRepositoryMock).findAllByStatusInAndMunicipalityId(List.of(MessageStatus.FAILED_ATTACHMENTS, MessageStatus.PROCESSING), MUNICIPALITY_ID);
		assertThat(result).containsExactly(message);
	}

	@Test
	void cleanUpDeletedMessages() {
		var executionInformationEntityMock = mock(ExecutionInformationEntity.class);
		var familyId = "123";
		var lastSuccess = OffsetDateTime.now();
		var keep = Duration.ofMinutes(123);

		when(executionInformationRepositoryMock.findByMunicipalityId(any())).thenReturn(List.of(executionInformationEntityMock));
		when(executionInformationEntityMock.getFamilyId()).thenReturn(familyId);
		when(executionInformationEntityMock.getLastSuccessfulExecution()).thenReturn(lastSuccess);

		service.cleanUpDeletedMessages(Duration.ofMinutes(123), MUNICIPALITY_ID);

		verify(executionInformationRepositoryMock).findByMunicipalityId(MUNICIPALITY_ID);
		verify(messageRepositoryMock).deleteByStatusAndMunicipalityIdAndFamilyIdAndStatusTimestampIsBefore(
			MessageStatus.DELETED,
			MUNICIPALITY_ID,
			familyId,
			lastSuccess.minus(keep).toLocalDateTime());
	}
}
