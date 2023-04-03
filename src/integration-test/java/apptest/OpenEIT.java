package apptest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.webmessagecollector.Application;

@WireMockAppTestSuite(
    files = "classpath:/MessagingIT/",
    classes = Application.class
)
@Testcontainers
public class OpenEIT extends AbstractAppTest {
    
    @Container
    public static MariaDBContainer<?> messageDb = new MariaDBContainer<>(DockerImageName.parse(
        "mariadb:10.9.2"))
        .withDatabaseName("webMessageCollector")
        .withUsername("root")
        .withPassword("")
        .withInitScript("webMessageCollector.sql");
    
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", messageDb::getJdbcUrl);
        registry.add("spring.datasource.username", messageDb::getUsername);
        registry.add("spring.datasource.password", messageDb::getPassword);
    }
    
    @Test
    void test1_getAllMessages() {
        assertThat(messageDb.isRunning()).isTrue();
        setupCall()
            .withServicePath("/messages")
            .withHttpMethod(HttpMethod.GET)
            .withExpectedResponseStatus(HttpStatus.OK)
            .withExpectedResponse("expected.json")
            .sendRequestAndVerifyResponse();
        
    }
    
    @Test
    void test2_deleteMessages() {
        assertThat(messageDb.isRunning()).isTrue();
        setupCall()
            .withServicePath("/messages")
            .withHttpMethod(HttpMethod.DELETE)
            .withRequest(new Gson().toJson(List.of("1")))
            .withExpectedResponseStatus(HttpStatus.OK)
            .sendRequestAndVerifyResponse();
        
    }
}
