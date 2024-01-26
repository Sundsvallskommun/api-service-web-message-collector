package se.sundsvall.webmessagecollector.service;

import static se.sundsvall.webmessagecollector.service.MessageMapper.toDTOs;

import java.util.List;

import org.springframework.stereotype.Service;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;

@Service
public class MessageService {
    
    private final MessageRepository repository;
    
	public MessageService(MessageRepository repository) {
        this.repository = repository;
    }
    
    public List<MessageDTO> getMessages() {
		return toDTOs(repository.findAll());
    }
    
    public void deleteMessages(List<Integer> ids) {
        repository.deleteAllById(ids);
    }
}