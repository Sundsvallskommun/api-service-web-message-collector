package se.sundsvall.webmessagecollector.integration.opene;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.model.ExternalMessage;
import se.sundsvall.webmessagecollector.integration.opene.model.Messages;
import se.sundsvall.webmessagecollector.integration.opene.model.Scope;

public final class OpenEMapper {

	private static final Logger LOG = LoggerFactory.getLogger(OpenEMapper.class);

	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		.appendPattern("yyyy-MM-dd")
		.optionalStart()
		.appendPattern(" HH:mm")
		.optionalEnd()
		.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
		.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		.toFormatter();

	private OpenEMapper() {
		// Intentionally Empty
	}

	public static List<MessageEntity> toMessageEntities(final byte[] errands, final String familyId, final Scope scope) {

		if (errands == null) {
			LOG.info("No messages found for familyId {}", familyId);
			return emptyList();
		}

		final var xmlString = new String(errands, StandardCharsets.ISO_8859_1);
		try {
			return ofNullable(new XmlMapper().readValue(xmlString, Messages.class).getExternalMessages())
				.orElse(emptyList())
				.stream()
				.filter(externalMessage -> !externalMessage.isPostedByManager())
				.map(externalMessage -> toMessageEntity(familyId, externalMessage, scope))
				.toList();
		} catch (final Exception e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "%s occurred when parsing open-e messages for familyId %s. Message is: %s".formatted(e.getClass().getSimpleName(), familyId, e.getMessage()));
		}
	}

	private static MessageEntity toMessageEntity(final String familyId, final ExternalMessage externalMessage, final Scope scope) {
		final var builder = MessageEntity.builder()
			.withFamilyId(familyId)
			.withScope(scope)
			.withDirection(externalMessage.isPostedByManager() ? Direction.OUTBOUND : Direction.INBOUND)
			.withMessageId(String.valueOf(externalMessage.getMessageID()))
			.withExternalCaseId(String.valueOf(externalMessage.getFlowInstanceID()))
			.withMessage(externalMessage.getMessage())
			.withSent(LocalDateTime.parse(externalMessage.getAdded(), formatter));

		ofNullable(externalMessage.getPoster()).ifPresent(poster -> builder
			.withEmail(poster.getEmail())
			.withFirstName(poster.getFirstname())
			.withLastName(poster.getLastname())
			.withUsername(poster.getUsername())
			.withUserId(String.valueOf(poster.getUserID())));

		final var entity = builder.build();

		ofNullable(externalMessage.getAttachments()).ifPresent(attachments -> entity
			.setAttachments(attachments.stream()
				.map(attachment -> MessageAttachmentEntity.builder()
					.withAttachmentId(attachment.getAttachmentID())
					.withMessage(entity)
					.withName(attachment.getFileName())
					.withMimeType(getMimeType(attachment.getFileName()))
					.withExtension(getFileExtension(attachment.getFileName()))
					.build())
				.toList()));

		return entity;
	}

	private static String getMimeType(final String file) {
		try {
			final String encodedFile = URLEncoder.encode(file, StandardCharsets.ISO_8859_1);
			return Files.probeContentType(Paths.get(new URI("file:///" + encodedFile)));
		} catch (final IOException | URISyntaxException e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "Unable to determine mime type for file %s".formatted(file));
		}

	}

	private static String getFileExtension(final String file) {
		final int dotIndex = file.lastIndexOf('.');
		return (dotIndex == -1) ? "" : file.substring(dotIndex + 1);
	}


}
