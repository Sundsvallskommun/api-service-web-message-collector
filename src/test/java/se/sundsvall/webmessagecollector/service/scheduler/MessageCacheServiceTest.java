package se.sundsvall.webmessagecollector.service.scheduler;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.webmessagecollector.api.model.Direction.INBOUND;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.webmessagecollector.integration.db.ExecutionInformationRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.OpenEIntegration;

@ExtendWith(MockitoExtension.class)
class MessageCacheServiceTest {

	private static final String RESPONSE = """
			<Messages>
		    <ExternalMessage>
		        <postedByManager>false</postedByManager>
		        <systemMessage>false</systemMessage>
		        <readReceiptEnabled>false</readReceiptEnabled>
		        <messageID>10</messageID>
		        <message>Inbound message</message>
		        <added>2022-05-25 11:20</added>
		        <flowInstanceID>102251</flowInstanceID>
		         <attachments>
		            <ExternalMessageAttachment>
		                <attachmentID>123</attachmentID>
		                <filename>someFile.pdf</filename>
		            </ExternalMessageAttachment>
		        </attachments>
		    </ExternalMessage>
		</Messages>
		""";

	@Mock
	private OpenEIntegration openEIntegrationMock;

	@Mock
	private MessageRepository messageRepositoryMock;

	@Mock
	private MessageAttachmentRepository messageAttachmentRepositoryMock;

	@Mock
	private ExecutionInformationRepository executionInformationRepositoryMock;

	@InjectMocks
	private MessageCacheService service;

	@Captor
	private ArgumentCaptor<String> fromTimeStampCaptor;

	@Captor
	private ArgumentCaptor<List<MessageEntity>> messageEntityCaptor;

	@Captor
	private ArgumentCaptor<MessageAttachmentEntity> messageAttachmentEntityCaptor;

	@Captor
	private ArgumentCaptor<ExecutionInformationEntity> executionInformationEntityCaptor;

	@Test
	void fetchMessagesWithResponse() {
		var municipalityId = "1984";
		var familyId = "123";
		var lastExecuted = OffsetDateTime.now().minusHours(new Random().nextInt());

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.of(ExecutionInformationEntity.builder().withFamilyId(familyId).withLastSuccessfulExecution(lastExecuted).build()));
		when(openEIntegrationMock.getMessages(any(), any(), any(), any(), any())).thenReturn(RESPONSE.getBytes());

		service.fetchMessages(municipalityId, INTERNAL, familyId);

		verify(openEIntegrationMock).getMessages(municipalityId, INTERNAL, familyId, lastExecuted.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "");
		verify(messageRepositoryMock).saveAllAndFlush(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());

		assertThat(messageEntityCaptor.getValue().getFirst()).satisfies(entity -> {
			assertThat(entity.getDirection()).isEqualTo(INBOUND);
			assertThat(entity.getEmail()).isNull();
			assertThat(entity.getExternalCaseId()).isEqualTo("102251");
			assertThat(entity.getFamilyId()).isEqualTo(familyId);
			assertThat(entity.getFirstName()).isNull();
			assertThat(entity.getId()).isNull();
			assertThat(entity.getLastName()).isNull();
			assertThat(entity.getMessage()).isEqualTo("Inbound message");
			assertThat(entity.getMessageId()).isEqualTo("10");
			assertThat(entity.getSent()).isEqualTo(LocalDateTime.of(2022, 5, 25, 11, 20));
			assertThat(entity.getUserId()).isNull();
			assertThat(entity.getUsername()).isNull();
			assertThat(entity.getAttachments()).hasSize(1);
			assertThat(entity.getAttachments().getFirst().getAttachmentId()).isEqualTo(123);
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
	void fetchMessagesFirstTime() {
		var municipalityId = "1984";
		var familyId = "123";

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.empty());
		when(openEIntegrationMock.getMessages(any(), any(), any(), any(), any())).thenReturn(RESPONSE.getBytes());

		service.fetchMessages(municipalityId, INTERNAL, familyId);

		// Assert and verify
		verify(openEIntegrationMock).getMessages(eq(municipalityId), eq(INTERNAL), eq(familyId), fromTimeStampCaptor.capture(), eq(""));
		verify(messageRepositoryMock).saveAllAndFlush(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());
		assertThat(LocalDateTime.parse(fromTimeStampCaptor.getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.isCloseTo(LocalDateTime.now().minusHours(1), within(1, MINUTES));
		assertThat(messageEntityCaptor.getValue().getFirst()).satisfies(entity -> {
			assertThat(entity.getDirection()).isEqualTo(INBOUND);
			assertThat(entity.getEmail()).isNull();
			assertThat(entity.getExternalCaseId()).isEqualTo("102251");
			assertThat(entity.getFamilyId()).isEqualTo(familyId);
			assertThat(entity.getFirstName()).isNull();
			assertThat(entity.getId()).isNull();
			assertThat(entity.getLastName()).isNull();
			assertThat(entity.getMessage()).isEqualTo("Inbound message");
			assertThat(entity.getMessageId()).isEqualTo("10");
			assertThat(entity.getSent()).isEqualTo(LocalDateTime.of(2022, 5, 25, 11, 20));
			assertThat(entity.getUserId()).isNull();
			assertThat(entity.getUsername()).isNull();
		});
		assertThat(executionInformationEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFamilyId()).isEqualTo(familyId);
			assertThat(entity.getLastSuccessfulExecution()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		});
	}

	@Test
	void fetchAttachment() {
		var municipalityId = "1984";
		var message = MessageEntity.builder().build();
		var attachmentEntity = MessageAttachmentEntity.builder()
			.withMessage(message)
			.withExtension("pdf")
			.withAttachmentId(123)
			.withMimeType("application/pdf")
			.withName("someFile.pdf")
			.build();
		var attachment = "attachment".getBytes();
		when(openEIntegrationMock.getAttachment(municipalityId, INTERNAL, 123)).thenReturn(attachment);

		service.fetchAttachment(municipalityId, INTERNAL, attachmentEntity);

		verify(openEIntegrationMock).getAttachment(municipalityId, INTERNAL, 123);
		verify(messageAttachmentRepositoryMock).saveAndFlush(messageAttachmentEntityCaptor.capture());
		assertThat(messageAttachmentEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getAttachmentId()).isEqualTo(123);
			assertThat(entity.getFile()).isEqualTo(new SerialBlob(attachment));
			assertThat(entity.getMimeType()).isEqualTo("application/pdf");
			assertThat(entity.getName()).isEqualTo("someFile.pdf");
			assertThat(entity.getExtension()).isEqualTo("pdf");
			assertThat(entity.getMessage()).isEqualTo(message);
		});
	}

	@Test
	void fetchAttachmentWhenAttachmentIsNull() {
		var municipalityId = "1984";
		var attachmentId = 123;
		var attachmentEntity = MessageAttachmentEntity.builder()
			.withExtension("pdf")
			.withAttachmentId(attachmentId)
			.withMimeType("application/pdf")
			.withName("someFile.pdf")
			.build();
		when(openEIntegrationMock.getAttachment(municipalityId, INTERNAL, attachmentId)).thenReturn(null);

		service.fetchAttachment(municipalityId, INTERNAL, attachmentEntity);

		verify(openEIntegrationMock).getAttachment(municipalityId, INTERNAL, attachmentId);
		verifyNoInteractions(messageAttachmentRepositoryMock);
	}
}
