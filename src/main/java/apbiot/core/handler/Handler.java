package apbiot.core.objects.interfaces;

import discord4j.core.GatewayDiscordClient;

public interface IHandler {
	
	void preRegister();
	void register(GatewayDiscordClient gateway);
	void postRegister();
}
