package apbiot.core.handler;

import com.vdurmont.emoji.EmojiManager;

import apbiot.core.objects.interfaces.IHandler;
import discord4j.core.GatewayDiscordClient;

/**
 * Classed used to access and use emoji
 * @author 278deco
 * @deprecated 4.0
 * @since 0.1
 */
public class EmojiRessources implements IHandler {
	/*
	 * A bunch of used emoji links in String form
	 */
	public static String WARNING, INFO, DENY, CHECKMARK, OK;

	@Override
	public void preRegister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(GatewayDiscordClient gateway) {
		WARNING = EmojiManager.getByUnicode("⚠").getUnicode();
		INFO = EmojiManager.getByUnicode("ℹ").getUnicode();
		DENY = EmojiManager.getByUnicode("⛔").getUnicode();
		CHECKMARK = EmojiManager.getByUnicode("✅").getUnicode();
		OK = EmojiManager.getByUnicode("🆗").getUnicode();
		
	}

	@Override
	public void postRegister() {
		// TODO Auto-generated method stub
		
	}
	
}
