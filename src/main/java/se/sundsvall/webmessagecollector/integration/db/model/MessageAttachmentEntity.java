package se.sundsvall.webmessagecollector.integration.db.model;

import java.sql.Blob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_attachment")
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageAttachmentEntity {

	@Id
	private Integer attachmentId;

	private String name;

	private String extension;

	private String mimeType;

	@Lob
	@Column(columnDefinition = "longblob")
	private Blob file;

	@ManyToOne
	@JoinColumn(name = "message_id", foreignKey = @ForeignKey(name = "FK_message_attachment_message_id"))
	private MessageEntity message;

}
