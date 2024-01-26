package apptest;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import com.google.gson.Gson;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.webmessagecollector.Application;

@WireMockAppTestSuite(files = "classpath:/MessagingIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class MessagingIT extends AbstractAppTest {

	@Test
	void test1_getAllMessages() {
		setupCall()
			.withServicePath("/messages")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("expected.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_deleteMessages() {
		setupCall()
			.withServicePath("/messages")
			.withHttpMethod(HttpMethod.DELETE)
			.withRequest(new Gson().toJson(List.of("1")))
			.withExpectedResponseStatus(HttpStatus.OK)
			.sendRequestAndVerifyResponse();
	}
}
