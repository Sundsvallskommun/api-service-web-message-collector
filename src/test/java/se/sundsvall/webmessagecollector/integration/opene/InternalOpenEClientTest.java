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
class InternalOpenEClientTest {

	@Test
	void verifyAnnotations() {
		// Assert
		assertThat(InternalOpenEClient.class).hasAnnotation(FeignClient.class);
		assertThat(InternalOpenEClient.class.getAnnotation(FeignClient.class).url()).isEqualTo("${integration.open-e.internal.url}");
		assertThat(fetchMethod("getMessages").getAnnotation(GetMapping.class).path()).containsExactly("/api/messageapi/getmessages/family/{familyId}");
	}

	private Method fetchMethod(final String methodName) {
		return Stream.of(InternalOpenEClient.class.getMethods())
			.filter(m -> methodName.equals(m.getName()))
			.findFirst()
			.orElseThrow();
	}

}
