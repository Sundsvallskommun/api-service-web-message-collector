package se.sundsvall.webmessagecollector.integration.oep;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessage;
import static se.sundsvall.webmessagecollector.TestDataFactory.createWebMessageAttachment;
import static se.sundsvall.webmessagecollector.integration.oep.OepIntegratorMapper.DATE_TIME_FORMAT;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.api.model.MessageAttachment;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;

class OepIntegratorMapperTest {

	@Test
	void toMessages() {
		var webMessages = List.of(createWebMessage(), createWebMessage());

		var result = OepIntegratorMapper.toMessages(webMessages);

		assertThat(result).isNotNull().hasSize(2);
	}

	@Test
	void toMessage() {
		var webMessage = createWebMessage();

		var result = OepIntegratorMapper.toMessage(webMessage);

		assertThat(result).isNotNull().isInstanceOf(MessageDTO.class).satisfies(message -> {
			assertThat(message.getId()).isEqualTo(webMessage.getId());
			assertThat(message.getMessage()).isEqualTo(webMessage.getMessage());
			assertThat(message.getMessageId()).isEqualTo(webMessage.getMessageId());
			assertThat(message.getDirection()).isEqualTo(Direction.valueOf(webMessage.getDirection().name()));
			assertThat(message.getSent()).isEqualTo(webMessage.getSent().format(DATE_TIME_FORMAT));
			assertThat(message.getEmail()).isEqualTo(webMessage.getEmail());
			assertThat(message.getUserId()).isEqualTo(webMessage.getUserId());
			assertThat(message.getExternalCaseId()).isEqualTo(webMessage.getExternalCaseId());
			assertThat(message.getFirstName()).isEqualTo(webMessage.getFirstName());
			assertThat(message.getLastName()).isEqualTo(webMessage.getLastName());
			assertThat(message.getFamilyId()).isEqualTo(webMessage.getFamilyId());
			assertThat(message.getInstance()).isEqualTo(webMessage.getInstance());
			assertThat(message.getUsername()).isEqualTo(webMessage.getUsername());
			assertThat(message.getMunicipalityId()).isEqualTo(webMessage.getMunicipalityId());
		});
	}

	@Test
	void toMessageAttachments() {
		var webMessageAttachments = List.of(createWebMessageAttachment(), createWebMessageAttachment());

		var result = OepIntegratorMapper.toMessageAttachments(webMessageAttachments);

		assertThat(result).isNotNull().hasSize(2);
	}

	@Test
	void toMessageAttachment() {
		var webMessageAttachment = createWebMessageAttachment();

		var result = OepIntegratorMapper.toMessageAttachment(webMessageAttachment);

		assertThat(result).isNotNull().isInstanceOf(MessageAttachment.class).satisfies(attachment -> {
			assertThat(attachment.getName()).isEqualTo(webMessageAttachment.getName());
			assertThat(attachment.getMimeType()).isEqualTo(webMessageAttachment.getMimeType());
			assertThat(attachment.getAttachmentId()).isEqualTo(webMessageAttachment.getAttachmentId());
			assertThat(attachment.getMimeType()).isEqualTo(webMessageAttachment.getMimeType());
		});
	}

	@Test
	void toMessageEntity() {
		var webMessage = createWebMessage();

		var result = OepIntegratorMapper.toMessageEntity(webMessage);

		assertThat(result).isNotNull().isInstanceOf(MessageEntity.class).satisfies(entity -> {
			assertThat(entity.getFamilyId()).isEqualTo(webMessage.getFamilyId());
			assertThat(entity.getInstance()).isEqualTo(Instance.valueOf(webMessage.getInstance()));
			assertThat(entity.getDirection()).isEqualTo(Direction.valueOf(webMessage.getDirection().name()));
			assertThat(entity.getMessageId()).isEqualTo(webMessage.getMessageId());
			assertThat(entity.getExternalCaseId()).isEqualTo(webMessage.getExternalCaseId());
			assertThat(entity.getMunicipalityId()).isEqualTo(webMessage.getMunicipalityId());
			assertThat(entity.getMessage()).isEqualTo(webMessage.getMessage());
			assertThat(entity.getSent()).isEqualTo(webMessage.getSent());
			assertThat(entity.getEmail()).isEqualTo(webMessage.getEmail());
			assertThat(entity.getUserId()).isEqualTo(webMessage.getUserId());
			assertThat(entity.getFirstName()).isEqualTo(webMessage.getFirstName());
			assertThat(entity.getLastName()).isEqualTo(webMessage.getLastName());
			assertThat(entity.getUsername()).isEqualTo(webMessage.getUsername());
			assertThat(entity.getStatus()).isEqualTo(MessageStatus.PROCESSING);
		});
	}

	@Test
	void toMessageEntities() {
		var webMessages = List.of(createWebMessage(), createWebMessage());

		var result = OepIntegratorMapper.toMessageEntities(webMessages);

		assertThat(result).isNotNull().hasSize(2);
	}

	@Test
	void toMessageAttachmentEntities() {
		var webMessageAttachments = List.of(createWebMessageAttachment(), createWebMessageAttachment());

		var result = OepIntegratorMapper.toMessageAttachmentEntities(webMessageAttachments);

		assertThat(result).isNotNull().hasSize(2);
	}

	@Test
	void toMessageAttachmentEntity() {
		var webMessageAttachment = createWebMessageAttachment();

		var result = OepIntegratorMapper.toMessageAttachmentEntity(webMessageAttachment);

		assertThat(result).isNotNull().isInstanceOf(MessageAttachmentEntity.class).satisfies(entity -> {
			assertThat(entity.getName()).isEqualTo(webMessageAttachment.getName());
			assertThat(entity.getMimeType()).isEqualTo(webMessageAttachment.getMimeType());
			assertThat(entity.getAttachmentId()).isEqualTo(webMessageAttachment.getAttachmentId());
			assertThat(entity.getMimeType()).isEqualTo(webMessageAttachment.getMimeType());
		});
	}

}
