package se.sundsvall.webmessagecollector.integration.opene;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.model.Messages;

@Component
class OpenEMapper {

	private static final Logger LOG = LoggerFactory.getLogger(OpenEMapper.class);

	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
		.appendPattern("yyyy-MM-dd")
		.optionalStart()
		.appendPattern(" HH:mm")
		.optionalEnd()
		.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
		.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		.toFormatter();

	List<MessageEntity> mapMessages(final byte[] errands, final String familyId) {

		if (errands == null) {
			return emptyList();
		}

		final var xmlString = new String(errands, StandardCharsets.ISO_8859_1);
		try {
			return ofNullable(new XmlMapper()
				.readValue(xmlString, Messages.class)
				.getExternalMessages())
				.orElse(emptyList())
				.stream()
				.filter(externalMessage -> !externalMessage.isPostedByManager())
				.map(externalMessage -> {
					final var entity = MessageEntity.builder()
						.withFamilyId(familyId)
						.withDirection(externalMessage.isPostedByManager() ? Direction.OUTBOUND : Direction.INBOUND)
						.withMessageId(String.valueOf(externalMessage.getMessageID()))
						.withExternalCaseId(String.valueOf(externalMessage.getFlowInstanceID()))
						.withMessage(externalMessage.getMessage())
							.withSent(LocalDateTime.parse(externalMessage.getAdded(), FORMATTER));
					if (externalMessage.getPoster() != null) {
						entity.withEmail(externalMessage.getPoster().getEmail())
							.withFirstName(externalMessage.getPoster().getFirstname())
							.withLastName(externalMessage.getPoster().getLastname())
							.withUsername(externalMessage.getPoster().getUsername())
							.withUserId(String.valueOf(externalMessage.getPoster().getUserID()));
					}
					return entity.build();
				})
				.toList();
		} catch (final JsonProcessingException e) {
			LOG.info("Something went wrong parsing messages", e);
			return emptyList();
		}
	}
}