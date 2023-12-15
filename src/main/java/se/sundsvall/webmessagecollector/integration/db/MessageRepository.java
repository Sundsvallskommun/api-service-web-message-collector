package se.sundsvall.webmessagecollector.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

@CircuitBreaker(name = "messageRepository")
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

}
