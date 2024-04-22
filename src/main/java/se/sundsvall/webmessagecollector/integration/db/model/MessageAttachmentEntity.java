package se.sundsvall.webmessagecollector.integration.db.model;

import java.sql.Blob;
import java.util.Objects;
import java.util.Optional;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "message_attachment")
@Getter
@Setter
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

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final MessageAttachmentEntity that = (MessageAttachmentEntity) o;
		return Objects.equals(attachmentId, that.attachmentId) && Objects.equals(name, that.name) && Objects.equals(extension, that.extension) && Objects.equals(mimeType, that.mimeType) && Objects.equals(file, that.file) && Objects.equals(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(attachmentId, name, extension, mimeType, file, message);
	}

	@Override
	public String toString() {
		final long messageId = Optional.ofNullable(message)
			.map(MessageEntity::getId)
			.orElse(0);

		return "MessageAttachmentEntity{" +
			"attachmentId=" + attachmentId +
			", name='" + name + '\'' +
			", extension='" + extension + '\'' +
			", mimeType='" + mimeType + '\'' +
			", file=" + file +
			", message.id=" + messageId +
			'}';
	}

}
