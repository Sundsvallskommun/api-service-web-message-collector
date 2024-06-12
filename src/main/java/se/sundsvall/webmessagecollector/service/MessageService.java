package se.sundsvall.webmessagecollector.service;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static se.sundsvall.webmessagecollector.service.MessageMapper.toMessageDTOs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@Service
public class MessageService {

	private final MessageRepository repository;

	private final MessageAttachmentRepository attachmentRepository;

	public MessageService(final MessageRepository repository, final MessageAttachmentRepository attachmentRepository) {
		this.repository = repository;
		this.attachmentRepository = attachmentRepository;
	}

	public List<MessageDTO> getMessages(final String familyId, final String instance) {
		return toMessageDTOs(repository.findAllByFamilyIdAndInstance(familyId, Instance.fromString(instance)));
	}

	public void getMessageAttachmentStreamed(final int attachmentID, final HttpServletResponse response) {
		try {
			final var attachmentEntity = attachmentRepository
				.findById(attachmentID)
				.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND, "MessageAttachment not found"));

			final var file = attachmentEntity.getFile();

			response.addHeader(CONTENT_TYPE, attachmentEntity.getMimeType());
			response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentEntity.getName() + "\"");
			response.setContentLength((int) file.length());
			StreamUtils.copy(file.getBinaryStream(), response.getOutputStream());
		} catch (final IOException | SQLException e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "%s occurred when copying file with attachment id '%s' to response: %s".formatted(e.getClass().getSimpleName(), attachmentID, e.getMessage()));
		}
	}

	public void deleteAttachment(final int id) {
		attachmentRepository.deleteById(id);
	}

	public void deleteMessages(final List<Integer> ids) {
		repository.deleteAllById(ids);
	}

}
