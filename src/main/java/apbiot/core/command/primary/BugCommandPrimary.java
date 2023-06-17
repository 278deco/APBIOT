package apbiot.core.command.primary;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apbiot.core.builder.ColorBuilder;
import apbiot.core.builder.DateBuilder;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.informations.CommandGatewayComponentInformations;
import apbiot.core.command.informations.CommandGatewaySlashInformations;
import apbiot.core.objects.enums.CommandCategory;
import apbiot.core.permissions.CommandPermission;
import apbiot.core.utils.Emojis;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;

public class BugCommandPrimary extends SlashCommandInstance {
	
	private EmbedCreateSpec bugEmbed;
	private final String botUsername, botAvatarUrl;
	
	public BugCommandPrimary(User botAccount) {
		super(Arrays.asList("bug"), "Permet d'envoyer un message au développeur pour l'informer d'un bug.", CommandCategory.UTILITY);
		
		this.botUsername = botAccount.getUsername();
		this.botAvatarUrl = botAccount.getAvatarUrl();
	}
	
	@Override
	public List<ApplicationCommandOptionData> getCommandArguments(ArrayList<ApplicationCommandOptionData> args) {
		args.add(ApplicationCommandOptionData.builder()
				.name("bug")
				.description("Le bug que vous souhaitez signaler")
				.type(ApplicationCommandOption.Type.STRING.getValue())
				.maxLength(500)
				.required(true)
				.build());
		return args;
	}

	
	
	@Override
	public void buildCommand() {
		this.built = true;
		
		final EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
		
		builder.author(this.botUsername, null, this.botAvatarUrl);
		builder.title("Signalement de bug / Question");
		builder.color(ColorBuilder.randomColor().get());
		builder.footer(this.botUsername+" (278deco) "+new DateBuilder(ZoneId.of("Europe/Paris")).getYear()+" © | Tout droits réservés", null);
		
		this.bugEmbed = builder.build();
	}
	
	@Override
	public void execute(CommandGatewaySlashInformations infos) {
		infos.getEvent().deferReply().withEphemeral(true).block();
		
		if (infos.getCommandResult().getOption("bug").isPresent()) {
			final String strBug = infos.getCommandResult().getOption("bug")
					.flatMap(ApplicationCommandInteractionOption::getValue)
					.map(ApplicationCommandInteractionOptionValue::asString).get();
			
			if(strBug.length() > 500) {
				infos.getEvent().createFollowup("La taille de votre rapport de bug ne doit pas dépasser 500 caractères !").withEphemeral(true).block();
				
			}else {
				final EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
				embedBuilder.from(bugEmbed);
	
				embedBuilder.addField("Question / bug", strBug, true);
				embedBuilder.addField("Heure", new DateBuilder(ZoneId.of("Europe/Paris")).getFormattedTime(), true);
				embedBuilder.addField("Auteur", infos.getExecutor().getUsername()+ "\n ID : ``"+infos.getExecutor().getId().asString()+"``", false);
				
				boolean success = true;
				try {
					infos.getEvent().getClient().getApplicationInfo().block().getOwner().block().getPrivateChannel().block().createMessage(embedBuilder.build()).block();					
				}catch(Exception e) { success = false; }
				
				if(success) infos.getEvent().createFollowup(Emojis.WHITE_CHECK_MARK+" Votre message à bien été envoyé ! *(Tout abus de cette commande sera sanctionné)*").withEphemeral(true).block();
				else infos.getEvent().createFollowup(Emojis.TOOLS+" Une erreur est survenue, merci de réessayer ultérieurement !").withEphemeral(true).block();
				
			}
		}
	}
	
	@Override
	public void executeComponent(CommandGatewayComponentInformations infos) { }

	@Override
	protected CommandPermission setPermissions() {
		return CommandPermission.EMPTY;
	}

	@Override
	public boolean isServerOnly() {
		return false;
	}
}
