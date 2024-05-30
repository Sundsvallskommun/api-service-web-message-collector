package se.sundsvall.webmessagecollector.integration.opene.model;

import java.util.Optional;

public enum Scope {
	INTERNAL, EXTERNAL;

	public static Scope fromString(final String scope) {
		return Optional.ofNullable(scope)
			.map(nonNullScope -> {
				try {
					return Scope.valueOf(nonNullScope.toUpperCase());
				} catch (final IllegalArgumentException e) {
					return null;
				}
			})
			.orElse(null);
	}
}
