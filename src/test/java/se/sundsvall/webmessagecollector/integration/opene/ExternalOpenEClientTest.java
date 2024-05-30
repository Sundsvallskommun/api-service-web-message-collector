package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@ExtendWith(MockitoExtension.class)
class ExternalOpenEClientTest {

	@Test
	void verifyAnnotations() {
		// Assert
		assertThat(ExternalOpenEClient.class).hasAnnotation(FeignClient.class);
		assertThat(ExternalOpenEClient.class.getAnnotation(FeignClient.class).url()).isEqualTo("${integration.open-e.external.url}");
		assertThat(fetchMethod("getMessages").getAnnotation(GetMapping.class).path()).containsExactly("/api/messageapi/getmessages/family/{familyId}");
	}

	private Method fetchMethod(final String methodName) {
		return Stream.of(ExternalOpenEClient.class.getMethods())
			.filter(m -> methodName.equals(m.getName()))
			.findFirst()
			.orElseThrow();
	}

}
