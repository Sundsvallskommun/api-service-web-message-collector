package se.sundsvall.webmessagecollector.service;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Objects;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

class MessageMapper {
	private MessageMapper() {}
    
	static MessageDTO toDTO(MessageEntity entity) {
		return ofNullable(entity)
			.map(e -> MessageDTO.builder()
				.withId(e.getId())
				.withFamilyId(e.getFamilyId())
				.withDirection(e.getDirection())
				.withMessageId(e.getMessageId())
				.withExternalCaseId(e.getExternalCaseId())
				.withMessage(e.getMessage())
				.withSent(String.valueOf(e.getSent()))
				.withEmail(e.getEmail())
				.withFirstName(e.getFirstName())
				.withLastName(e.getLastName())
				.withUsername(e.getUsername())
				.withUserId(e.getUserId())
				.build())
			.orElse(null);
    }
    
	static List<MessageDTO> toDTOs(List<MessageEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(MessageMapper::toDTO)
			.filter(Objects::nonNull)
			.toList();
    }
}
