package se.sundsvall.webmessagecollector;

import generated.se.sundsvall.oepintegrator.Webmessage;
import generated.se.sundsvall.oepintegrator.WebmessageAttachment;
import generated.se.sundsvall.oepintegrator.WebmessageAttachmentData;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.api.model.MessageAttachment;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;

public class TestDataFactory {

	public static Webmessage createWebMessage() {
		return new Webmessage()
			.message("message")
			.messageId("messageId")
			.id(123)
			.email("email")
			.familyId("familyId")
			.direction(Webmessage.DirectionEnum.INBOUND)
			.instance("INTERNAL")
			.firstName("firstName")
			.lastName("lastName")
			.externalCaseId("externalCaseId")
			.municipalityId("municipalityId")
			.sent(LocalDateTime.MIN)
			.username("username")
			.userId("userId")
			.attachments(List.of(createWebMessageAttachment()));
	}

	public static ResponseEntity<InputStreamResource> createEmptyResponse() {
		return ResponseEntity.ok().build();
	}

	public static ResponseEntity<InputStreamResource> createAttachmentStream(WebmessageAttachment attachment) {
		var contentDisposition = "attachment; filename=%s".formatted(attachment.getName());
		var contentType = attachment.getMimeType();

		return ResponseEntity.ok()
			.header("Content-Disposition", contentDisposition)
			.header("Content-Type", contentType)
			.body(new InputStreamResource(new ByteArrayInputStream(new byte[] {
				1, 2, 3
			})));
	}

	public static WebmessageAttachment createWebMessageAttachment() {
		return new WebmessageAttachment()
			.attachmentId(12345)
			.name("someFile.pdf")
			.extension("pdf")
			.mimeType("application/pdf");
	}

	public static WebmessageAttachmentData createWebMessageAttachmentData() {
		return new WebmessageAttachmentData()
			.data(new byte[] {
				1, 2, 3
			});
	}

	public static MessageDTO createMessageDTO() {
		return MessageDTO.builder()
			.withId(1)
			.withMunicipalityId("someMunicipalityId")
			.withMessageId("someMessageId")
			.withMessage("someMessage")
			.withExternalCaseId("someExternalCaseId")
			.withFamilyId("someFamilyId")
			.withDirection(Direction.OUTBOUND)
			.withSent(OffsetDateTime.now().toString())
			.withLastName("someLastName")
			.withFirstName("someFirstName")
			.withUsername("someUsername")
			.withEmail("someEmail")
			.withUserId("someUserid")
			.withInstance("someInstance")
			.withAttachments(List.of(MessageAttachment.builder().build()))
			.build();
	}
}
