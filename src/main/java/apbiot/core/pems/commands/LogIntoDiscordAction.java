package apbiot.core.pems.commands;

import java.util.Optional;

import apbiot.core.pems.Action;

public record LogIntoDiscordAction(String clientToken) implements Action<Void> {

	public Optional<String> optionalClientToken() {
		return Optional.ofNullable(clientToken);
	}
}
