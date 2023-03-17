package se.sundsvall.webmessagecollector.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class PosterDTO {
    @Schema(description = "Username for the poster", example = "te01st")
    private String username;
    @Schema(description = "Firstname of the poster ", example = "Test")
    private String firstName;
    @Schema(description = "If the message was utbound from a manager or inbound from a customer ", example = "Testorsson")
    private String lastName;
    @Schema(description = "Email for the poster", example = "test@sundsvall.se")
    private String email;
    @Schema(description = "If the message was utbound from a manager or inbound from a customer ", example = "177")
    private String userId;
}
