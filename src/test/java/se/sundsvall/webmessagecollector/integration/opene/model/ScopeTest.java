package se.sundsvall.webmessagecollector.integration.opene.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static se.sundsvall.webmessagecollector.integration.opene.model.Scope.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Scope.INTERNAL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ScopeTest {


	@Test
	void enums() {
		assertThat(Scope.values()).containsExactlyInAnyOrder(INTERNAL, EXTERNAL);
	}

	@ParameterizedTest
	@ValueSource(strings = {"INTERNAL", "EXTERNAL", "internal", "external"})
	void fromString(final String scope) {
		assertThat(Scope.fromString(scope)).isNotNull();

	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" "})
	void fromStringNull(final String scope) {
		assertThat(Scope.fromString(scope)).isNull();

	}

}
