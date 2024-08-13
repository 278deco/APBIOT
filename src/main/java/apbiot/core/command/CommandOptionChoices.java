package apbiot.core.command;

import java.util.Map;

import apbiot.core.i18n.LanguageManager;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;

public class CommandOptionChoices {

	private final String name;
	private final Object value;
	
	private Map<String, String> nameLocalizations;
	
	private CommandOptionChoices(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public void updateLocalizationMapping(String commandName, String optionName) {
		this.nameLocalizations = LanguageManager.get().getChoiceLocalizationMapping(commandName, optionName, name, "name");
    }
	
	public ApplicationCommandOptionChoiceData get() {
		final ApplicationCommandOptionChoiceData data = ApplicationCommandOptionChoiceData.builder()
				.name(name)
				.nameLocalizationsOrNull(nameLocalizations)
				.value(value)
				.build();
		
		return data;
	}
	
	public static final Builder builder() {
		return new CommandOptionChoices.Builder();
	}
	
	public static final class Builder {
		
		private String name;
        private Object value;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder value(Object value) {
            this.value = value;
            return this;
        }
        
        public CommandOptionChoices build() {
            return new CommandOptionChoices(name, value);
        }
	}
}
