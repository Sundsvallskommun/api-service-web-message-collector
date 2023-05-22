package se.sundsvall.webmessagecollector.service;


import java.util.List;

import org.springframework.stereotype.Service;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.integration.db.MessageRepository;

@Service
public class MessageService {
    
    private final MessageRepository repository;
    private final MessageMapper mapper;
    
    public MessageService(MessageRepository repository, MessageMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    public List<MessageDTO> getMessages() {
        return mapper.toDTOs(repository.findAll());
    }
    
    public void deleteMessages(List<Integer> ids) {
        repository.deleteAllById(ids);
    }
}