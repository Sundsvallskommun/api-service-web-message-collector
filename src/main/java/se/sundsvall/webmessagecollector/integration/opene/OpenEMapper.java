package se.sundsvall.webmessagecollector.integration.opene;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.model.ExternalMessage;
import se.sundsvall.webmessagecollector.integration.opene.model.Messages;

public final class OpenEMapper {

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

	public static List<MessageEntity> toMessageEntities(final byte[] errands, final String familyId) {

		if (errands == null) {
			return emptyList();
		}

		final var xmlString = new String(errands, StandardCharsets.ISO_8859_1);
		try {
			return ofNullable(new XmlMapper().readValue(xmlString, Messages.class).getExternalMessages())
				.orElse(emptyList())
				.stream()
				.filter(externalMessage -> !externalMessage.isPostedByManager())
				.map(externalMessage -> toMessageEntity(familyId, externalMessage))
				.toList();
		} catch (final Exception e) {
			throw Problem.valueOf(Status.INTERNAL_SERVER_ERROR, "%s occurred when parsing open-e messages for familyId %s. Message is: %s".formatted(e.getClass().getSimpleName(), familyId, e.getMessage()));
		}
	}

	private static MessageEntity toMessageEntity(final String familyId, ExternalMessage externalMessage) {
		final var entity = MessageEntity.builder()
			.withFamilyId(familyId)
			.withDirection(externalMessage.isPostedByManager() ? Direction.OUTBOUND : Direction.INBOUND)
			.withMessageId(String.valueOf(externalMessage.getMessageID()))
			.withExternalCaseId(String.valueOf(externalMessage.getFlowInstanceID()))
			.withMessage(externalMessage.getMessage())
			.withSent(LocalDateTime.parse(externalMessage.getAdded(), formatter));

		ofNullable(externalMessage.getPoster()).ifPresent(poster -> {
			entity.withEmail(poster.getEmail())
				.withFirstName(poster.getFirstname())
				.withLastName(poster.getLastname())
				.withUsername(poster.getUsername())
				.withUserId(String.valueOf(poster.getUserID()));
		});

		return entity.build();
	}
}
