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
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;

public class BugCommandPrimary extends NativeCommandInstance {
	
	private EmbedCreateSpec bugEmbed;
	private final String botUsername, botAvatarUrl;
	
	public BugCommandPrimary(User botAccount) {
		super(Arrays.asList("bug"), "Permet d'envoyer un message au développeur pour l'informer d'un bug.", CommandCategory.UTILITY);
		
		this.botUsername = botAccount.getUsername();
		this.botAvatarUrl = botAccount.getAvatarUrl();
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
	public void execute(CommandGatewayNativeInformations infos) {
		String msg = infos.getMessageContent();
		
		if(PermissionHelper.isServerEnvironnment(infos.getChannel().getType())) infos.getEvent().getMessage().delete().block();
		
		if (infos.getArguments().size() == 0) {
			new TimedMessage(infos.getChannel().createMessage(
					ArgumentHelper.getStringHelpSyntaxe(getRequiredArguments(), getMainName(), infos.getUsedPrefix())).block())
			.setDelayedDelete(Duration.ofSeconds(5), true);
			return;
		}else if(msg.length() > 500) {
			new TimedMessage(infos.getChannel().createMessage(
					"⛔ Votre message dépasse la limite de 500 caractères !").block())
			.setDelayedDelete(Duration.ofSeconds(7), true);
			return;
		}else {
			
			final EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
			embedBuilder.from(bugEmbed);

			embedBuilder.addField("Question / bug", msg, true);
			embedBuilder.addField("Heure", new DateBuilder(ZoneId.of("Europe/Paris")).getFormattedTime(), true);
			embedBuilder.addField("Auteur", infos.getExecutor().getUsername()+ "\n ID : ``"+infos.getExecutor().getId().asString()+"``", false);
			
			
			new TimedMessage(infos.getChannel().createMessage(
					"🆗 Votre message à bien été envoyé ! *(Tout abus de cette commande sera sanctionné)*").block())
			.setDelayedDelete(Duration.ofSeconds(7), true);
			
			infos.getEvent().getClient().getApplicationInfo().block().getOwner().block().getPrivateChannel().block().createMessage(embedBuilder.build()).block();
			
		}
	}
	
	@Override
	public void executeComponent(CommandGatewayComponentInformations infos) { }

	@Override
	protected CommandPermission setPermissions() {
		return null;
	}

	@Override
	public boolean isServerOnly() {
		return false;
	}

	@Override
	protected HelpDescription setHelpDescription() {
		return new HelpDescription(this);
	}

	@Override
	protected List<Argument> setArguments(ArrayList<Argument> args) {
		args.add(new Argument("message", "Le message que vous souhaitez envoyer", ArgumentLevel.REQUIRED, ArgumentType.TEXT));
		return args;
	}

}
