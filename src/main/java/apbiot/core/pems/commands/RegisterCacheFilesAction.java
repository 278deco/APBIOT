package apbiot.core.pems.commands;

import java.util.HexFormat;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import apbiot.core.pems.Action;

public record RegisterCacheFilesAction(String encryptedCacheKey) implements Action<Void> {
	
	public Optional<SecretKey> encryptedCacheSecretKey() {
		return Optional.ofNullable(encryptedCacheKey)
		        .map(HexFormat.of()::parseHex)
		        .map((keyStr) -> new SecretKeySpec(keyStr, "AES"));
	}

}
