package se.sundsvall.webmessagecollector.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.scheduling.annotation.Scheduled;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

class ShedlockUsageTest {

	@Test
	void verifySchedlockAnnotationIsPresentOnScheduledMethods() {
		final var scanner = new ClassPathScanningCandidateComponentProvider(true);
		scanner.findCandidateComponents("se.sundsvall.webmessagecollector").stream()
			.map(this::getMethodsAnnotatedWith)
			.flatMap(m -> m.entrySet().stream())
			.forEach(this::verifyMandatoryAnnotations);
	}

	private void verifyMandatoryAnnotations(Entry<String, List<Method>> entrySet) {
		entrySet.getValue().forEach(method -> {
			assertThat(method.isAnnotationPresent(SchedulerLock.class))
				.withFailMessage(() -> "Method %s in class %s has @Scheduled annotation but no @SchedulerLock annotation".formatted(method.getName(), entrySet.getKey()))
				.isTrue();
		});
	}

	private Map<String, List<Method>> getMethodsAnnotatedWith(final BeanDefinition candidate) {
		try {
			final List<Method> methods = new ArrayList<>();
			var klazz = Class.forName(candidate.getBeanClassName());
			while (klazz != Object.class) {
				// need to traverse a type hierarchy in order to process methods from super types iterate though the list of methods
				// declared in the class represented by klass variable, and add those annotated with the specified annotation
				for (final Method method : klazz.getDeclaredMethods()) {
					if (method.isAnnotationPresent(Scheduled.class)) {
						methods.add(method);
					}
				}
				// move to the upper class in the hierarchy in search for more methods
				klazz = klazz.getSuperclass();
			}
			return Map.of(candidate.getBeanClassName(), methods);
		} catch (ClassNotFoundException e) {
			fail("Couldn't traverse class methods as class %s could not be found".formatted(candidate.getBeanClassName()));
			return Collections.emptyMap();
		}
	}
}
