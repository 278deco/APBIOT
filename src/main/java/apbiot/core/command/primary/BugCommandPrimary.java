package apbiot.core.command.primary;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apbiot.core.builder.ColorBuilder;
import apbiot.core.builder.DateBuilder;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.informations.GatewayApplicationCommandPacket;
import apbiot.core.command.informations.GatewayComponentCommandPacket;
import apbiot.core.helper.CommandHelper;
import apbiot.core.objects.enums.CommandCategory;
import apbiot.core.permissions.CommandPermission;
import apbiot.core.utils.Emojis;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.command.Interaction.Type;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionPresentModalSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;

public class BugCommandPrimary extends SlashCommandInstance {
	
	private static final String MODAL_INPUT_ID = "modal-input";
	private static final String MODAL_ID = "reportmodal";
	
	private EmbedCreateSpec bugEmbed;
	private ActionRow buttonsRow;
	private InteractionPresentModalSpec reportModal;
	
	private final String botUsername, botAvatarUrl;
	
	public BugCommandPrimary(User botAccount) {
		super(Arrays.asList("bug"), "Permet d'envoyer un message au développeur pour l'informer d'un bug.", CommandCategory.UTILITY);
		
		this.botUsername = botAccount.getUsername();
		this.botAvatarUrl = botAccount.getAvatarUrl();
	}
	
	@Override
	public List<ApplicationCommandOptionData> getCommandArguments(ArrayList<ApplicationCommandOptionData> args) {
		return args;
	}
	
	@Override
	public void buildCommand() {
		this.built = true;
		
		final EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
		final InteractionPresentModalSpec.Builder modal = InteractionPresentModalSpec.builder();
		
		builder.author(this.botUsername, "https://www.youtube.com/watch?v=dQw4w9WgXcQ"/*Funny temporary fix TODO REMOVE*/, this.botAvatarUrl);
		builder.title("Rapport de bug");
		builder.color(ColorBuilder.randomColor().get());
		builder.footer(this.botUsername+" (278deco) "+new DateBuilder(ZoneId.of("Europe/Paris")).getYear()+" © | Tout droits réservés", null);
		
		modal.title("SAINT PC - Report de bug");
		modal.addComponent(ActionRow.of(TextInput.paragraph(MODAL_INPUT_ID, "Description :",0,3900)));
		
		this.bugEmbed = builder.build();
		this.reportModal = modal.build();
		
		final Button questionButton = Button.secondary(CommandHelper.generateComponentID(this, "question_related_report"), "QUESTION");
		final Button gameButton = Button.secondary(CommandHelper.generateComponentID(this, "game_related_report"), "JEUX");
		final Button grammarButton = Button.secondary(CommandHelper.generateComponentID(this, "grammar_related_report"), "ORTHOGRAPHE");
		final Button musicButton = Button.secondary(CommandHelper.generateComponentID(this, "music_related_report"), "MUSIQUE");
		final Button otherButton = Button.secondary(CommandHelper.generateComponentID(this, "other_related_report"), "AUTRE");
		
		this.buttonsRow = ActionRow.of(questionButton, gameButton, grammarButton, musicButton, otherButton);
	}
	
	@Override
	public void execute(GatewayApplicationCommandPacket infos) {
		infos.getEvent().reply(InteractionApplicationCommandCallbackSpec.builder()
				.content("🗒 "+infos.getExecutor().getMention()+", Vous êtes actuellement dans la démarche afin de signaler un bug.\n\n"
						+ "**Merci de choisir la catégorie décrivant au mieux le problème rencontré :**")
				.addComponent(this.buttonsRow)
				.ephemeral(true)
				.build()).block();
	}
	
	@Override
	public void executeComponent(GatewayComponentCommandPacket infos) { 
		if(infos.getEvent().getInteraction().getType() == Type.MESSAGE_COMPONENT) {
			
			final InteractionPresentModalSpec.Builder modalSpec = InteractionPresentModalSpec.builder();
			modalSpec.from(this.reportModal);
			modalSpec.customId(CommandHelper.generateComponentID(this, MODAL_ID+"["+infos.getComponentId()+"]"));
			
			infos.getEvent().presentModal(modalSpec.build()).block();
			
		}else if(infos.getComponentId().contains(MODAL_ID) && infos.getEvent().getInteraction().getType() == Type.MODAL_SUBMIT) {
			infos.getEvent().deferEdit().withEphemeral(true).block();
			
			final Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(infos.getComponentId());
			if(!m.find()) {
				infos.getEvent().editReply(Emojis.TOOLS+" Une erreur est survenue, merci de réessayer ultérieurement !").block();
				return;
			}
			
			final TextInput textInput = ((ModalSubmitInteractionEvent)infos.getEvent()).getComponents(TextInput.class).stream()
					.filter(input -> input.getCustomId().equals(MODAL_INPUT_ID)).findFirst().orElseThrow();
			
			final EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
			final DateBuilder date = new DateBuilder(ZoneId.of("Europe/Paris"));
			
			embedBuilder.from(bugEmbed);
			embedBuilder.description("Émit le **"+date.getFormattedDate('/', true)+"** à **"+date.getFormattedTime()+"**.\n\n**Message:**\n"+textInput.getValue().orElse("*Aucune description*")+"\n");
			embedBuilder.addField("Auteur", "**"+infos.getExecutor().getUsername()+ "** - *"+infos.getExecutor().getId().asString()+"*", false);
			embedBuilder.addField("Catégorie", m.group(1), false);
			
			boolean success = true;
			try {
				infos.getEvent().getClient().getApplicationInfo().block().getOwner().block().getPrivateChannel().block().createMessage(embedBuilder.build()).block();					
			}catch(Exception e) { success = false; }
			
			if(success) infos.getEvent().editReply(InteractionReplyEditSpec.builder().componentsOrNull(null).contentOrNull(Emojis.WHITE_CHECK_MARK+" Votre message à bien été envoyé ! *(Tout abus de cette commande signifie des cadeaux en moins du Père Noël)*").build()).block();
			else infos.getEvent().editReply(Emojis.TOOLS+" Une erreur est survenue, merci de réessayer ultérieurement !").block();
		}
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
