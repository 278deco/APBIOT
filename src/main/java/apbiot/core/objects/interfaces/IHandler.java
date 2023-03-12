package apbiot.core.objects.interfaces;

import discord4j.core.GatewayDiscordClient;

public interface IHandler {
	void build();
	void register(GatewayDiscordClient gateway);
}
