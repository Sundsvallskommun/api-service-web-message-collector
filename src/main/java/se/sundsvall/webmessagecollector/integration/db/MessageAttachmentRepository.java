package se.sundsvall.webmessagecollector.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.webmessagecollector.integration.db.model.MessageAttachmentEntity;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachmentEntity, Integer> {

}
