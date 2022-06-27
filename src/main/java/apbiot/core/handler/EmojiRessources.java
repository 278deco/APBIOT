package apbiot.core.handler;

import com.vdurmont.emoji.EmojiManager;

import apbiot.core.objects.interfaces.IOptionalHandler;
public class EmojiRessources implements IOptionalHandler {
	/*
	 * A bunch of used emoji links in String form
	 */
	public static String WARNING, INFO, DENY, CHECKMARK, OK;
	
	@Override
	public void register() {
		WARNING = EmojiManager.getByUnicode("⚠").getUnicode();
		INFO = EmojiManager.getByUnicode("ℹ").getUnicode();
		DENY = EmojiManager.getByUnicode("⛔").getUnicode();
		CHECKMARK = EmojiManager.getByUnicode("✅").getUnicode();
		OK = EmojiManager.getByUnicode("🆗").getUnicode();
	}
	
	@Override
	public void init() { }
	
}
