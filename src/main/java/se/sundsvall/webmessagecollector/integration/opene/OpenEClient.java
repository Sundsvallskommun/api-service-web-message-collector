package se.sundsvall.webmessagecollector.integration.opene;

import static se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import se.sundsvall.webmessagecollector.integration.opene.configuration.OpenEConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.open-e.base-url}", configuration = OpenEConfiguration.class)
public interface OpenEClient {

	String TEXT_XML_CHARSET_ISO_8859_1 = "text/xml; charset=ISO-8859-1";

	@GetMapping(path = "/api/messageapi/getmessages/family/{familyId}", produces = TEXT_XML_CHARSET_ISO_8859_1)
	byte[] getMessages(@PathVariable(name = "familyId") final String familyId,
		@RequestParam(name = "fromDate") final String fromDate,
		@RequestParam(name = "toDate") final String toDate);

	@GetMapping(path = "/api/messageapi/getattachment/{attachmentId}", produces = TEXT_XML_CHARSET_ISO_8859_1)
	byte[] getAttachment(@PathVariable(name = "attachmentId") final int attachmentId);

}
