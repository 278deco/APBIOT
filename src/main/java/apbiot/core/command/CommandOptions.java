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
    private final boolean autocomplete;
    private final List<Integer> channelTypes;
    private final List<CommandOptions> options;
    private final List<CommandOptionChoices> choices;
    private final Double minValue, maxValue;
    private final int minLength, maxLength;
    
    private Map<String, String> nameLocalizations;
    private Map<String, String> descriptionLocalizations;
	
    private CommandOptions(String name, ApplicationCommandOption.Type type, 
    		boolean required, boolean autocomplete, List<Integer> channelTypes,
    		List<CommandOptions> options, List<CommandOptionChoices> choices,
    		Double minValue, Double maxValue, int minLength, int maxLength) {
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
    }

	public ApplicationCommandOptionData get() {
		final List<ApplicationCommandOptionData> optionsData = new ArrayList<>();
		this.options.forEach(option -> optionsData.add(option.get()));
		
		final List<ApplicationCommandOptionChoiceData> choicesData = new ArrayList<>();
		this.choices.forEach(choice -> choicesData.add(choice.get()));
		
		final ApplicationCommandOptionData data = ApplicationCommandOptionData.builder().name(name).type(type.getValue())
				.nameLocalizationsOrNull(nameLocalizations).descriptionLocalizationsOrNull(descriptionLocalizations)
				.required(Possible.of(required)).autocomplete(Possible.of(autocomplete)).channelTypes(channelTypes)
				.options(optionsData).choices(choicesData).minValue(Possible.of(minValue)).maxValue(Possible.of(maxValue))
				.minLength(Possible.of(minLength)).maxLength(Possible.of(maxLength))
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
	    private boolean autocomplete;
	    private List<Integer> channelTypes;
	    private List<CommandOptions> options;
	    private List<CommandOptionChoices> choices;
	    private Double minValue, maxValue;
	    private int minLength, maxLength;
		
		private Builder() { }
		
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
			this.autocomplete = autocomplete;
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
		
		public Builder options(List<CommandOptions> options) {
			this.options = options;
			return this;
		}
		
		public Builder choices(List<CommandOptionChoices> choices) {
			this.choices = choices;
			return this;
		}
		
		public Builder minValue(Double minValue) {
			this.minValue = minValue;
			return this;
		}
		
		public Builder maxValue(Double maxValue) {
			this.maxValue = maxValue;
			return this;
		}
		
		public Builder minLength(int minLength) {
			this.minLength = minLength;
			return this;
		}
		
		public Builder maxLength(int maxLength) {
			this.maxLength = maxLength;
			return this;
		}
		
		public CommandOptions build() {
            return new CommandOptions(name, type, required, autocomplete, 
            		channelTypes, options, choices, minValue, maxValue, 
            		minLength, maxLength);
		}
	}
	
}
