package se.sundsvall.webmessagecollector.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class MessageAttachmentEntityTest {


	@Test
	void bean() {
		MatcherAssert.assertThat(MessageAttachmentEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}


	@Test
	void builder() throws SQLException {
		// Arrange
		final var attachmentId = 1;
		final var fileName = "fileName";
		final var file = new SerialBlob(new byte[0]);
		// Act
		final var result = MessageAttachmentEntity.builder()
			.withFile(file)
			.withAttachmentId(attachmentId)
			.withFileName(fileName)
			.build();
		// Assert
		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getAttachmentId()).isEqualTo(attachmentId);
		assertThat(result.getFileName()).isEqualTo(fileName);
		assertThat(result.getFile()).isEqualTo(file);
	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(MessageAttachmentEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new MessageAttachmentEntity()).hasAllNullFieldsOrProperties();
	}

}
