package se.sundsvall.webmessagecollector.integration.db.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message")
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private Direction direction;

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

	@Enumerated(EnumType.STRING)
	private Instance instance;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "message")
	private List<MessageAttachmentEntity> attachments;

}
