package se.sundsvall.webmessagecollector.integration.opene.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class ExternalMessageAttachmentTest {

	@Test
	void bean() {
		MatcherAssert.assertThat(ExternalMessageAttachment.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void gettersAndSetters() {
		// Arrange
		final var attachmentID = 123;
		final var fileName = "someFileName";

		// Act
		final var externalMessageAttachment = new ExternalMessageAttachment();
		externalMessageAttachment.setAttachmentID(attachmentID);
		externalMessageAttachment.setFileName(fileName);

		// Assert
		assertThat(externalMessageAttachment.getAttachmentID()).isEqualTo(attachmentID);
		assertThat(externalMessageAttachment.getFileName()).isEqualTo(fileName);
	}


	@Test
	void noDirtOnCreatedBean() {
		assertThat(new ExternalMessageAttachment()).hasAllNullFieldsOrPropertiesExcept("attachmentID").satisfies(
			externalMessageAttachment -> {
				assertThat(externalMessageAttachment.getAttachmentID()).isZero();
			}
		);
	}

}
