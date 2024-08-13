package apbiot.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apbiot.core.i18n.LanguageManager;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.possible.Possible;

public class CommandOptions {

	private final String name;
    private final ApplicationCommandOption.Type type;
    private final boolean required;
    private final Possible<Boolean> autocomplete;
    private final List<Integer> channelTypes;
    private final List<CommandOptions> options;
    private final List<CommandOptionChoices> choices;
    private final Possible<Double> minValue, maxValue;
    private final Possible<Integer> minLength, maxLength;
    
    private String description;
    private Map<String, String> nameLocalizations;
    private Map<String, String> descriptionLocalizations;
	
    private CommandOptions(String name, ApplicationCommandOption.Type type, 
    		boolean required, Possible<Boolean> autocomplete, List<Integer> channelTypes,
    		List<CommandOptions> options, List<CommandOptionChoices> choices,
    		Possible<Double> minValue, Possible<Double> maxValue, Possible<Integer> minLength, Possible<Integer> maxLength) {
    	this.name = name;
    	this.type = type;
    	this.required = required;
    	this.autocomplete = autocomplete;
    	this.channelTypes = channelTypes;
    	this.options = options;
    	this.choices = choices;
    	this.minValue = minValue;
    	this.maxValue = maxValue;
    	this.minLength = minLength;
    	this.maxLength = maxLength;
    }
    
    public void updateLocalizationMapping(String commandName) {
		recursiveMappingUpdate(commandName, null);
    }
    
    private void recursiveMappingUpdate(String commandName, String parentName) {
    	final String recursiveName = parentName != null ? parentName + "." + name : name;
		for (CommandOptions option : options) {
			option.recursiveMappingUpdate(commandName, recursiveName);
		}
		
		for (CommandOptionChoices choice : choices) {
			choice.updateLocalizationMapping(commandName, recursiveName);
		}
		
		this.nameLocalizations = LanguageManager.get().getOptionLocalizationMapping(commandName, recursiveName, "name");
		this.descriptionLocalizations = LanguageManager.get().getOptionLocalizationMapping(commandName, recursiveName, "description");
		
		if(this.descriptionLocalizations != null) {
			this.description = this.descriptionLocalizations.getOrDefault("en-US", "commands.discord.no.description");
		}else {
			this.description = "commands.discord.no.description";
		}
    }

	public ApplicationCommandOptionData get() {
		final List<ApplicationCommandOptionData> optionsData = new ArrayList<>();
		this.options.forEach(option -> optionsData.add(option.get()));
		
		final List<ApplicationCommandOptionChoiceData> choicesData = new ArrayList<>();
		this.choices.forEach(choice -> choicesData.add(choice.get()));
		
		final ApplicationCommandOptionData data = ApplicationCommandOptionData.builder().name(name).type(type.getValue())
				.description(description).nameLocalizationsOrNull(nameLocalizations).descriptionLocalizationsOrNull(descriptionLocalizations)
				.required(Possible.of(required)).autocomplete(autocomplete).channelTypes(channelTypes).options(optionsData)
				.choices(choicesData).minValue(minValue).maxValue(maxValue).minLength(minLength).maxLength(maxLength)
				.build();

		return data;
	}	

	public static CommandOptions.Builder builder() {
		return new CommandOptions.Builder();
	}
	
	public static final class Builder {
		
		private String name;
	    private ApplicationCommandOption.Type type;
	    private boolean required;
	    private Possible<Boolean> autocomplete;
	    private List<Integer> channelTypes;
	    private List<CommandOptions> options;
	    private List<CommandOptionChoices> choices;
	    private Possible<Double> minValue, maxValue;
	    private Possible<Integer> minLength, maxLength;
		
		private Builder() { 
			this.options = new ArrayList<>();
			this.choices = new ArrayList<>();
			this.channelTypes = new ArrayList<>();
			
			this.autocomplete = Possible.absent();
			this.minValue = Possible.absent();
			this.maxValue = Possible.absent();
			this.minLength = Possible.absent();
			this.maxLength = Possible.absent();
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder type(ApplicationCommandOption.Type type) {
			this.type = type;
			return this;
		}
		
		public Builder required(boolean required) {
			this.required = required;
			return this;
		}
		
		public Builder autocomplete(boolean autocomplete) {
			this.autocomplete = Possible.of(autocomplete);
			return this;
		}
		
		public Builder autocomplete(Boolean autocomplete) {
			if (autocomplete != null)
				this.autocomplete = Possible.of(autocomplete);
			else
				this.autocomplete = Possible.absent();
			return this;
		}
		
		public Builder channelTypes(List<Channel.Type> channelTypes) {
			final List<Integer> typesInteger = new ArrayList<>();
			for (Channel.Type type : channelTypes) {
				typesInteger.add(type.getValue());
			}
			
			this.channelTypes = typesInteger;
			return this;
		}
		
		public Builder addChannelType(Channel.Type channelType) {
			this.channelTypes.add(channelType.getValue());
			return this;
		}
		
		public Builder addChannelType(Channel.Type... channelTypes) {
			for (Channel.Type type : channelTypes)
				this.channelTypes.add(type.getValue());
			return this;
		}
		
		public Builder options(List<CommandOptions> options) {
			this.options = options;
			return this;
		}
		
		public Builder addOption(CommandOptions options) {
			this.options.add(options);
			return this;
		}
		
		public Builder addOption(CommandOptions... options) {
			for (CommandOptions option : options) this.options.add(option);
			return this;
		}
		
		public Builder choices(List<CommandOptionChoices> choices) {
			this.choices = choices;
			return this;
		}
		
		public Builder addChoice(CommandOptionChoices choices) {
			this.choices.add(choices);
			return this;
		}
		
		public Builder addChoice(CommandOptionChoices... choices) {
			for (CommandOptionChoices choice : choices) this.choices.add(choice);
			return this;
		}
		
		public Builder minValue(double minValue) {
			this.minValue = Possible.of(minValue);
			return this;
		}
		
		public Builder maxValue(double maxValue) {
			this.maxValue = Possible.of(maxValue);
			return this;
		}
		
		public Builder minValue(Double minValue) {
			if (minValue != null) 
				this.minValue = Possible.of(minValue);
			else 
				this.minValue = Possible.absent();
			return this;
		}
		
		public Builder maxValue(Double maxValue) {
			if (maxValue != null)
				this.maxValue = Possible.of(maxValue);
			else
				this.maxValue = Possible.absent();
			return this;
		}
		
		public Builder minLength(int minLength) {
			this.minLength = Possible.of(minLength);
			return this;
		}
		
		public Builder maxLength(int maxLength) {
			this.maxLength = Possible.of(maxLength);
			return this;
		}
		
		public Builder minLength(Integer minLength) {
			if (minLength != null)
				this.minLength = Possible.of(minLength);
			else
				this.minLength = Possible.absent();
			return this;
		}
		
		public Builder maxLength(Integer maxLength) {
			if (maxLength != null)
				this.maxLength = Possible.of(maxLength);
			else
				this.maxLength = Possible.absent();
			return this;
		}
		
		public CommandOptions build() {
            return new CommandOptions(name, type, required, autocomplete, 
            		channelTypes, options, choices, minValue, maxValue, 
            		minLength, maxLength);
		}
	}
	
}
