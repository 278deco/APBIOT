package apbiot.core.command.primary;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import apbiot.core.MainInitializer;
import apbiot.core.builder.ColorBuilder;
import apbiot.core.builder.DateBuilder;
import apbiot.core.builder.EmbedBuilder;
import apbiot.core.builder.TimedMessage;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.informations.CommandGatewayComponentInformations;
import apbiot.core.command.informations.CommandGatewayNativeInformations;
import apbiot.core.commandator.HelpDescription;
import apbiot.core.handler.EmojiRessources;
import apbiot.core.helper.CommandHelper;
import apbiot.core.helper.PermissionHelper;
import apbiot.core.objects.Argument;
import apbiot.core.objects.enums.ArgumentLevel;
import apbiot.core.objects.enums.ArgumentType;
import apbiot.core.objects.enums.CommandCategory;
import discord4j.common.util.Snowflake;
import discord4j.core.object.command.Interaction.Type;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.MessageCreateSpec;

public class HelpCommandPrimary extends NativeCommandInstance {
	
	private EmbedBuilder template;
	private final Snowflake ownerID;
	private final String botUsername, botAvatarUrl;
	
	private ActionRow buttonsRow;
	
	public HelpCommandPrimary(Snowflake ownerID, User botAccount) {
		super(Arrays.asList("help"), "Permet d'obtenir la liste des commandes éxécutables.", CommandCategory.UTILITY);
		
		this.ownerID = ownerID;
		
		this.botUsername = botAccount.getUsername();
		this.botAvatarUrl = botAccount.getAvatarUrl();	
	}
	
	@Override
	public void initCommand() {
		
		this.template = new EmbedBuilder();

		this.template.setAuthor(this.botUsername, null, this.botAvatarUrl);
		this.template.setTitle("Commande d'aide");
		this.template.setFooter(this.botUsername+" (278deco) "+new DateBuilder(ZoneId.of("Europe/Paris")).getYear()+" © | Tout droits réservés", null);
		this.template.setColor(new ColorBuilder().randomColor().get());
		
		Button funButton = Button.secondary(CommandHelper.generateComponentID(this, "fun_button"), "FUN");
		Button gameButton = Button.secondary(CommandHelper.generateComponentID(this, "game_button"), "GAME");
		Button adminButton = Button.secondary(CommandHelper.generateComponentID(this, "admin_button"), "ADMIN");
		Button musicButton = Button.secondary(CommandHelper.generateComponentID(this, "music_button"), "MUSIC");
		Button utilityButton = Button.secondary(CommandHelper.generateComponentID(this, "utility_button"), "UTILITY");
		
		buttonsRow = ActionRow.of(funButton, gameButton, adminButton, musicButton, utilityButton);
	}
	
	@Override
	public void execute(CommandGatewayNativeInformations infos) {
		infos.getEvent().getMessage().delete().block();
		
		new TimedMessage(
			infos.getChannel().createMessage(
				MessageCreateSpec.builder()
				.addComponent(this.buttonsRow)
				.content("🗒️ "+infos.getExecutor().getMention()+", merci de choisir la catégorie de commande à consulter :").build()
			).block()
		).setDelayedDelete(Duration.ofSeconds(15), true);
		
	}
	
	private boolean process(Member member, CommandCategory choosenCat) {
		final List<Field> fields = new ArrayList<>();
		
		for(Map.Entry<List<String>, NativeCommandInstance> entry : MainInitializer.getCommandHandler().NATIVE_COMMANDS.entrySet()) {
			if(entry.getValue().isInHelpListed() && entry.getValue().getCommandCategory() == choosenCat) {
				if(PermissionHelper.compareCommandPermissions(member, this, this.ownerID)) {
					fields.add(EmbedCreateFields.Field.of("• "+entry.getValue().getMainName()+" ➭", entry.getValue().getDescription(), false));
				}
			}
		}
		
		for(Map.Entry<List<String>, SlashCommandInstance> entry : MainInitializer.getCommandHandler().SLASH_COMMANDS.entrySet()) {
			if(entry.getValue().isInHelpListed() && entry.getValue().getCommandCategory() == choosenCat) {
				if(PermissionHelper.compareCommandPermissions(member, this, this.ownerID)) {
					fields.add(EmbedCreateFields.Field.of("• "+entry.getValue().getMainName()+" ➭", "	*Se référer au menu commandes slash* ", false));
				}
			}
		}
		
		if(fields.size() == 0) {
			return false;
		}else {
			
			final EmbedBuilder[] helpEmbeds = new EmbedBuilder[Math.floorDiv(fields.size(), 25)+1];
			
			int index = 0, arrayIndex = 0;
			while(index < fields.size() && arrayIndex <= helpEmbeds.length) {
				if(index%25 == 0) {
					arrayIndex+=1;
					helpEmbeds[arrayIndex-1] = new EmbedBuilder().copyLayout(this.template);
				}
				
				helpEmbeds[arrayIndex-1].addExistingField(fields.get(index));

				index+=1;
			}
			
			helpEmbeds[0].setDescription("**Liste des commandes accessibles pour "+member.getUsername()+" :**");
			
			final PrivateChannel memberPV = member.getPrivateChannel().block();
			
			for(EmbedBuilder eb : helpEmbeds) {
				memberPV.createMessage(eb.build()).block();
			}
			
			return true;
		}
	}
	
	@Override
	public void executeComponent(CommandGatewayComponentInformations infos) { 
		if(infos.getEvent().getInteraction().getType() == Type.MESSAGE_COMPONENT) {
			infos.getEvent().deferReply().withEphemeral(true).block();
			
			boolean success = false;
			
			if(infos.getComponentId().equals("fun_button")) {
				success = process(infos.getEvent().getInteraction().getMember().get(), CommandCategory.FUN);
			}else if(infos.getComponentId().equals("admin_button")) {
				success = process(infos.getEvent().getInteraction().getMember().get(), CommandCategory.ADMIN);
			}else if(infos.getComponentId().equals("music_button")) {
				success = process(infos.getEvent().getInteraction().getMember().get(), CommandCategory.MUSIC);
			}else if(infos.getComponentId().equals("utility_button")) {
				success = process(infos.getEvent().getInteraction().getMember().get(), CommandCategory.UTILITY);
			}else if(infos.getComponentId().equals("game_button")) {
				success = process(infos.getEvent().getInteraction().getMember().get(), CommandCategory.GAME);
			}
			
			if(success) {				
				infos.getEvent().getInteractionResponse().createFollowupMessageEphemeral(EmojiRessources.CHECKMARK+" La liste des commandes vous a bien été envoyée !").block();
			}else {				
				infos.getEvent().getInteractionResponse().createFollowupMessageEphemeral(EmojiRessources.WARNING+" Une erreur s'est produite, réessayer ultérieurement.").block();
			}
			
		}
	}
	
	@Override
	public HelpDescription setHelpDescription() {
		return new HelpDescription(this);
	}
	
	@Override
	public boolean isServerOnly() {
		return true;
	}

	@Override
	public List<Argument> setArguments(ArrayList<Argument> args) {
		args.add(new Argument("commande", "Utiliser pour obtenir des renseignements sur une commande.", ArgumentLevel.OPTIONNAL, ArgumentType.TEXT));
		
		return args;
	}

	@Override
	protected CommandPermission setPermissions() {
		return null;
	}


}
