package se.sundsvall.webmessagecollector.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.webmessagecollector.integration.db.model.Instance;
import se.sundsvall.webmessagecollector.integration.db.model.MessageStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class MessageRepositoryTest {

	private static final String MUNICIPALITY_ID = "1984";
	private static final String FAMILY_ID = "200";

	@Autowired
	private MessageRepository repository;

	@Test
	void findAllByMunicipalityIdAndFamilyIdAndInstanceAndStatus() {
		var result = repository.findAllByMunicipalityIdAndFamilyIdAndInstanceAndStatus(
			MUNICIPALITY_ID,
			FAMILY_ID,
			Instance.INTERNAL,
			MessageStatus.COMPLETE);

		assertThat(result).hasSize(1).extracting("id").containsExactly(1);
	}

	@Test
	void existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId() {
		assertThat(repository.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(FAMILY_ID, Instance.INTERNAL, "1", "100")).isTrue();
		assertThat(repository.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId("something", Instance.INTERNAL, "1", "100")).isFalse();
		assertThat(repository.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(FAMILY_ID, Instance.EXTERNAL, "1", "100")).isFalse();
		assertThat(repository.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(FAMILY_ID, Instance.INTERNAL, "123", "100")).isFalse();
		assertThat(repository.existsByFamilyIdAndInstanceAndMessageIdAndExternalCaseId(FAMILY_ID, Instance.INTERNAL, "1", "123")).isFalse();
	}

	@Test
	void findAllByStatusAndMunicipalityId() {
		var result = repository.findAllByStatusInAndMunicipalityId(List.of(MessageStatus.COMPLETE, MessageStatus.PROCESSING), MUNICIPALITY_ID);
		assertThat(result).hasSize(4).extracting("id").containsExactly(1, 3, 4, 5);
	}

	@Test
	void deleteByStatusAndMunicipalityIdAndFamilyIdAndStatusTimestampIsBefore() {
		repository.deleteByStatusAndMunicipalityIdAndFamilyIdAndStatusTimestampIsBefore(
			MessageStatus.COMPLETE,
			MUNICIPALITY_ID,
			FAMILY_ID,
			LocalDateTime.of(2024, 1, 1, 15, 0, 0));
		// None deleted
		assertThat(repository.findAll()).extracting("id").containsExactly(1, 2, 3, 4, 5);

		repository.deleteByStatusAndMunicipalityIdAndFamilyIdAndStatusTimestampIsBefore(
			MessageStatus.COMPLETE,
			MUNICIPALITY_ID,
			FAMILY_ID,
			LocalDateTime.of(2024, 1, 1, 15, 0, 1));
		// Only 1 and 4 deleted
		assertThat(repository.findAll()).extracting("id").containsExactly(2, 3, 5);
	}
}
