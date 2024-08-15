package se.sundsvall.webmessagecollector.service.scheduler;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageCacheServiceTest {
/*
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
		// Arrange
		var familyId = "123";
		var lastExecuted = OffsetDateTime.now().minusHours(RandomUtils.nextInt());

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.of(ExecutionInformationEntity.builder().withFamilyId(familyId).withLastSuccessfulExecution(lastExecuted).build()));
		when(openEIntegrationMock.getMessages(any(), any(), any(), any())).thenReturn(RESPONSE.getBytes());
		// Act
		service.fetchMessages(Instance.INTERNAL, familyId);

		// Assert and verify
		verify(openEIntegrationMock).getMessages(Instance.INTERNAL, familyId, lastExecuted.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "");
		verify(messageRepositoryMock).saveAllAndFlush(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());
		assertThat(messageEntityCaptor.getValue().getFirst()).satisfies(entity -> {
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
		// Arrange
		var familyId = "123";

		when(executionInformationRepositoryMock.findById(familyId)).thenReturn(Optional.empty());
		when(openEIntegrationMock.getMessages(any(), any(), any(), any())).thenReturn(RESPONSE.getBytes());

		// Act
		service.fetchMessages(Instance.INTERNAL, familyId);

		// Assert and verify
		verify(openEIntegrationMock).getMessages(eq(Instance.INTERNAL), eq(familyId), fromTimeStampCaptor.capture(), eq(""));
		verify(messageRepositoryMock).saveAllAndFlush(messageEntityCaptor.capture());
		verify(executionInformationRepositoryMock).save(executionInformationEntityCaptor.capture());
		assertThat(LocalDateTime.parse(fromTimeStampCaptor.getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
			.isCloseTo(LocalDateTime.now().minusHours(1), within(1, MINUTES));
		assertThat(messageEntityCaptor.getValue().getFirst()).satisfies(entity -> {
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


	@Test
	void fetchAttachment() {
		// Arrange
		var message = MessageEntity.builder().build();
		var attachmentEntity = MessageAttachmentEntity.builder()
			.withMessage(message)
			.withExtension("pdf")
			.withAttachmentId(123)
			.withMimeType("application/pdf")
			.withName("someFile.pdf")
			.build();
		var attachment = "attachment".getBytes();
		when(openEIntegrationMock.getAttachment(Instance.INTERNAL, 123)).thenReturn(attachment);

		// Act
		service.fetchAttachment(Instance.INTERNAL, attachmentEntity);

		// Assert
		verify(openEIntegrationMock).getAttachment(Instance.INTERNAL, 123);
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
		// Arrange
		var attachmentId = 123;
		var attachmentEntity = MessageAttachmentEntity.builder()
			.withExtension("pdf")
			.withAttachmentId(attachmentId)
			.withMimeType("application/pdf")
			.withName("someFile.pdf")
			.build();
		when(openEIntegrationMock.getAttachment(Instance.INTERNAL, attachmentId)).thenReturn(null);

		// Act
		service.fetchAttachment(Instance.INTERNAL, attachmentEntity);

		// Assert
		verify(openEIntegrationMock).getAttachment(Instance.INTERNAL, attachmentId);
		verifyNoInteractions(messageAttachmentRepositoryMock);
	}
*/
}
