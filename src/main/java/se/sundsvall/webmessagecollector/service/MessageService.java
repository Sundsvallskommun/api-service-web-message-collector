package se.sundsvall.webmessagecollector.service;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static se.sundsvall.webmessagecollector.service.MessageMapper.toMessageDTOs;
import static se.sundsvall.webmessagecollector.utility.StreamUtils.setResponse;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.MessageAttachmentRepository;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;
import se.sundsvall.webmessagecollector.integration.oep.OepIntegratorIntegration;
import se.sundsvall.webmessagecollector.integration.oep.OepIntegratorMapper;

@Service
public class MessageService {

	private final MessageRepository repository;
	private final MessageAttachmentRepository attachmentRepository;

	private final OepIntegratorIntegration oepIntegratorIntegration;

	public MessageService(
		final MessageRepository repository,
		final MessageAttachmentRepository attachmentRepository,
		final OepIntegratorIntegration oepIntegratorIntegration) {
		this.repository = repository;
		this.attachmentRepository = attachmentRepository;
		this.oepIntegratorIntegration = oepIntegratorIntegration;
	}

	public List<MessageDTO> getMessages(final String municipalityId, final String familyId, final String instance) {
		return toMessageDTOs(repository.findAllByMunicipalityIdAndFamilyIdAndInstanceAndStatus(municipalityId, familyId, Instance.valueOf(instance), MessageStatus.COMPLETE));
	}

	public void getMessageAttachmentStreamed(final int attachmentID, final HttpServletResponse response) {
		try {
			var attachmentEntity = attachmentRepository.findById(attachmentID)
				.orElseThrow(() -> Problem.valueOf(Status.NOT_FOUND, "MessageAttachment not found"));

			var file = attachmentEntity.getFile();

			response.addHeader(CONTENT_TYPE, attachmentEntity.getMimeType());
			response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentEntity.getName() + "\"");
			response.setContentLength((int) file.length());
			StreamUtils.copy(file.getBinaryStream(), response.getOutputStream());
		} catch (IOException | SQLException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "%s occurred when copying file with attachment id '%s' to response: %s".formatted(e.getClass().getSimpleName(), attachmentID, e.getMessage()));
		}
	}

	public void deleteAttachment(final int id) {
		attachmentRepository.deleteById(id);
	}

	public void deleteMessages(final List<Integer> ids) {
		var messages = repository.findAllById(ids);
		messages.forEach(message -> message.setStatus(MessageStatus.DELETED));
		repository.saveAll(messages);
	}

	public List<MessageDTO> getMessagesByFlowInstanceId(final String municipalityId, final String instance, final String flowInstanceId, final LocalDateTime from, final LocalDateTime to) {
		var webMessages = oepIntegratorIntegration.getWebmessagesByFlowInstanceId(municipalityId, Instance.valueOf(instance), flowInstanceId, from, to);
		return OepIntegratorMapper.toMessages(webMessages);
	}

	public void streamAttachmentById(final String municipalityId, final String instance, final Integer attachmentId, final HttpServletResponse response) {
		var responseEntity = oepIntegratorIntegration.getAttachmentStreamById(municipalityId, Instance.valueOf(instance), "", attachmentId);
		setResponse(responseEntity, response, "Failed to stream attachment with id: " + attachmentId);
	}

}
