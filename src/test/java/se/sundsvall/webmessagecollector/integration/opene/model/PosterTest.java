package se.sundsvall.webmessagecollector.integration.opene.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class PosterTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(Poster.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanToString(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void gettersAndSetters() {
		var userID = 123;
		var username = "someUsername";
		var firstname = "someFirstname";
		var lastname = "someLastname";
		var email = "someEmail";

		var poster = new Poster();
		poster.setUserID(userID);
		poster.setUsername(username);
		poster.setFirstname(firstname);
		poster.setLastname(lastname);
		poster.setEmail(email);

		assertThat(poster.getUserID()).isEqualTo(userID);
		assertThat(poster.getUsername()).isEqualTo(username);
		assertThat(poster.getFirstname()).isEqualTo(firstname);
		assertThat(poster.getLastname()).isEqualTo(lastname);
		assertThat(poster.getEmail()).isEqualTo(email);
	}

	@Test
	void noDirtOnCreatedBean() {

		assertThat(new Poster()).hasAllNullFieldsOrPropertiesExcept("userID").satisfies(
			poster -> assertThat(poster.getUserID()).isZero());
	}
}
