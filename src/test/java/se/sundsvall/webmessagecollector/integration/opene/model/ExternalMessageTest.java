package se.sundsvall.webmessagecollector.integration.opene.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class ExternalMessageTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(ExternalMessage.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanToString(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void gettersAndSetters() {
		// Arrange
		final var messageID = 123;
		final var message = "someMessage";
		final var poster = new Poster();
		final var added = "someAdded";
		final var sent = "someSent";
		final var flowInstanceID = 123;
		final var attachments = List.of(new ExternalMessageAttachment());

		// Act
		final var externalMessage = new ExternalMessage();
		externalMessage.setMessageID(messageID);
		externalMessage.setMessage(message);
		externalMessage.setPoster(poster);
		externalMessage.setAdded(added);
		externalMessage.setFlowInstanceID(flowInstanceID);
		externalMessage.setAttachments(attachments);

		// Assert
		assertThat(externalMessage.getMessageID()).isEqualTo(messageID);
		assertThat(externalMessage.getMessage()).isEqualTo(message);
		assertThat(externalMessage.getPoster()).isEqualTo(poster);
		assertThat(externalMessage.getAdded()).isEqualTo(added);
		assertThat(externalMessage.getFlowInstanceID()).isEqualTo(flowInstanceID);
		assertThat(externalMessage.getAttachments()).isEqualTo(attachments);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new ExternalMessage()).hasAllNullFieldsOrPropertiesExcept("postedByManager", "messageID", "flowInstanceID").satisfies(
			externalMessage -> {
				assertThat(externalMessage.getMessageID()).isZero();
				assertThat(externalMessage.getFlowInstanceID()).isZero();
				assertThat(externalMessage.isPostedByManager()).isFalse();
			}
		);
	}

}
