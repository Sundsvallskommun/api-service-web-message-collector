package se.sundsvall.webmessagecollector;

import generated.se.sundsvall.oepintegrator.Webmessage;
import generated.se.sundsvall.oepintegrator.WebmessageAttachment;
import generated.se.sundsvall.oepintegrator.WebmessageAttachmentData;
import java.time.LocalDateTime;
import java.util.List;

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
}
