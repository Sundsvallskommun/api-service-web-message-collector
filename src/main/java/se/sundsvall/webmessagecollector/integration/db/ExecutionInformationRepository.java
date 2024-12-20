package se.sundsvall.webmessagecollector.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;

@CircuitBreaker(name = "executionInformationRepository")
public interface ExecutionInformationRepository extends JpaRepository<ExecutionInformationEntity, String> {
	List<ExecutionInformationEntity> findByMunicipalityId(String municipalityId);

}
