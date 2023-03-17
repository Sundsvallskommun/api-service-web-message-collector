package se.sundsvall.webmessagecollector.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class MessageDTO {
    @Schema(description = "The webMessageCollector Id for the message", example = "1")
    private String id;
    @Schema(description = "If the message was utbound from a manager or inbound from a customer ", example = "true")
    private boolean postedByManager;
    @Schema(description = "What E-service the message was found in", example = "501")
    private String familyId;
    @Schema(description = "The external caseID ", example = "caa230c6-abb4-4592-ad9a-34e263c2787b")
    private String externalCaseId;
    @Schema(description = "The message ", example = "Hello World")
    private String message;
    @Schema(description = "The unique messageId from openE for the message", example = "12")
    private String messageId;
    @Schema(description = "Time and date the message was sent ", example = "2023-02-23 17:26:23")
    private String sent;
    @Schema(description = "The poster of the message ")
    private PosterDTO poster;
}
