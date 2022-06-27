package apbiot.core.command.primary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.informations.CommandGatewayComponentInformations;
import apbiot.core.command.informations.CommandGatewaySlashInformations;
import apbiot.core.objects.enums.CommandCategory;
import apbiot.core.objects.interfaces.IRunnableMethod;
import discord4j.discordjson.json.ApplicationCommandOptionData;

public class ShutdownCommandPrimary extends SlashCommandInstance {

	private final IRunnableMethod shutdownMethod;
	
	public ShutdownCommandPrimary(IRunnableMethod shutdownMethod) {
		super(Arrays.asList("shutdown"), "Eteint le bot.", CommandCategory.ADMIN);
		
		this.shutdownMethod = shutdownMethod;
	}
	
	@Override
	public void execute(CommandGatewaySlashInformations infos) {
		
		infos.getEvent().deferReply().block();
		infos.getEvent().getInteractionResponse().createFollowupMessage("👋 Extinction du bot ! Au revoir.").block();
		
		this.shutdownMethod.run();
	}
	
	@Override
	public void executeComponent(CommandGatewayComponentInformations infos) { }
	
	@Override
	public List<ApplicationCommandOptionData> getCommandArguments(ArrayList<ApplicationCommandOptionData> args) {
		return args;
	}
	
	@Override
	protected CommandPermission setPermissions() {
		CommandPermission perm = new CommandPermission();
		perm.setDeveloperCommand(true);
		return perm;
	}

	@Override
	public boolean isServerOnly() {
		return false;
	}

}
