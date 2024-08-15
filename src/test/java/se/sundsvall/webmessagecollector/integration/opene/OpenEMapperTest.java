package se.sundsvall.webmessagecollector.integration.opene;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.sundsvall.webmessagecollector.integration.opene.OpenEMapper.toMessageEntities;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@ExtendWith(ResourceLoaderExtension.class)
@ActiveProfiles("junit")
class OpenEMapperTest {

	@Test
	void mapWhenInputIsNull() {
		var familyId = "familyId";

		assertThat(toMessageEntities(null, familyId, Instance.EXTERNAL)).isEmpty();
	}

	@Test
	void mapWithNonValidXML() {
		var familyId = "123";
		var bytes = "{this is not a valid xml}".getBytes(ISO_8859_1);

		var e = assertThrows(ThrowableProblem.class, () -> toMessageEntities(bytes, familyId, Instance.EXTERNAL));

		assertThat(e.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR);
		assertThat(e.getMessage()).isEqualToIgnoringNewLines(
			"Internal Server Error: JsonParseException occurred when parsing open-e messages for familyId 123. Message is: Unexpected character '{' (code 123) in prolog; expected '<'\r\n at [row,col {unknown-source}]: [1,1]");
	}

	@Test
	void mapWhenExternalMessagesAreNull(@Load(value = "/empty-messages.xml") final String input) {
		var familyId = "123";
		var bytes = input.getBytes(ISO_8859_1);

		assertThat(toMessageEntities(bytes, familyId, Instance.INTERNAL)).isEmpty();
	}

	@Test
	void mapMessagesWithValidXML(@Load("/messages.xml") final String input) {
		var familyId = "123";
		var bytes = input.getBytes(ISO_8859_1);

		var result = toMessageEntities(bytes, familyId, Instance.INTERNAL);

		assertThat(result).hasSize(1)
			.allSatisfy(entity -> {
				assertThat(entity.getDirection()).isEqualTo(Direction.INBOUND);
				assertThat(entity.getEmail()).isEqualTo("email_1@test.se");
				assertThat(entity.getExternalCaseId()).isEqualTo("102251");
				assertThat(entity.getFamilyId()).isEqualTo(familyId);
				assertThat(entity.getFirstName()).isEqualTo("firstName_1");
				assertThat(entity.getId()).isNull();
				assertThat(entity.getLastName()).isEqualTo("lastName_1");
				assertThat(entity.getMessage()).isEqualTo("Inbound message");
				assertThat(entity.getMessageId()).isEqualTo("10");
				assertThat(entity.getSent()).isEqualTo(LocalDateTime.of(2022, 5, 25, 11, 20));
				assertThat(entity.getUserId()).isEqualTo("1");
				assertThat(entity.getUsername()).isEqualTo("userName_1");
				assertThat(entity.getInstance()).isEqualTo(Instance.INTERNAL);
				assertThat(entity.getAttachments()).hasSize(1)
					.allSatisfy(attachment -> {
						assertThat(attachment.getAttachmentId()).isEqualTo(164);
						assertThat(attachment.getName()).isEqualTo("test.pdf");
						assertThat(attachment.getExtension()).isEqualTo("pdf");
						assertThat(attachment.getMimeType()).isEqualTo("application/pdf");
					});
			});
	}

}
