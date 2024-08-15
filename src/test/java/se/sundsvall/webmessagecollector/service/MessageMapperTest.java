package se.sundsvall.webmessagecollector.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.webmessagecollector.api.model.Direction;
import se.sundsvall.webmessagecollector.integration.db.model.MessageEntity;

class MessageMapperTest {

	private static final Direction DIRECTION = Direction.INBOUND;

	private static final String MUNICIPALITY_ID = "municipalityId";

	private static final String EMAIL = "email";

	private static final String EXTERNAL_CASE_ID = "externalCaseId";

	private static final String FAMILY_ID = "familyId";

	private static final String FIRST_NAME = "firstName";

	private static final int ID = 558877;

	private static final String LAST_NAME = "lastName";

	private static final String MESSAGE = "message";

	private static final String MESSAGE_ID = "messageId";

	private static final LocalDateTime SENT = LocalDateTime.now();

	private static final String USER_ID = "userId";

	private static final String USERNAME = "username";

	@Test
	void toDTO() {
		// Act
		var bean = MessageMapper.toMessageDTO(createEntity());

		// Assert
		assertThat(bean.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(bean.getDirection()).isEqualTo(DIRECTION);
		assertThat(bean.getEmail()).isEqualTo(EMAIL);
		assertThat(bean.getExternalCaseId()).isEqualTo(EXTERNAL_CASE_ID);
		assertThat(bean.getFamilyId()).isEqualTo(FAMILY_ID);
		assertThat(bean.getFirstName()).isEqualTo(FIRST_NAME);
		assertThat(bean.getId()).isEqualTo(ID);
		assertThat(bean.getLastName()).isEqualTo(LAST_NAME);
		assertThat(bean.getMessage()).isEqualTo(MESSAGE);
		assertThat(bean.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(bean.getSent()).isEqualTo(SENT.toString());
		assertThat(bean.getUserId()).isEqualTo(USER_ID);
		assertThat(bean.getUsername()).isEqualTo(USERNAME);
	}

	@Test
	void toDTOFromNull() {
		assertThat(MessageMapper.toMessageDTO(null)).isNull();
	}

	@Test
	void toDTOs() {
		var arrayList = new ArrayList<>(List.of(createEntity(), createEntity()));
		arrayList.addFirst(null);

		var beans = MessageMapper.toMessageDTOs(arrayList);

		assertThat(beans).isNotNull().hasSize(2).allSatisfy(bean -> {
			assertThat(bean.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
			assertThat(bean.getDirection()).isEqualTo(DIRECTION);
			assertThat(bean.getEmail()).isEqualTo(EMAIL);
			assertThat(bean.getExternalCaseId()).isEqualTo(EXTERNAL_CASE_ID);
			assertThat(bean.getFamilyId()).isEqualTo(FAMILY_ID);
			assertThat(bean.getFirstName()).isEqualTo(FIRST_NAME);
			assertThat(bean.getId()).isEqualTo(ID);
			assertThat(bean.getLastName()).isEqualTo(LAST_NAME);
			assertThat(bean.getMessage()).isEqualTo(MESSAGE);
			assertThat(bean.getMessageId()).isEqualTo(MESSAGE_ID);
			assertThat(bean.getSent()).isEqualTo(SENT.toString());
			assertThat(bean.getUserId()).isEqualTo(USER_ID);
			assertThat(bean.getUsername()).isEqualTo(USERNAME);
		});
	}

	@Test
	void toDTOsFromNull() {
		assertThat(MessageMapper.toMessageDTOs(null)).isEmpty();
	}

	private MessageEntity createEntity() {
		return MessageEntity.builder()
			.withMunicipalityId(MUNICIPALITY_ID)
			.withDirection(DIRECTION)
			.withEmail(EMAIL)
			.withExternalCaseId(EXTERNAL_CASE_ID)
			.withFamilyId(FAMILY_ID)
			.withFirstName(FIRST_NAME)
			.withId(ID)
			.withLastName(LAST_NAME)
			.withMessage(MESSAGE)
			.withMessageId(MESSAGE_ID)
			.withSent(SENT)
			.withUserId(USER_ID)
			.withUsername(USERNAME)
			.build();
	}
}
