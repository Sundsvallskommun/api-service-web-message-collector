package se.sundsvall.webmessagecollector.integration.opene;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.model.Messages;


@Component
class OpenEMapper {
    private static final Logger LOG = LoggerFactory.getLogger(OpenEMapper.class);
    
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd")
        .optionalStart()
        .appendPattern(" HH:mm")
        .optionalEnd()
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .toFormatter();
    
    List<MessageEntity> mapMessages(byte[] errands, String familyId) {
        
        if (errands == null) {
            return List.of();
        }
        
        var xmlString = new String(errands, StandardCharsets.ISO_8859_1);
        try {
            return new XmlMapper()
                .readValue(xmlString, Messages.class)
                .getExternalMessage().stream()
                .map(externalMessage -> {
                    var entity = MessageEntity.builder()
                        .withFamilyId(familyId)
                        .withDirection(externalMessage.isPostedByManager() ? Direction.OUTBOUND : Direction.INBOUND)
                        .withMessageId(String.valueOf(externalMessage.getMessageID()))
                        .withExternalCaseId(String.valueOf(externalMessage.getFlowInstanceID()))
                        .withMessage(externalMessage.getMessage())
                        .withSent(LocalDateTime.parse(externalMessage.getAdded(), formatter));
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
        } catch (JsonProcessingException e) {
            LOG.info("Something went wrong parsing messages", e);
            return List.of();
        }
    }
}
