package se.sundsvall.webmessagecollector.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;

@CircuitBreaker(name = "messageRepository")
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

	List<MessageEntity> findAllByMunicipalityIdAndFamilyIdAndInstanceAndStatus(String municipalityId, String familyId, Instance instance, MessageStatus status);

	boolean existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(String familyId, Instance instance, String messageId, String externalCaseId);

	List<MessageEntity> findAllByStatusInAndMunicipalityId(List<MessageStatus> statuses, String municipalityId);

	void deleteByStatusAndMunicipalityIdAndFamilyIdAndStatusTimestampIsBefore(MessageStatus status, String municipalityId, String familyId, LocalDateTime lastSuccessMinusKeep);
}
