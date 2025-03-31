package se.sundsvall.webmessagecollector.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import generated.se.sundsvall.oepintegrator.Webmessage;
import generated.se.sundsvall.oepintegrator.WebmessageAttachment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.api.model.MessageAttachment;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;

public final class MessageMapper {

	private MessageMapper() {}

	public static List<MessageEntity> toMessageEntities(final List<Webmessage> webmessages) {
		return webmessages.stream()
			.map(MessageMapper::toMessageEntity)
			.toList();
	}

	public static MessageEntity toMessageEntity(final Webmessage webmessage) {
		var messageEntity = MessageEntity.builder()
			.withFamilyId(webmessage.getFamilyId())
			.withInstance(Instance.valueOf(webmessage.getInstance()))
			.withDirection(Direction.valueOf(webmessage.getDirection().getValue()))
			.withMessageId(webmessage.getMessageId())
			.withExternalCaseId(webmessage.getExternalCaseId())
			.withMessage(webmessage.getMessage())
			.withMunicipalityId(webmessage.getMunicipalityId())
			.withSent(webmessage.getSent())
			.withStatus(MessageStatus.PROCESSING)
			.withStatusTimestamp(LocalDateTime.now())
			.withEmail(webmessage.getEmail())
			.withFirstName(webmessage.getFirstName())
			.withLastName(webmessage.getLastName())
			.withUsername(webmessage.getUsername())
			.withUserId(webmessage.getUserId())
			.withAttachments(toMessageAttachmentEntities(webmessage.getAttachments()))
			.build();

		decorateAttachments(messageEntity);

		return messageEntity;
	}

	static void decorateAttachments(final MessageEntity messageEntity) {
		messageEntity.getAttachments()
			.forEach(attachment -> attachment.setMessage(messageEntity));
	}

	static List<MessageAttachmentEntity> toMessageAttachmentEntities(final List<WebmessageAttachment> webmessageAttachments) {
		return webmessageAttachments.stream()
			.map(MessageMapper::toMessageAttachmentEntity)
			.toList();
	}

	static MessageAttachmentEntity toMessageAttachmentEntity(final WebmessageAttachment webmessageAttachment) {
		return MessageAttachmentEntity.builder()
			.withAttachmentId(webmessageAttachment.getAttachmentId())
			.withName(webmessageAttachment.getName())
			.withMimeType(webmessageAttachment.getMimeType())
			.withExtension(webmessageAttachment.getExtension())
			.build();
	}

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
