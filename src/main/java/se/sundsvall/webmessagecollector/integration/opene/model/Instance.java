package se.sundsvall.webmessagecollector.integration.opene.model;

import java.util.Optional;

public enum Instance {
	INTERNAL, EXTERNAL;

	public static Instance fromString(final String instance) {
		return Optional.ofNullable(instance)
			.map(nonNullInstance -> {
				try {
					return Instance.valueOf(nonNullInstance.toUpperCase());
				} catch (final IllegalArgumentException e) {
					return null;
				}
			})
			.orElse(null);
	}
}
