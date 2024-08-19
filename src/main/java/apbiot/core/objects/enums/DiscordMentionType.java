package apbiot.core.objects.enums;

import java.util.regex.Pattern;

import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.reaction.ReactionEmoji.Custom;

public enum DiscordMentionType {

	/**
	 * Representation of an {@link User} as a mention <br/>
	 * @see <a href="https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	USER(Pattern.compile("^<@!?(?<id>\\d+)>$")),
	
	/**
	 * Representation of a {@link Role} as a mention <br/>
	 * @see <a href="https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	ROLE(Pattern.compile("^<@&(?<id>\\d+)>$")),
	
	/**
	 * Representation of a {@link Channel} as a mention <br/>
	 * @see <a href="https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	CHANNEL(Pattern.compile("^<#(?<id>\\d+)>$")),
	
	/**
	 * Representation of an {@link Custom} emoji as a mention<br/>
	 * @see <a href="https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	CUSTOM_EMOJI(Pattern.compile("^<:(?<name>\\w+):(?<id>\\d+)>$")),
	
	/**
	 * Representation of an {@link Custom} animated emoji as a mention<br/>
	 * @see <a href="https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	CUSTOM_ANIMATED_EMOJI(Pattern.compile("^<a:(?<name>\\w+):(?<id>\\d+)>$")),
	
	/**
	 * Representation of an Unix Timestamp as a mention<br/>
	 * 
	 * @see <a href= "https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	UNIX_TIMESTAMP(Pattern.compile("^<t:(?<timestamp>\\d+)>$")),
	
	/**
	 * Representation of a styled Unix Timestamp as a mention<br/>
	 * 
	 * @see <a href= "https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	UNIX_TIMESTAMP_STYLED(Pattern.compile("^<t:(?<timestamp>\\d+):(?<style>[tTdDfFR])>$")),
	
	/**
	 * Representation of a Slash Command as a mention<br/>
	 * 
	 * @see <a href= "https://discord.com/developers/docs/reference#message-formatting">Discord message formating</a>
	 */
	SLASH_COMMAND(Pattern.compile("^</(?<command>\\w+):(?<id>\\d+)>$"));
	
	private final Pattern pattern;
	private DiscordMentionType(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public Pattern getPattern() {
		return pattern;
	}

}
