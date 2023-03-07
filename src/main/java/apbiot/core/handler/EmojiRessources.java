package apbiot.core.handler;

import com.vdurmont.emoji.EmojiManager;
import apbiot.core.objects.interfaces.IOptionalHandler;

/**
 * Classed used to access and use emoji
 * @author 278deco
 * @deprecated 4.0
 * @since 0.1
 */
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
