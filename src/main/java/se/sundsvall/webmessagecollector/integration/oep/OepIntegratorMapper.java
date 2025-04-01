package se.sundsvall.webmessagecollector.integration.oep;

import static java.util.Optional.ofNullable;

import generated.se.sundsvall.oepintegrator.Webmessage;
import generated.se.sundsvall.oepintegrator.WebmessageAttachment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.api.model.MessageAttachment;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;

public final class OepIntegratorMapper {

	private OepIntegratorMapper() {}

	static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public static List<MessageDTO> toMessages(final List<Webmessage> webmessages) {
		return webmessages.stream()
			.map(OepIntegratorMapper::toMessage)
			.toList();
	}

	static MessageDTO toMessage(final Webmessage webmessage) {
		return MessageDTO.builder()
			.withId(webmessage.getId())
			.withMessage(webmessage.getMessage())
			.withMessageId(webmessage.getMessageId())
			.withAttachments(toMessageAttachments(webmessage.getAttachments()))
			.withDirection(ofNullable(webmessage.getDirection()).map(direction -> Direction.valueOf(direction.name())).orElse(null))
			.withSent(ofNullable(webmessage.getSent()).map(time -> time.format(DATE_TIME_FORMAT)).orElse(null))
			.withEmail(webmessage.getEmail())
			.withUserId(webmessage.getUserId())
			.withExternalCaseId(webmessage.getExternalCaseId())
			.withFirstName(webmessage.getFirstName())
			.withLastName(webmessage.getLastName())
			.withFamilyId(webmessage.getFamilyId())
			.withInstance(webmessage.getInstance())
			.withUsername(webmessage.getUsername())
			.withMunicipalityId(webmessage.getMunicipalityId())
			.build();
	}

	static List<MessageAttachment> toMessageAttachments(final List<WebmessageAttachment> webmessageAttachments) {
		return webmessageAttachments.stream()
			.map(OepIntegratorMapper::toMessageAttachment)
			.toList();
	}

	static MessageAttachment toMessageAttachment(final WebmessageAttachment webmessageAttachment) {
		return ofNullable(webmessageAttachment).map(attachment -> MessageAttachment.builder()
			.withName(webmessageAttachment.getName())
			.withAttachmentId(webmessageAttachment.getAttachmentId())
			.withExtension(webmessageAttachment.getExtension())
			.withMimeType(webmessageAttachment.getMimeType())
			.build())
			.orElse(null);
	}

	public static List<MessageEntity> toMessageEntities(final List<Webmessage> webmessages) {
		return webmessages.stream()
			.map(OepIntegratorMapper::toMessageEntity)
			.toList();
	}

	static MessageEntity toMessageEntity(final Webmessage webmessage) {
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
			.map(OepIntegratorMapper::toMessageAttachmentEntity)
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

}
