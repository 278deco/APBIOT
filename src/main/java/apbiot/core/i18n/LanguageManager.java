package apbiot.core.i18n;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.exceptions.LocalizationKeyFormatException;
import apbiot.core.exceptions.LocalizationReadingException;
import marshmalliow.core.objects.Directory;

public class LanguageManager {

	private static final Logger LOGGER = LogManager.getLogger(LanguageManager.class);
	
	private static volatile LanguageManager instance;
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
		
	private Set<Language> localizations = new HashSet<>();
	
	private LanguageManager() {
	}
	
	public static LanguageManager get() {
		if(instance == null) {
			synchronized (LanguageManager.class) {
				if(instance == null) instance = new LanguageManager();
			}
		}

		return instance;
	}
	
	public void loadLanguagesFolder(Directory folderDir) {
		loadLanguagesFolder(folderDir.getPath());
	}

	public void reloadLanguagesFolder(Directory folderDir) {
		reloadLanguagesFolder(folderDir.getPath());
	}
	
	public void loadLanguagesFolder(Path folderPath) {
		try {
			LOCK.writeLock().lock();
			
			final Stream<Path> paths = Files.list(folderPath);
			
			paths.forEach(path -> {
				if(Files.isDirectory(path)) return;
				
				final String fileName = path.getFileName().toString();
				if(!fileName.endsWith(".json")) return;
				
				LOGGER.info("Loading language {}...", fileName);
				
				try {
					localizations.add(new Language(path));
					
				} catch (LocalizationReadingException | LocalizationKeyFormatException e) {
					LOGGER.warn("Skipping language file {} with cause {}", fileName, e);
				}
			});
			paths.close();
			
		} catch (IOException e) {
			LOGGER.warn("Error while loading language files directory {}", e);
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public void reloadLanguagesFolder(Path folderPath) {
		try {
			LOCK.writeLock().lock();

			localizations.clear();
			loadLanguagesFolder(folderPath);

		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public void loadLanguage(Path folderPath, String languageCode) {
		handleLanguageMapping(folderPath, languageCode, false);
    }
	
	public void reloadLanguage(Path folderPath, String languageCode) {
		handleLanguageMapping(folderPath, languageCode, true);
    }
	
	private void handleLanguageMapping(Path folderPath, String languageCode, boolean reload) {
		try {
            LOCK.writeLock().lock();
            
            final Path path = folderPath.resolve(languageCode + ".json");
            if(!Files.exists(path)) {
                LOGGER.warn("Language file {} does not exist", languageCode);
                return;
            }
            
            LOGGER.info("Loading language {}...", languageCode);
            
            try {
            	final Language language = new Language(path);
            	if(reload) localizations.remove(language);
                localizations.add(language);
                
            } catch (LocalizationReadingException | LocalizationKeyFormatException e) {
                LOGGER.warn("Skipping language file {} with cause {}", languageCode, e);
            }
            
        }finally {
            LOCK.writeLock().unlock();
        }
	}
	
	public Optional<Language> getLanguage(String languageCode) {
		try {
			LOCK.readLock().lock();

			for (Language language : localizations) {
				if (language.getCode().equals(languageCode))
					return Optional.of(language);
			}

		} finally {
			LOCK.readLock().unlock();
		}

		return Optional.empty();
	}
	
	/**
	 * Get the localization of a key in all languages loaded and available for a command <br/>
	 * The locale use discord official language codes
	 * 
	 * @param commandName The command's name
	 * @param localizationKey The localization key (ex: name, description, ...)
	 * @return A map with the discord language code as key and the localized name
	 */
	public Map<String, String> getCommandLocalizationMapping(String commandName, String localizationKey) {
		final String key = "commands.discord."+commandName+"."+localizationKey;

		final Map<String, String> entries = new HashMap<>();
		
		for (Language language : localizations) {
			if(language.hasKey(key)) entries.put(language.getDiscordCode(), language.getOrDefault(key));
		}
		
		return entries;
	}
	
	/**
	 * Get the localization of a key in all languages loaded and available for command's options <br/>
	 * The locale use discord official language codes
	 * 
	 * @param commandName The command's name
	 * @param optionName The option's name
	 * @param localizationKey The localization key (ex: name, description, ...)
	 * @return A map with the discord language code as key and the localized name
	 */
	public Map<String, String> getOptionLocalizationMapping(String commandName, String optionName, String localizationKey) {
		final String key = "options.discord."+commandName +"."+ optionName +"."+ localizationKey;

		final Map<String, String> entries = new HashMap<>();
		
		for (Language language : localizations) {
			if(language.hasKey(key)) entries.put(language.getDiscordCode(), language.getOrDefault(key));
		}
		
		return entries;
	}
	
	/**
	 * Get the localization of a key in all languages loaded and available for option's choices <br/>
	 * The locale use discord official language codes
	 * 
	 * @param commandName The command's name
	 * @param optionName The option's name
	 * @param choiceName The choice's name
	 * @param localizationKey The localization key (ex: name, description, ...)
	 * @return A map with the discord language code as key and the localized name
	 */
	public Map<String, String> getChoiceLocalizationMapping(String commandName, String optionName, String choiceName, String localizationKey) {
		final String key = "choices.discord."+commandName +"."+ optionName +"."+ choiceName +"."+ localizationKey;

		final Map<String, String> entries = new HashMap<>();
		
		for (Language language : localizations) {
			if(language.hasKey(key)) entries.put(language.getDiscordCode(), language.getOrDefault(key));
		}
		
		return entries;
	}
	
}
