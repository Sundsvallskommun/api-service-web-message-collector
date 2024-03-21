package apptest;

import java.util.List;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

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
			.withServicePath("/messages?familyid=123")
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
			.withExpectedResponseStatus(HttpStatus.NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_getAttachment() {
		setupCall()
			.withServicePath("/messages/attachments/1")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("SGVsbG8gV29ybGQ=")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test4_deleteAttachment() {
		setupCall()
			.withServicePath("/messages/attachments/1")
			.withHttpMethod(HttpMethod.DELETE)
			.withExpectedResponseStatus(HttpStatus.NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

}
