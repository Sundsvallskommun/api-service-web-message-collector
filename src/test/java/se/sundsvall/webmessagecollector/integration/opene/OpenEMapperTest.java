package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import se.sundsvall.webmessagecollector.api.model.Direction;

class OpenEMapperTest {

	@Test
	void mapMessages() {

		// Arrange
		final var externalMessage = """
			<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
			<Messages>
			    <ExternalMessage>
			        <postedByManager>false</postedByManager>
			        <systemMessage>false</systemMessage>
			        <readReceiptEnabled>false</readReceiptEnabled>
			        <attachments>
			            <ExternalMessageAttachment>
			                <attachmentID>73</attachmentID>
			                <filename>Test G Test - 2421.pdf</filename>
			                <size>126438</size>
			                <added>2022-09-15 11:16</added>
			                <FormatedSize>123 KB</FormatedSize>
			            </ExternalMessageAttachment>
			        </attachments>
			        <messageID>190</messageID>
			        <message>test</message>
			        <poster>
			            <userID>1337</userID>
			            <username>te69st</username>
			            <firstname>Test</firstname>
			            <lastname>Testorsson</lastname>
			            <email>Test.Testorsson@sundsvall.se</email>
			            <admin>false</admin>
			            <enabled>true</enabled>
			            <lastLogin>2024-02-22 13:35</lastLogin>
			            <lastLoginInMilliseconds>1708605349000</lastLoginInMilliseconds>
			            <added>2015-07-03 01:02</added>
			            <isMutable>true</isMutable>
			            <hasFormProvider>true</hasFormProvider>
			        </poster>
			        <added>2022-09-15 11:16</added>
			        <flowInstanceID>2542</flowInstanceID>
			    </ExternalMessage>
			   </Messages>
			""";

		//Act
		final var result = OpenEMapper.mapMessages(externalMessage.getBytes(), "123");
		//Assert
		assertThat(result).isNotNull().hasSize(1);
		assertThat(result.getFirst()).hasNoNullFieldsOrPropertiesExcept("id");
		assertThat(result.getFirst().getDirection()).isEqualTo(Direction.INBOUND);
		assertThat(result.getFirst().getFamilyId()).isEqualTo("123");
		assertThat(result.getFirst().getExternalCaseId()).isEqualTo("2542");
		assertThat(result.getFirst().getMessage()).isEqualTo("test");
		assertThat(result.getFirst().getMessageId()).isEqualTo("190");
		assertThat(result.getFirst().getSent()).isEqualTo(LocalDateTime.parse("2022-09-15T11:16"));
		assertThat(result.getFirst().getUsername()).isEqualTo("te69st");
		assertThat(result.getFirst().getFirstName()).isEqualTo("Test");
		assertThat(result.getFirst().getLastName()).isEqualTo("Testorsson");
		assertThat(result.getFirst().getEmail()).isEqualTo("Test.Testorsson@sundsvall.se");
		assertThat(result.getFirst().getUserId()).isEqualTo("1337");
	}


	@Test
	void mapMessages_IsPostedByManager() {

		// Arrange
		final var externalMessage = """
			<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
			<Messages>
			    <ExternalMessage>
			        <postedByManager>true</postedByManager>
			        <systemMessage>false</systemMessage>
			        <readReceiptEnabled>false</readReceiptEnabled>
			        <attachments>
			            <ExternalMessageAttachment>
			                <attachmentID>73</attachmentID>
			                <filename>Test G Test - 2421.pdf</filename>
			                <size>126438</size>
			                <added>2022-09-15 11:16</added>
			                <FormatedSize>123 KB</FormatedSize>
			            </ExternalMessageAttachment>
			        </attachments>
			        <messageID>190</messageID>
			        <message>test</message>
			        <poster>
			            <userID>1337</userID>
			            <username>te69st</username>
			            <firstname>Test</firstname>
			            <lastname>Testorsson</lastname>
			            <email>Test.Testorsson@sundsvall.se</email>
			            <admin>false</admin>
			            <enabled>true</enabled>
			            <lastLogin>2024-02-22 13:35</lastLogin>
			            <lastLoginInMilliseconds>1708605349000</lastLoginInMilliseconds>
			            <added>2015-07-03 01:02</added>
			            <isMutable>true</isMutable>
			            <hasFormProvider>true</hasFormProvider>
			        </poster>
			        <added>2022-09-15 11:16</added>
			        <flowInstanceID>2542</flowInstanceID>
			    </ExternalMessage>
			   </Messages>
			""";

		//Act
		final var result = OpenEMapper.mapMessages(externalMessage.getBytes(), "123");
		//Assert
		assertThat(result).isNotNull().isEmpty();
	}


	@Test
	void mapMessages_messageIsNull() {
		final var result = OpenEMapper.mapMessages(null, "123");
		assertThat(result).isNotNull().isEmpty();
	}

}
