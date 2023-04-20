package se.sundsvall.webmessagecollector.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
    
}
