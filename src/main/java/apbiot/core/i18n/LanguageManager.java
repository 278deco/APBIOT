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
			
			Files.list(folderPath).forEach(path -> {
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
	
	
	public Map<String, String> getCommandLocalizationMapping(String commandName, String localizationKey) {
		final String key = "commands.discord."+commandName+"."+localizationKey;

		final Map<String, String> entries = new HashMap<>();
		
		for (Language language : localizations) {
			if(language.hasKey(key)) entries.put(language.getCode(), language.getOrDefault(key));
		}
		
		return entries;
	}
	
}
