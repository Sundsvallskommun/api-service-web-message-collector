package se.sundsvall.webmessagecollector;

import generated.se.sundsvall.oepintegrator.Webmessage;
import generated.se.sundsvall.oepintegrator.WebmessageAttachmentData;
import java.time.LocalDateTime;

public class TestDataFactory {

	public static Webmessage createWebMessage() {
		return new Webmessage()
			.message("message")
			.messageId("messageId")
			.id(123)
			.email("email")
			.familyId("familyId")
			.direction(Webmessage.DirectionEnum.INBOUND)
			.firstName("firstName")
			.lastName("lastName")
			.instance("instance")
			.externalCaseId("externalCaseId")
			.municipalityId("municipalityId")
			.sent(LocalDateTime.MIN)
			.username("username")
			.userId("userId");
	}

	public static WebmessageAttachmentData createWebMessageAttachmentData() {
		return new WebmessageAttachmentData()
			.data(new byte[] {
				1, 2, 3
			});
	}
}
