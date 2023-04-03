package se.sundsvall.webmessagecollector.service;

import java.util.List;

import org.springframework.stereotype.Component;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
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
            .withEmail(entity.getEmail())
            .withFirstName(entity.getFirstName())
            .withLastName(entity.getLastName())
            .withUsername(entity.getUsername())
            .withUserId(String.valueOf(entity.getUserId()))
            .build();
    }
    
    List<MessageDTO> toDTOs(List<MessageEntity> entities) {
        return entities.stream().map(this::toDTO).toList();
    }
}
