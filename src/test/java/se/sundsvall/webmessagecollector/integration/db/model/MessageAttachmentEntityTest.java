package se.sundsvall.webmessagecollector.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
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
			hasValidBeanHashCodeExcluding("message"),
			hasValidBeanEqualsExcluding("message"),
			hasValidBeanToStringExcluding("message")));
	}

	@Test
	void builder() throws SQLException {
		var attachmentId = 1;
		var name = "fileName";
		var file = new SerialBlob(new byte[0]);
		var mimeType = "text/plain";
		var extension = "txt";
		var message = new MessageEntity();

		var result = MessageAttachmentEntity.builder()
			.withFile(file)
			.withAttachmentId(attachmentId)
			.withName(name)
			.withMimeType(mimeType)
			.withExtension(extension)
			.withMessage(message)
			.build();

		assertThat(result).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(result.getAttachmentId()).isEqualTo(attachmentId);
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getFile()).isSameAs(file);
		assertThat(result.getExtension()).isEqualTo(extension);
		assertThat(result.getMimeType()).isEqualTo(mimeType);
		assertThat(result.getMessage()).isSameAs(message);
	}

	@Test
	void noDirtOnCreatedBean() {
		assertThat(MessageAttachmentEntity.builder().build()).hasAllNullFieldsOrProperties();
		assertThat(new MessageAttachmentEntity()).hasAllNullFieldsOrProperties();
	}
}
