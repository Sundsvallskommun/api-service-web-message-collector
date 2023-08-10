package se.sundsvall.webmessagecollector.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.service.MessageService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@RequestMapping("messages")
@Tag(name = "messages", description = "Messages")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {Problem.class, ConstraintViolationProblem.class})))
@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
public class MessageResource {
    
    private final MessageService service;
    
    public MessageResource(MessageService service) {
        this.service = service;
    }
    
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Successful Operation",
        content = @Content(schema = @Schema(implementation = MessageDTO.class)))
    @Operation(summary = "Get a list of message",
        description = "Returns a list of messages found")
    public ResponseEntity<List<MessageDTO>> getMessages() {
        return ResponseEntity.ok(service.getMessages());
    }
    
    @DeleteMapping
    @Operation(summary = "Delete a list of messages",
        description = "Deletes a list of messages with the ids provided")
    public ResponseEntity<Void> deleteMessages(@RequestBody List<Integer> ids) {
        service.deleteMessages(ids);
        return ResponseEntity.ok().build();
    }
    
}
