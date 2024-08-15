package apptest;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.webmessagecollector.Application;

@WireMockAppTestSuite(files = "classpath:/MessageIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class MessageIT extends AbstractAppTest {

	@Test
	void test1_getAllMessages() {
		setupCall()
			.withServicePath("/1984/messages/123/internal")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("expected.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_deleteMessages() {
		setupCall()
			.withServicePath("/1984/messages")
			.withHttpMethod(HttpMethod.DELETE)
			.withRequest(new Gson().toJson(List.of("1")))
			.withExpectedResponseStatus(HttpStatus.NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_getAttachment() throws IOException {
		setupCall()
			.withServicePath("/1984/messages/attachments/1")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(IMAGE_PNG_VALUE))
			.withExpectedBinaryResponse("test_image.png")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test4_deleteAttachment() {
		setupCall()
			.withServicePath("/1984/messages/attachments/1")
			.withHttpMethod(HttpMethod.DELETE)
			.withExpectedResponseStatus(HttpStatus.NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}
}
