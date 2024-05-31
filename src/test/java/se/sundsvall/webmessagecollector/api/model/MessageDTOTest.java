package se.sundsvall.webmessagecollector.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class MessageDTOTest {

	@Test
	void testBuildPattern() {
		final var direction = Direction.OUTBOUND;
		final var email = "email";
		final var externalCaseId = "123";
		final var familyId = "234";
		final var firstName = "firstName";
		final var id = 345;
		final var lastName = "lastName";
		final var message = "message";
		final var messageId = "456";
		final var sent = "sent";
		final var userId = "userId";
		final var username = "username";
		final var instance = "instance";
		final var attachments = List.of(MessageAttachment.builder().build());

		final var bean = MessageDTO.builder()
			.withDirection(direction)
			.withEmail(email)
			.withExternalCaseId(externalCaseId)
			.withFamilyId(familyId)
			.withFirstName(firstName)
			.withId(id)
			.withLastName(lastName)
			.withMessage(message)
			.withMessageId(messageId)
			.withSent(sent)
			.withUserId(userId)
			.withUsername(username)
			.withInstance(instance)
			.withAttachments(attachments)
			.build();

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getDirection()).isEqualTo(direction);
		assertThat(bean.getEmail()).isEqualTo(email);
		assertThat(bean.getExternalCaseId()).isEqualTo(externalCaseId);
		assertThat(bean.getFamilyId()).isEqualTo(familyId);
		assertThat(bean.getFirstName()).isEqualTo(firstName);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getLastName()).isEqualTo(lastName);
		assertThat(bean.getMessage()).isEqualTo(message);
		assertThat(bean.getMessageId()).isEqualTo(messageId);
		assertThat(bean.getSent()).isEqualTo(sent);
		assertThat(bean.getUserId()).isEqualTo(userId);
		assertThat(bean.getUsername()).isEqualTo(username);
		assertThat(bean.getInstance()).isEqualTo(instance);
		assertThat(bean.getAttachments()).isSameAs(attachments);
	}

}
