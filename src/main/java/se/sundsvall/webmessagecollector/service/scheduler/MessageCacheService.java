package se.sundsvall.webmessagecollector.service.scheduler;

import static se.sundsvall.webmessagecollector.integration.opene.OpenEMapper.toMessageEntities;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.sundsvall.webmessagecollector.integration.db.ExecutionInformationRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.OpenEIntegration;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@Component
public class MessageCacheService {

	private static final Logger LOG = LoggerFactory.getLogger(MessageCacheService.class);


	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	private final OpenEIntegration openEIntegration;

	private final MessageRepository messageRepository;

	private final MessageAttachmentRepository messageAttachmentRepository;

	private final ExecutionInformationRepository executionInformationRepository;

	MessageCacheService(final OpenEIntegration openEIntegration, final MessageRepository messageRepository, final MessageAttachmentRepository messageAttachmentRepository, final ExecutionInformationRepository executionInformationRepository) {
		this.openEIntegration = openEIntegration;
		this.messageRepository = messageRepository;
		this.messageAttachmentRepository = messageAttachmentRepository;
		this.executionInformationRepository = executionInformationRepository;
	}

	@Transactional
	public List<MessageEntity> fetchMessages(final Instance instance, final String familyId) {
		// Fetch info regarding last execution for fetching familyId (or initiate entity if no info exists)
		final var executionInfo = executionInformationRepository.findById(familyId).orElse(initiateExecutionInfo(familyId));
		// Calculate timestamp from when messages should be fetched
		final var fromTimestamp = executionInfo.getLastSuccessfulExecution().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
		// Update timestamp for last execution
		executionInfo.setLastSuccessfulExecution(OffsetDateTime.now());

		final var bytes = openEIntegration.getMessages(instance, familyId, fromTimestamp, "");
		final var messages = toMessageEntities(bytes, familyId, instance);
		messageRepository.saveAllAndFlush(messages);

		executionInformationRepository.save(executionInfo);
		return messages;
	}

	@Transactional
	public void fetchAttachment(final Instance instance, final MessageAttachmentEntity attachmentEntity) {
		try {
			final var attachmentStream = openEIntegration.getAttachment(instance, attachmentEntity.getAttachmentId());
			if (attachmentStream != null) {
				attachmentEntity.setFile(new SerialBlob(attachmentStream));
				messageAttachmentRepository.saveAndFlush(attachmentEntity);
			}
		} catch (final SQLException e) {
			LOG.error("Unable to fetch Attachment ", e);
		}
	}

	private ExecutionInformationEntity initiateExecutionInfo(final String familyId) {
		return ExecutionInformationEntity.builder()
			.withFamilyId(familyId)
			.withLastSuccessfulExecution(OffsetDateTime.now().minusHours(1)) // minusHours(1) is a safety for not missing any messages on the first execution run
			.build();
	}

}
