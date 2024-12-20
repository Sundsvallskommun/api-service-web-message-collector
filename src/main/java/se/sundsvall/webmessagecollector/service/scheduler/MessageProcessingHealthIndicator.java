package se.sundsvall.webmessagecollector.service.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessingHealthIndicator implements HealthIndicator {
	private final AtomicBoolean healthy = new AtomicBoolean(true);
	private final AtomicBoolean errors = new AtomicBoolean(false);
	private String reason;

	@Override
	public Health health() {
		if (healthy.get()) {
			return Health.up().build();
		} else {
			return Health.status("RESTRICTED").withDetail("Reason", reason == null ? "Unknown" : reason).build();
		}
	}

	public void setUnhealthy() {
		healthy.set(false);
		errors.set(true);
	}

	public void setUnhealthy(String reason) {
		setUnhealthy();
		this.reason = reason;
	}

	public void setHealthy() {
		healthy.set(true);
		errors.set(false);
		this.reason = null;
	}

	public void resetErrors() {
		errors.set(false);
	}

	public boolean hasErrors() {
		return errors.get();
	}
}
