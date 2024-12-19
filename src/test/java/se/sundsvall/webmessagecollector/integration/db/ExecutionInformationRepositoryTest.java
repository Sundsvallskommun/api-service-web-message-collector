package se.sundsvall.webmessagecollector.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

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
