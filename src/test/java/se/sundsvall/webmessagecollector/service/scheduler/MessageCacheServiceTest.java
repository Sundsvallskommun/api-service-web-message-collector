package se.sundsvall.webmessagecollector.service.scheduler;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.ExecutionInformationRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.OpenEClient;

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
	private OpenEClient openEClientMock;

	@Mock
	private MessageRepository messageRepositoryMock;

	@Mock
	private ExecutionInformationRepository executionInformationRepositoryMock;

	@InjectMocks
	private MessageCacheService service;

	@Captor
	private ArgumentCaptor<String> fromTimeStampCaptor;

	@Captor
	private ArgumentCaptor<MessageEntity> messageEntityCaptor;

	@Captor
	private ArgumentCaptor<ExecutionInformationEntity> executionInformationEntityCaptor;

	@Test
	void fetchMessagesWithResponse() {
		// Arrange
		final var familyId = "123";
		final var lastExecuted = OffsetDateTime.now().minusHours(RandomUtils.nextInt());

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.of(ExecutionInformationEntity.builder().withFamilyId(familyId).withLastSuccessfulExecution(lastExecuted).build()));
		when(openEClientMock.getMessages(any(), any(), any())).thenReturn(RESPONSE.getBytes());
		when(openEClientMock.getAttachment(anyInt())).thenReturn("attachment".getBytes());
		// Act
		service.fetchMessages(familyId);

		// Assert and verify
		verify(openEClientMock).getMessages(familyId, lastExecuted.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "");
		verify(messageRepositoryMock).save(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());
		assertThat(messageEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getDirection()).isEqualTo(Direction.INBOUND);
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
			assertThat(entity.getAttachments().getFirst().getFileName()).isEqualTo("someFile.pdf");
			assertThat(entity.getAttachments().getFirst().getFile()).isNotNull();
		});
		assertThat(executionInformationEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getFamilyId()).isEqualTo(familyId);
			assertThat(entity.getLastSuccessfulExecution()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		});
	}

	@Test
	void fetchMessagesFirstTime() {
		// Arrange
		final var familyId = "123";

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.empty());
		when(openEClientMock.getMessages(any(), any(), any())).thenReturn(RESPONSE.getBytes());

		// Act
		service.fetchMessages(familyId);

		// Assert and verify
		verify(openEClientMock).getMessages(eq(familyId), fromTimeStampCaptor.capture(), eq(""));
		verify(messageRepositoryMock).save(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());
		assertThat(LocalDateTime.parse(fromTimeStampCaptor.getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.isCloseTo(LocalDateTime.now().minusHours(1), within(1, MINUTES));
		assertThat(messageEntityCaptor.getValue()).satisfies(entity -> {
			assertThat(entity.getDirection()).isEqualTo(Direction.INBOUND);
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

}
