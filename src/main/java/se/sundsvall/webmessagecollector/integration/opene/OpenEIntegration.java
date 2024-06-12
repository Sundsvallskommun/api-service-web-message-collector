package se.sundsvall.webmessagecollector.integration.opene;

import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

@Component
public class OpenEIntegration {

	private final InternalOpenEClient internalOpenEClient;

	private final ExternalOpenEClient externalOpenEClient;

	public OpenEIntegration(final InternalOpenEClient internalOpenEClient, final ExternalOpenEClient externalOpenEClient) {
		this.internalOpenEClient = internalOpenEClient;
		this.externalOpenEClient = externalOpenEClient;
	}

	public byte[] getMessages(final Instance instance, final String familyId, final String fromDate, final String toDate) {

		return switch (instance) {
			case INTERNAL -> internalOpenEClient.getMessages(familyId, fromDate, toDate);
			case EXTERNAL -> externalOpenEClient.getMessages(familyId, fromDate, toDate);
		};
	}

	public byte[] getAttachment(final Instance instance, final int attachmentId) {

		return switch (instance) {
			case INTERNAL -> internalOpenEClient.getAttachment(attachmentId);
			case EXTERNAL -> externalOpenEClient.getAttachment(attachmentId);
		};
	}


}
