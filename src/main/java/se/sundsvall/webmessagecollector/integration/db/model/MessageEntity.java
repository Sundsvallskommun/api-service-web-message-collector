package se.sundsvall.webmessagecollector.integration.db.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.opene.model.Instance;

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

	@Column(name = "municipality_id", length = 4, nullable = false)
	private String municipalityId;

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

	@Enumerated(EnumType.STRING)
	private MessageStatus status;

	@Column(name = "status_timestamp", nullable = false)
	private LocalDateTime statusTimestamp;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "message")
	private List<MessageAttachmentEntity> attachments;

}
