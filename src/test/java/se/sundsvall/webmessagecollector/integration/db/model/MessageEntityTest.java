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
		final var direction = Direction.OUTBOUND;
		final var email = "email";
		final var externalCaseId = "123";
		final var familyId = "234";
		final var firstName = "firstName";
		final var id = 345;
		final var lastName = "lastName";
		final var message = "message";
		final var messageId = "456";
		final var sent = LocalDateTime.now();
		final var userId = "userId";
		final var username = "username";
		final var attachments = List.of(MessageAttachmentEntity.builder().build());

		final var bean = MessageEntity.builder()
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
			.build();

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
	}

}
