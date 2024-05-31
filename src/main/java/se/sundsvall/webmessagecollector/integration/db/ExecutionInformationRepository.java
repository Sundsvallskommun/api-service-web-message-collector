package se.sundsvall.webmessagecollector.integration.db;

import org.springframework.data.jpa.repository.JpaRepository;

import se.sundsvall.webmessagecollector.integration.db.model.ExecutionInformationEntity;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "executionInformationRepository")
public interface ExecutionInformationRepository extends JpaRepository<ExecutionInformationEntity, String> {

}
