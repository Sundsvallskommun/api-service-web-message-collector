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

class MessagesTest {

	@Test
	void bean() {
		MatcherAssert.assertThat(Messages.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanToString(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void gettersAndSetters() {
		// Arrange
		final var externalMessages = List.of(new ExternalMessage());

		// Act
		final var messages = new Messages();
		messages.setExternalMessages(externalMessages);

		// Assert
		assertThat(messages.getExternalMessages()).isSameAs(externalMessages);
	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(new Messages()).hasAllNullFieldsOrProperties();
	}

}
