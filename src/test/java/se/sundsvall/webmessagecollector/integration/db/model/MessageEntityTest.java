package se.sundsvall.webmessagecollector.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.webmessagecollector.api.model.Direction;

class MessageEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> LocalDateTime.now().plusDays((new Random().nextInt())), LocalDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(MessageEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanToString(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

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
		var sent = LocalDateTime.now();
		var userId = "userId";
		var username = "username";
		var attachments = List.of(MessageAttachmentEntity.builder().build());
		var instance = Instance.INTERNAL;

		var bean = MessageEntity.builder()
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
			.withAttachments(attachments)
			.withInstance(instance)
			.build();

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
		assertThat(bean.getAttachments()).isSameAs(attachments);
		assertThat(bean.getInstance()).isEqualTo(instance);
	}
}
