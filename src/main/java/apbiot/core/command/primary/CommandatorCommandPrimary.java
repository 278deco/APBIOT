 package apbiot.core.command.primary;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apbiot.core.builder.ColorBuilder;
import apbiot.core.builder.DateBuilder;
import apbiot.core.builder.TimedMessage;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.informations.CommandGatewayComponentInformations;
import apbiot.core.command.informations.CommandGatewayNativeInformations;
import apbiot.core.commandator.HelpDescription;
import apbiot.core.helper.ArgumentHelper;
import apbiot.core.helper.PermissionHelper;
import apbiot.core.objects.Argument;
import apbiot.core.objects.enums.ArgumentLevel;
import apbiot.core.objects.enums.ArgumentType;
import apbiot.core.objects.enums.CommandCategory;
import apbiot.core.permissions.CommandPermission;
import apbiot.core.utils.Emojis;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;

public class CommandatorCommandPrimary extends NativeCommandInstance {

	private EmbedCreateSpec cmdtorEmbed;
	private final long[] receivers;
	private final String botUsername, botAvatarUrl;
	
	public CommandatorCommandPrimary(long[] receivers, User botAccount) {
		super(Arrays.asList("commandator"), "Envoie un message au développeur pour améliorer Commandator", CommandCategory.UTILITY);
		
		this.receivers = receivers;
		
		this.botUsername = botAccount.getUsername();
		this.botAvatarUrl = botAccount.getAvatarUrl();
	}

	@Override
	public void buildCommand() {
		this.built = true;
		
		final EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
		
		builder.author(this.botUsername, null, this.botAvatarUrl);
		builder.title("Amélioration Commandator");
		builder.color(ColorBuilder.randomColor().get());
		builder.footer(this.botUsername+" (278deco) "+new DateBuilder(ZoneId.of("Europe/Paris")).getYear()+" © | Tout droits réservés", null);
		
		this.cmdtorEmbed = builder.build();
	}
	
	@Override
	public void execute(CommandGatewayNativeInformations infos) {
		if(PermissionHelper.isServerEnvironnment(infos.getChannel().getType())) infos.getEvent().getMessage().delete().block();
		
		if(infos.getArguments().size() == 0) {
			new TimedMessage(infos.getChannel().createMessage(
					ArgumentHelper.getStringHelpSyntaxe(getRequiredArguments(), getMainName(), infos.getUsedPrefix())).block()
			).setDelayedDelete(Duration.ofSeconds(7), true);
		}else {
			
			if(infos.getArguments().size() >= 3) {
				process(infos.getChannel(), infos.getEvent().getGuild().block(), infos.getArguments(), infos.getExecutor());
			}else {
				new TimedMessage(infos.getChannel().createMessage(
						ArgumentHelper.getStringHelpSyntaxe(getRequiredArguments(), getMainName(), infos.getUsedPrefix())).block()
				).setDelayedDelete(Duration.ofSeconds(7), true);
			}
		}
	}
	
	private void process(MessageChannel chan, Guild guild, List<String> cmdArgs, User user) {
		if(user == null) {
			new TimedMessage(chan.createMessage(Emojis.WARNING+" Une erreur est survenue, merci de réessayer dans quelques secondes...").block())
			.setDelayedDelete(Duration.ofSeconds(5), true);
		}else {

			final EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
			embedBuilder.from(cmdtorEmbed);
			
			embedBuilder.addField("Auteur", user.getUsername()+ "\n ID : ``"+user.getId().asString()+"``", true);
			embedBuilder.addField("Heure", new DateBuilder(ZoneId.of("Europe/Paris")).getFormattedTime(), true);
			embedBuilder.addField("Commande envoyée", cmdArgs.get(0), false);
			embedBuilder.addField("Commande indiquée", cmdArgs.get(1), false);
			embedBuilder.addField("Commande recherchée", cmdArgs.get(2), false);
			
			new TimedMessage(chan.createMessage(Emojis.WHITE_CHECK_MARK+" Votre rapport à bien été envoyé ! *(Tout abus de cette commande sera sanctionné)*").block())
			.setDelayedDelete(Duration.ofSeconds(7), true);
			
			for(Long id : receivers) {
				guild.getMemberById(Snowflake.of(id)).block().getPrivateChannel().block().createMessage(embedBuilder.build()).block();
			}
			
		}
	}
	
	@Override
	public void executeComponent(CommandGatewayComponentInformations infos) { }
	
	@Override
	public HelpDescription setHelpDescription() {
		return new HelpDescription(this);
	}

	@Override
	public List<Argument> setArguments(ArrayList<Argument> args) {
		args.add(new Argument("Commande envoyée", "Commande que l'utilisateur a envoyée en attente de réponse.", ArgumentLevel.REQUIRED, ArgumentType.TEXT));
		args.add(new Argument("Commande indiquée", "Commande que Commandator a interprété en fonction de la commande envoyée par l'utilisateur.", ArgumentLevel.REQUIRED, ArgumentType.TEXT));
		args.add(new Argument("Commande recherchée", "Commande que l'utilisateur recherchait (qui n'est pas la même que celle de Commandator).", ArgumentLevel.REQUIRED, ArgumentType.TEXT));
		return args;
	}

	@Override
	protected CommandPermission setPermissions() {
		return CommandPermission.EMPTY;
	}

	@Override
	public boolean isServerOnly() {
		return false;
	}

}
