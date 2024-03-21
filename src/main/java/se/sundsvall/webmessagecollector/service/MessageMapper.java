package se.sundsvall.webmessagecollector.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Objects;

import se.sundsvall.webmessagecollector.api.model.MessageAttachment;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

final class MessageMapper {

	private MessageMapper() {}

	static MessageDTO toMessageDTO(final MessageEntity entity) {
		return ofNullable(entity)
			.map(e -> MessageDTO.builder()
				.withId(e.getId())
				.withFamilyId(e.getFamilyId())
				.withDirection(e.getDirection())
				.withMessageId(e.getMessageId())
				.withExternalCaseId(e.getExternalCaseId())
				.withMessage(e.getMessage())
				.withSent(String.valueOf(e.getSent()))
				.withEmail(e.getEmail())
				.withFirstName(e.getFirstName())
				.withLastName(e.getLastName())
				.withUsername(e.getUsername())
				.withUserId(e.getUserId())
				.withAttachments(toMessageAttachmentDTOs(e.getAttachments()))
				.build())
			.orElse(null);
	}

	static List<MessageAttachment> toMessageAttachmentDTOs(final List<MessageAttachmentEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(MessageMapper::toMessageAttachmentDTO)
			.filter(Objects::nonNull)
			.toList();
	}

	static MessageAttachment toMessageAttachmentDTO(final MessageAttachmentEntity entity) {
		return ofNullable(entity)
			.map(e -> MessageAttachment.builder()
				.withAttachmentId(e.getAttachmentId())
				.withExtension(e.getExtension())
				.withMimeType(e.getMimeType())
				.withName(e.getName())
				.build())
			.orElse(null);
	}

	static List<MessageDTO> toMessageDTOs(final List<MessageEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(MessageMapper::toMessageDTO)
			.filter(Objects::nonNull)
			.toList();
	}

}
