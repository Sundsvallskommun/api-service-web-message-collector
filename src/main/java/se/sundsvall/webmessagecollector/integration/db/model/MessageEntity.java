package se.sundsvall.webmessagecollector.integration.db.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private boolean postedByManager;
    private String familyId;
    private String externalCaseId;
    private String message;
    private String messageId;
    private LocalDateTime sent;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String userId;
}
