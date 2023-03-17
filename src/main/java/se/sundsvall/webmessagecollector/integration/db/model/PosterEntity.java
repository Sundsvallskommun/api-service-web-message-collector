package se.sundsvall.webmessagecollector.integration.db.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class PosterEntity {
    @Id
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String userId;
}
