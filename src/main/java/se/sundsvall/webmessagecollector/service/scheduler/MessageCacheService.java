package se.sundsvall.webmessagecollector.service.scheduler;

import static se.sundsvall.webmessagecollector.integration.opene.OpenEMapper.toMessageEntities;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.sundsvall.webmessagecollector.integration.db.ExecutionInformationRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;
import se.sundsvall.webmessagecollector.integration.opene.OpenEClient;

@Component
public class MessageCacheService {
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	private final OpenEClient openEClient;
	private final MessageRepository messageRepository;
	private final ExecutionInformationRepository executionInformationRepository;

	MessageCacheService(OpenEClient openEClient, MessageRepository messageRepository, ExecutionInformationRepository executionInformationRepository) {
		this.openEClient = openEClient;
		this.messageRepository = messageRepository;
		this.executionInformationRepository = executionInformationRepository;
	}

	@Transactional
	public void fetchMessages(String familyId) {
		// Fetch info regarding last execution for fetching familyId (or initiate entity if no info exists)
		final var executionInfo = executionInformationRepository.findById(familyId).orElse(initiateExecutionInfo(familyId));
		// Calculate timestamp from when messages should be fetched
		final var fromTimestamp = executionInfo.getLastSuccessfulExecution().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
		// Update timestamp for last execution
		executionInfo.setLastSuccessfulExecution(OffsetDateTime.now());

		final var bytes = openEClient.getMessages(familyId, fromTimestamp, "");
		final var messages = toMessageEntities(bytes, familyId);
		messageRepository.saveAll(messages);

		executionInformationRepository.save(executionInfo);
	}

	private ExecutionInformationEntity initiateExecutionInfo(String familyId) {
		return ExecutionInformationEntity.builder()
			.withFamilyId(familyId)
			.withLastSuccessfulExecution(OffsetDateTime.now().minusHours(1)) // minusHours(1) is a safety for not missing any messages on the first execution run
			.build();
	}
}
