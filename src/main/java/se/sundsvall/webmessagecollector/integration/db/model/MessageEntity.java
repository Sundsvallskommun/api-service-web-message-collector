package se.sundsvall.webmessagecollector.integration.db.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Builder(setterPrefix = "with")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {
    
    @Id
    private String id;
    private boolean postedByManager;
    private String familyId;
    private String externalCaseId;
    private String message;
    private String messageId;
    private LocalDateTime sent;
    @ManyToOne
    private PosterEntity posterEntity;
}
