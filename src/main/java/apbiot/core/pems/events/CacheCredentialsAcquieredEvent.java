package apbiot.core.pems.events;

import java.util.HexFormat;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import apbiot.core.pems.ProgramEvent;

public class CacheCredentialsAcquieredEvent extends ProgramEvent {

	public CacheCredentialsAcquieredEvent(Object[] arguments) {
		super(arguments);
	}

	public String getKeyAsString() {
		return getEventArgument(String.class, 0);
	}
	
	public Optional<SecretKey> getKey() {
		return Optional.ofNullable(getKeyAsString())
		        .map(HexFormat.of()::parseHex)
		        .map((keyStr) -> new SecretKeySpec(keyStr, "AES"));
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}
}
