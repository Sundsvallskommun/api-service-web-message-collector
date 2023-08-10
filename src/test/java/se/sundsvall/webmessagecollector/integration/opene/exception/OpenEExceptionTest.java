package se.sundsvall.webmessagecollector.integration.opene.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OpenEExceptionTest {

	@Test
	void testOpenEException() {
		var message = "message";
		assertThat(new OpenEException(message)).hasMessage(message);
	}
}
