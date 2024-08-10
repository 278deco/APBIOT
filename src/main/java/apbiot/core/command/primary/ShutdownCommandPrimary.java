package apbiot.core.command.primary;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.informations.GatewayApplicationCommandPacket;
import apbiot.core.command.informations.GatewayComponentCommandPacket;
import apbiot.core.objects.enums.CommandCategory;
import apbiot.core.permissions.CommandPermission;
import discord4j.discordjson.json.ApplicationCommandOptionData;

public class ShutdownCommandPrimary extends SlashCommandInstance {

	private final Runnable shutdownMethod;
	
	public ShutdownCommandPrimary(Runnable shutdownMethod) {
		super("shutdown", CommandCategory.ADMIN);
		
		this.shutdownMethod = shutdownMethod;
	}
	
	@Override
	public void execute(GatewayApplicationCommandPacket infos) {
		
		infos.getEvent().deferReply().block();
		infos.getEvent().getInteractionResponse().createFollowupMessage("👋 Extinction du bot ! Au revoir.").block();
		
		new Thread(this.shutdownMethod, "Shutting down Thread").start();
	}
	
	@Override
	public void executeComponent(GatewayComponentCommandPacket infos) { }
	
	@Override
	public List<ApplicationCommandOptionData> getCommandArguments(ArrayList<ApplicationCommandOptionData> args) {
		return args;
	}
	
	@Override
	protected CommandPermission setPermissions() {
		return CommandPermission.builder().setDevelopperCommand(true).build();
	}

	@Override
	public boolean isServerOnly() {
		return false;
	}

}
