package se.sundsvall.webmessagecollector.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import java.time.OffsetDateTime;
import java.util.Random;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ExecutionInformationEntityTest {
	
	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays((new Random().nextInt())), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		MatcherAssert.assertThat(ExecutionInformationEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanToString(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void testBuildPattern() {
		final var familyId = "123";
		final var lastSuccessfulExecution = OffsetDateTime.now();

		final var bean = ExecutionInformationEntity.builder()
			.withFamilyId(familyId)
			.withLastSuccessfulExecution(lastSuccessfulExecution)
			.build();

		assertThat(bean.getFamilyId()).isEqualTo(familyId);
		assertThat(bean.getLastSuccessfulExecution()).isEqualTo(lastSuccessfulExecution);
	}
}
