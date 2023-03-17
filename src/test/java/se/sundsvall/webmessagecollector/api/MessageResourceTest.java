package se.sundsvall.webmessagecollector.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.webmessagecollector.api.model.MessageDTO;
import se.sundsvall.webmessagecollector.api.model.PosterDTO;
import se.sundsvall.webmessagecollector.service.MessageService;

import wiremock.org.hamcrest.Matchers;

@ExtendWith(MockitoExtension.class)
class MessageResourceTest {
    
    @Mock
    MessageService service;
    
    @InjectMocks
    MessageResource resource;
    
    @Test
    void getMessages() {
        when(service.getMessages()).thenReturn(List.of(MessageDTO.builder()
                .withId("someId")
                .withMessageId("someMessageId")
                .withMessage("someMessage")
                .withExternalCaseId("someExternalCaseId")
                .withFamilyId("someFamilyId")
                .withPostedByManager(true)
                .withSent(LocalDateTime.now().toString())
                .withPoster(PosterDTO.builder()
                    .withLastName("someLastName")
                    .withFirstName("someFirstName")
                    .withUsername("someUsername")
                    .withEmail("someEmail")
                    .withUserId("someUserid")
                    .build())
            .build()));
        
        var result = resource.getMessages();
        
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
        var body = result.getBody().get(0);
        assertThat(body).hasNoNullFieldsOrProperties();
        
        verify(service, times(1)).getMessages();
        verifyNoMoreInteractions(service);
    }
    
    @Test
    void deleteMessages(){
        resource.deleteMessages(List.of("someId"));
        verify(service, times(1)).deleteMessages(anyList());
        verifyNoMoreInteractions(service);
    }
}