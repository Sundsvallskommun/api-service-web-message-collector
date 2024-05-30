package se.sundsvall.webmessagecollector.integration.opene;

import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.integration.opene.model.Scope;

@Component
public class OpenEIntegration {

	private final InternalOpenEClient internalOpenEClient;

	private final ExternalOpenEClient externalOpenEClient;

	public OpenEIntegration(final InternalOpenEClient internalOpenEClient, final ExternalOpenEClient externalOpenEClient) {
		this.internalOpenEClient = internalOpenEClient;
		this.externalOpenEClient = externalOpenEClient;
	}

	public byte[] getMessages(final Scope scope, final String familyId, final String fromDate, final String toDate) {

		return switch (scope) {
			case INTERNAL -> internalOpenEClient.getMessages(familyId, fromDate, toDate);
			case EXTERNAL -> externalOpenEClient.getMessages(familyId, fromDate, toDate);
		};
	}

	public byte[] getAttachment(final Scope scope, final int attachmentId) {

		return switch (scope) {
			case INTERNAL -> internalOpenEClient.getAttachment(attachmentId);
			case EXTERNAL -> externalOpenEClient.getAttachment(attachmentId);
		};
	}


}
