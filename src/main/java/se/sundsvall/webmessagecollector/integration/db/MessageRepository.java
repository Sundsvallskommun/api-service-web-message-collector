package se.sundsvall.webmessagecollector.integration.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "messageRepository")
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

	List<MessageEntity> findAllByMunicipalityIdAndFamilyIdAndInstance(String municipalityId, String familyId, Instance instance);

}
