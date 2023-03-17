package se.sundsvall.webmessagecollector.integration.opene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class OpenEIntegrationTest {
    
    @Spy
    OpenEMapper mapper;
    
    @Mock
    OpenEClient client;
    
    @InjectMocks
    OpenEIntegration integration;
    
    @Test
    void getMessages() throws IOException {
        
        
        var bytes = """
            <?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
            <Messages>
                <ExternalMessage>
                    <postedByManager>true</postedByManager>
                    <systemMessage>false</systemMessage>
                    <readReceiptEnabled>false</readReceiptEnabled>
                    <messageID>1</messageID>
                    <message>Message</message>
                    <poster>
                        <userID>1</userID>
                        <username>userName</username>
                        <firstname>firstName</firstname>
                        <lastname>lastName</lastname>
                        <email>email@test.se</email>
                        <admin>false</admin>
                        <enabled>true</enabled>
                        <lastLogin>2023-02-03 11:10</lastLogin>
                        <lastLoginInMilliseconds>1675419056000</lastLoginInMilliseconds>
                        <added>2016-09-16 01:03</added>
                        <isMutable>true</isMutable>
                        <hasFormProvider>true</hasFormProvider>
                    </poster>
                    <added>2022-05-25 11:20</added>
                    <flowInstanceID>102251</flowInstanceID>
                </ExternalMessage>
            </Messages>
            """.getBytes(StandardCharsets.ISO_8859_1);
        
        
        when(client.getMessages(any(String.class), any(String.class), any(String.class))).thenReturn(bytes);
        
        var result = integration.getMessages("", "", "");
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isNotZero();
        assertThat(result.get(0)).hasNoNullFieldsOrPropertiesExcept("id");
        
        verify(client, times(1))
            .getMessages(any(String.class), any(String.class), any(String.class));
        
        verify(mapper, times(1))
            .mapMessages(any(byte[].class), any(String.class));
    }
    
    @Test
    void getMessages_OpenEReturnsNull() throws IOException {
        
        when(client.getMessages(any(String.class), any(String.class), any(String.class))).thenReturn(null);
        
        var result = integration.getMessages("", "", "");
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
        
        verify(client, times(1))
            .getMessages(any(String.class), any(String.class), any(String.class));
        
        verify(mapper, times(1))
            .mapMessages(any(), any(String.class));
    }
    
    @Test
    void getMessages_throwsException() throws IOException {
        
        when(client.getMessages(any(String.class), any(String.class), any(String.class)))
            .thenThrow(new IOException());
        
        
        var result = integration.getMessages("", "", "");
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
        
        verify(client, times(1))
            .getMessages(any(String.class), any(String.class), any(String.class));
        
        verifyNoInteractions(mapper);
        
    }
}