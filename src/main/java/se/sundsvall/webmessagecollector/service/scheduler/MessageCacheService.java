package se.sundsvall.webmessagecollector.service.scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.webmessagecollector.integration.db.ExecutionInformationRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;
import se.sundsvall.webmessagecollector.integration.oep.OepIntegratorIntegration;
import se.sundsvall.webmessagecollector.service.MessageMapper;

@Service
public class MessageCacheService {

	private static final Logger LOG = LoggerFactory.getLogger(MessageCacheService.class);
	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private final OepIntegratorIntegration oepIntegratorIntegration;
	private final MessageRepository messageRepository;
	private final MessageAttachmentRepository messageAttachmentRepository;
	private final ExecutionInformationRepository executionInformationRepository;

	MessageCacheService(
		final OepIntegratorIntegration oepIntegratorIntegration,
		final MessageRepository messageRepository,
		final MessageAttachmentRepository messageAttachmentRepository,
		final ExecutionInformationRepository executionInformationRepository) {
		this.oepIntegratorIntegration = oepIntegratorIntegration;
		this.messageRepository = messageRepository;
		this.messageAttachmentRepository = messageAttachmentRepository;
		this.executionInformationRepository = executionInformationRepository;
	}

	@Transactional
	public List<MessageEntity> fetchAndSaveMessages(final String municipalityId, final Instance instance, final String familyId, final Duration clockSkew) {
		// Fetch info regarding last execution for fetching familyId (or initiate entity if no info exists)
		var executionInfo = executionInformationRepository.findById(familyId)
			.orElse(initiateExecutionInfo(municipalityId, familyId));
		// Calculate timestamp from when messages should be fetched
		var fromTimestamp = executionInfo.getLastSuccessfulExecution().minus(clockSkew).format(DATE_TIME_FORMAT);

		var startTime = OffsetDateTime.now();

		var webmessages = oepIntegratorIntegration.getWebmessageByFamilyId(municipalityId, instance, familyId, fromTimestamp, "");
		var messages = MessageMapper.toMessageEntities(webmessages).stream()
			.filter(message -> !messageRepository.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(message.getFamilyId(), message.getInstance(), message.getMessageId(), message.getExternalCaseId()))
			.toList();
		messageRepository.saveAllAndFlush(messages);

		// Update timestamp for last successful execution
		executionInfo.setLastSuccessfulExecution(startTime);

		executionInformationRepository.save(executionInfo);
		return messages;
	}

	@Transactional
	public void fetchAndSaveAttachment(final String municipalityId, final Instance instance, final MessageAttachmentEntity attachmentEntity) {
		try {
			var webmessageAttachmentData = oepIntegratorIntegration.getAttachmentById(municipalityId, instance, "", attachmentEntity.getAttachmentId());

			if (webmessageAttachmentData != null && webmessageAttachmentData.getData() != null && attachmentEntity.getFile() == null) {
				attachmentEntity.setFile(new SerialBlob(webmessageAttachmentData.getData()));
				messageAttachmentRepository.saveAndFlush(attachmentEntity);
			}
		} catch (Exception e) {
			LOG.error("Unable to fetch attachment: municipalityId:'{}', instance:'{}', entity:'{}' ", municipalityId, instance, attachmentEntity);
			throw new RuntimeException(e);
		}
	}

	@Transactional
	public void failedAttachments(MessageEntity message) {
		message.setStatus(MessageStatus.FAILED_ATTACHMENTS);
		message.setStatusTimestamp(LocalDateTime.now());
		messageRepository.save(message);
	}

	@Transactional
	public void complete(MessageEntity message) {
		message.setStatus(MessageStatus.COMPLETE);
		message.setStatusTimestamp(LocalDateTime.now());
		messageRepository.save(message);
	}

	@Transactional
	public List<MessageEntity> getRetryableMessages(String municipalityId) {
		return messageRepository.findAllByStatusInAndMunicipalityId(List.of(MessageStatus.FAILED_ATTACHMENTS, MessageStatus.PROCESSING), municipalityId);
	}

	@Transactional
	public void cleanUpDeletedMessages(Duration keepDeletedAfterLastSuccessfulFor, String municipalityId) {
		executionInformationRepository.findByMunicipalityId(municipalityId)
			.forEach(info -> messageRepository.deleteByStatusAndMunicipalityIdAndFamilyIdAndStatusTimestampIsBefore(
				MessageStatus.DELETED,
				municipalityId,
				info.getFamilyId(),
				info.getLastSuccessfulExecution().minus(keepDeletedAfterLastSuccessfulFor).toLocalDateTime()));
	}

	ExecutionInformationEntity initiateExecutionInfo(final String municipalityId, final String familyId) {
		return ExecutionInformationEntity.builder()
			.withMunicipalityId(municipalityId)
			.withFamilyId(familyId)
			.withLastSuccessfulExecution(OffsetDateTime.now().minusHours(1)) // minusHours(1) is a safety for not missing any messages on the first execution run
			.build();
	}

}
