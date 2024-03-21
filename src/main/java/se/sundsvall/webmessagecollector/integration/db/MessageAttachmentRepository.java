package se.sundsvall.webmessagecollector.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "messageAttachmentRepository")
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachmentEntity, Integer> {

}
