package se.sundsvall.webmessagecollector.integration.opene.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.EXTERNAL;
import static se.sundsvall.webmessagecollector.integration.opene.model.Instance.INTERNAL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class InstanceTest {

	@Test
	void enums() {
		assertThat(Instance.values()).containsExactlyInAnyOrder(INTERNAL, EXTERNAL);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"INTERNAL", "EXTERNAL", "internal", "external"
	})
	void fromString(final String instance) {
		assertThat(Instance.fromString(instance)).isNotNull();

	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {
		" "
	})
	void fromStringNull(final String instance) {
		assertThat(Instance.fromString(instance)).isNull();

	}

}
