package se.sundsvall.webmessagecollector.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MessageDTOTest {

	@Test
	void testBuildPattern() {
		var direction = Direction.OUTBOUND;
		var email = "email";
		var externalCaseId = "123";
		var municipalityId = "1984";
		var familyId = "234";
		var firstName = "firstName";
		var id = 345;
		var lastName = "lastName";
		var message = "message";
		var messageId = "456";
		var sent = "sent";
		var userId = "userId";
		var username = "username";
		var instance = "instance";
		var attachments = List.of(MessageAttachment.builder().build());

		var bean = MessageDTO.builder()
			.withMunicipalityId(municipalityId)
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
		assertThat(bean.getMunicipalityId()).isEqualTo(municipalityId);
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
