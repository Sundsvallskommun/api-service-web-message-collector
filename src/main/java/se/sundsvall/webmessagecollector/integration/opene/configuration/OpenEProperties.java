package se.sundsvall.webmessagecollector.integration.opene.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "integration.open-e")
public record OpenEProperties(Map<String, OpenEEnvironment> environments) {

	public record OpenEEnvironment(

		@Valid @NotNull SchedulerProperties scheduler,

		@Valid OpenEInstance internal,

		@Valid OpenEInstance external) {

		public record SchedulerProperties(

			@DefaultValue("true") boolean enabled,

			@NotBlank String cron,

			@DefaultValue("PT2M") Duration lockAtMostFor,

			@DefaultValue("PT5M") Duration clockSkew,

			@DefaultValue("PT3H") Duration keepDeletedAfterLastSuccessFor) {
		}

		public record OpenEInstance(

			@NotBlank String baseUrl,

			@NotBlank String username,

			@NotBlank String password,

			List<@NotBlank String> familyIds,

			@DefaultValue("5") int connectTimeout,

			@DefaultValue("60") int readTimeout) {
		}
	}
}
