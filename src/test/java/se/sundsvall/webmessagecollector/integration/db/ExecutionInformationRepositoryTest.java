package se.sundsvall.webmessagecollector.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-junit.sql"
})
class ExecutionInformationRepositoryTest {

	private static final String MUNICIPALITY_ID = "1984";

	@Autowired
	private ExecutionInformationRepository repository;

	@Test
	void findByMunicipalityId() {
		assertThat(repository.findByMunicipalityId(MUNICIPALITY_ID))
			.hasSize(2)
			.extracting("familyId")
			.containsExactly("1", "2");
	}
}
