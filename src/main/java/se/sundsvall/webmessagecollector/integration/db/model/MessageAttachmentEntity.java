package se.sundsvall.webmessagecollector.integration.db.model;

import java.sql.Blob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

	private String fileName;

	@Lob
	@Column(columnDefinition = "longblob")
	private Blob file;

}
