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
			.map(nonNullEntity -> MessageDTO.builder()
				.withId(nonNullEntity.getId())
				.withMunicipalityId(nonNullEntity.getMunicipalityId())
				.withFamilyId(nonNullEntity.getFamilyId())
				.withDirection(nonNullEntity.getDirection())
				.withMessageId(nonNullEntity.getMessageId())
				.withExternalCaseId(nonNullEntity.getExternalCaseId())
				.withMessage(nonNullEntity.getMessage())
				.withSent(String.valueOf(nonNullEntity.getSent()))
				.withEmail(nonNullEntity.getEmail())
				.withFirstName(nonNullEntity.getFirstName())
				.withLastName(nonNullEntity.getLastName())
				.withUsername(nonNullEntity.getUsername())
				.withUserId(nonNullEntity.getUserId())
				.withAttachments(toMessageAttachmentDTOs(nonNullEntity.getAttachments()))
				.withInstance(String.valueOf(nonNullEntity.getInstance()))
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
			.map(nonNullEntity -> MessageAttachment.builder()
				.withAttachmentId(nonNullEntity.getAttachmentId())
				.withExtension(nonNullEntity.getExtension())
				.withMimeType(nonNullEntity.getMimeType())
				.withName(nonNullEntity.getName())
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
