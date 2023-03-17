package se.sundsvall.webmessagecollector.service;

import java.util.List;

import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.api.model.PosterDTO;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

@Component
class MessageMapper {
    
    
    MessageDTO toDTO(MessageEntity entity) {
        return MessageDTO.builder()
            .withId(entity.getId())
            .withFamilyId(entity.getFamilyId())
            .withPostedByManager(entity.isPostedByManager())
            .withMessageId(String.valueOf(entity.getMessageId()))
            .withExternalCaseId(String.valueOf(entity.getExternalCaseId()))
            .withMessage(entity.getMessage())
            .withSent(String.valueOf(entity.getSent()))
            .withPoster(PosterDTO.builder()
                .withEmail(entity.getPosterEntity().getEmail())
                .withFirstName(entity.getPosterEntity().getFirstName())
                .withLastName(entity.getPosterEntity().getLastName())
                .withUsername(entity.getPosterEntity().getUsername())
                .withUserId(String.valueOf(entity.getPosterEntity().getUserId()))
                .build())
            .build();
    }
    
    List<MessageDTO> toDTOs(List<MessageEntity> entities) {
        return entities.stream().map(this::toDTO).toList();
    }
}
