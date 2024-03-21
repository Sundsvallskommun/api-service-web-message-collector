package se.sundsvall.webmessagecollector.service;

import static se.sundsvall.webmessagecollector.service.MessageMapper.toMessageDTOs;

import java.util.List;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;

@Service
public class MessageService {

	private static final Logger log = LoggerFactory.getLogger(MessageService.class);

	private final MessageRepository repository;

	private final MessageAttachmentRepository attachmentRepository;

	public MessageService(final MessageRepository repository, final MessageAttachmentRepository attachmentRepository) {
		this.repository = repository;
		this.attachmentRepository = attachmentRepository;
	}

	public List<MessageDTO> getMessages(final String familyId) {
		return toMessageDTOs(repository.findAllByFamilyId(familyId));
	}

	public String getAttachment(final int attachmentId) {
		return attachmentRepository.findById(attachmentId).map(messageAttachmentEntity -> {
			try {
				final var file = messageAttachmentEntity.getFile();
				return Base64.toBase64String(file.getBytes(1, (int) file.length()));
			} catch (final Exception e) {
				log.error("Could not fetch attachment: {}", e.getMessage());
				throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Could not fetch attachment");
			}
		}).orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND, "Attachment not found"));
	}

	public void deleteAttachment(final int id) {
		attachmentRepository.deleteById(id);
	}

	public void deleteMessages(final List<Integer> ids) {
		repository.deleteAllById(ids);
	}

}
