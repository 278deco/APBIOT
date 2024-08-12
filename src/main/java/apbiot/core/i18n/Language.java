package apbiot.core.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import apbiot.core.exceptions.LocalizationKeyFormatException;
import apbiot.core.exceptions.LocalizationReadingException;
import marshmalliow.core.exceptions.JSONParseException;
import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.io.JSONParser;
import marshmalliow.core.objects.Directory;

public class Language {
	
	private static final Pattern ENTRY_PATTERN = Pattern.compile("^(\\w+\\.)(\\w+\\.)*(\\w+)$");
	
	private String name, region;
	private String code, discordCode;

	private Map<String, String> entries;

	public Language(Directory dir, String code) throws LocalizationReadingException, LocalizationKeyFormatException {
		readFile(dir.getPath().resolve(code + ".json"), code);
	}

	public Language(Path path, String code) throws LocalizationReadingException, LocalizationKeyFormatException {
		readFile(path.resolve(code + ".json"), code);
	}
	
	public Language(Path path) throws LocalizationReadingException, LocalizationKeyFormatException {
		readFile(path, path.getFileName().toString().replace(".json", ""));
	}

	@SuppressWarnings("unchecked")
	private void readFile(Path fullPath, String code) throws LocalizationReadingException, LocalizationKeyFormatException {
		BufferedReader reader = null;
		try {
			if(Files.size(fullPath) <= 0) {
	            throw new LocalizationReadingException("Localization "+code+" file is empty");
	        }
			
			reader = Files.newBufferedReader(fullPath);
							
			final JSONParser parser = new JSONParser(new JSONLexer(reader));
			this.entries = (Map<String,String>)parser.parse();
			
			//Check the validity of the file
			this.region = this.entries.get("language.region");
			this.name = this.entries.get("language.name");
			this.code = this.entries.get("language.code");
			this.discordCode = this.entries.get("language.discordcode");
			
			if(this.region == null || this.name == null || this.code == null || this.discordCode == null)
				throw new LocalizationKeyFormatException("Missing language keys information (region/name/code/discordcode)");

			//Check the validy of the language code
			if (!"und".equals(Locale.forLanguageTag(code).toLanguageTag()))
				throw new LocalizationReadingException("Invalid language code "+code);
			
			//Check the validity of the entries
			for(Map.Entry<String, String> entry : this.entries.entrySet()) {
				if(!ENTRY_PATTERN.matcher(entry.getKey()).matches()) {
					throw new LocalizationKeyFormatException("Format error with key "+entry.getKey());
				}
			}			
						
		}catch(JSONParseException e) {
			throw new LocalizationReadingException("Unable to parse localization file "+code, e);
		}catch(IOException e) {
			throw new LocalizationReadingException("Unexcepted exception while reading localization file "+code, e);
		}finally {
			if(reader != null)
				try { reader.close(); } catch (IOException e) { /* Unused */ }
		}
	}
	
	/**
	 * Get the localization associated with the key. <br/>
	 * If no localization is found for the given key, 
	 * returns the key as a result.
	 * 
	 * @param key The key bound to the localization
	 * @return The localization depending on the key
	 */
	public final String getOrDefault(String key) {
		return this.entries.getOrDefault(key, key);
	}
	
	/**
	 * Get the localization associated with the key. <br/>
	 * If no localization is found for the given key, 
	 * returns the <code>defaultValue</code>
	 * 
	 * @param key The key bound to the localization
	 * @param defaultValue The default value to be returned if no key exist
	 * @return The localization depending on the key
	 */
	public final String getOrDefault(String key, String defaultValue) {
		return this.entries.getOrDefault(key, defaultValue);
	}
	
	
	/**
	 * Get the localization associated with the key. <br/>
	 * If the localization doesn't exist for the given key, 
	 * try to get the localization for the <code>otherKey</code>. <br/>
	 * At the end, if no localization is found for either keys, 
	 * returns the key as a result.
	 * 
	 * @param key The key bound to the localization
	 * @param otherKey The spare key if the first doesn't exist
	 * @return The localization depending on the key
	 */
	public final String getOrElse(String key, String otherKey) {
		return this.entries.getOrDefault(key, this.entries.getOrDefault(otherKey, key));
	}
	
	/**
	 * Verify if the given key has a mapped localization
	 * @param key The key bound to the localization
	 * @return true if the key has a mapping
	 */
	public final boolean hasKey(String key) {
		return this.entries.containsKey(key);
	}
	
	/**
	 * Get the language's region
	 * <p>
	 * Example : 
	 * <ul>
	 * 	<li>United States</li>
	 * 	<li>France</li>
	 * 	<li>United Kingdom</li>
	 * 	<li>Italy</li>
	 * </ul>
	 * 
	 * @return the region associated with this instance
	 */
	public String getRegion() {
		return region;
	}
	
	/**
	 * Get the language's name
	 * <p>
	 * Example : 
	 * <ul>
	 * 	<li>English</li>
	 * 	<li>French</li>
	 * 	<li>English</li>
	 * 	<li>Italian</li>
	 * </ul>
	 * 
	 * @return the name associated with this instance
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Get the language's code
	 * <p>
	 * Example : 
	 * <ul>
	 * 	<li>en_US</li>
	 * 	<li>fr_FR</li>
	 * 	<li>en_GB</li>
	 * 	<li>it_IT</li>
	 * </ul>
	 * 
	 * @return The code associated with this instance
	 */
	public final String getCode() {
		return code;
	}
	
	/**
	 * Get the language's discord code <br/>
	 * This code is similar to the official code but is formatted for discord
	 * <p>
	 * Example :
	 * <ul>
	 * <li>en-US</li>
	 * <li>fr</li>
	 * <li>en-GB</li>
	 * <li>it</li>
	 * </ul>
	 * @see <a href="https://discord.com/developers/docs/reference#locales">Discord Locales</a>
	 * @return The discord code associated with this instance
	 */
	public final String getDiscordCode() {
		return discordCode;
	}
	
	@Override
	public String toString() {
		return "Language [name=" + name + ", region=" + region + ", code=" + code + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Language)) return false;
        final Language language = (Language) obj;
        return language.code == this.code && language.name == this.name && 
        		language.region == this.region && language.discordCode == this.discordCode;
	}

}
