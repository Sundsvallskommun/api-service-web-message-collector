package apptest;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
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

	private static final String RESPONSE_FILE = "expected.json";

	@Test
	void test01_getAllMessages() {
		setupCall()
			.withServicePath("/1984/messages/123/internal")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_deleteMessages() {
		setupCall()
			.withServicePath("/1984/messages")
			.withHttpMethod(DELETE)
			.withRequest(new Gson().toJson(List.of("1")))
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getAttachment() throws IOException {
		setupCall()
			.withServicePath("/1984/messages/attachments/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(IMAGE_PNG_VALUE))
			.withExpectedBinaryResponse("test_image.png")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_deleteAttachment() {
		setupCall()
			.withServicePath("/1984/messages/attachments/1")
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_getMessagesByFlowInstanceId() {
		setupCall()
			.withServicePath("/1984/messages/external/flow-instances/12345")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_getAttachmentById() throws IOException {
		setupCall()
			.withServicePath("/1984/messages/external/attachments/12345")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(IMAGE_JPEG_VALUE))
			.withExpectedBinaryResponse("test_image.jpg")
			.sendRequestAndVerifyResponse();
	}
}
