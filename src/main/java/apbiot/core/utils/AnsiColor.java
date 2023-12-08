package apbiot.core.utils;

/**
 * Static attributes used to add colors to string using ANSI escape code<br/><br/>
 * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">ANSI Escape Code</a>
 * @author 278deco
 * @since 5.0
 */
public class AnsiColor {

	public static final String DEFAULT = "\u001B[0m";
	
	public static final String BLACK = "\u001B[30m";
	public static final String WHITE = "\u001B[97m";
	public static final String GRAY = "\u001B[90m";
	public static final String LIGHT_GRAY = "\u001B[37m";

	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String MAGENTA = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	
	public static final String LIGHT_RED = "\u001B[91m";
	public static final String LIGHT_GREEN = "\u001B[92m";
	public static final String LIGHT_YELLOW = "\u001B[93m";
	public static final String LIGHT_BLUE = "\u001B[94m";
	public static final String LIGHT_MAGENTA = "\u001B[95m";
	public static final String LIGHT_CYAN = "\u001B[96m";
	
	public static final String BACKGROUND_BLACK = "\u001B[40m";
	public static final String BACKGROUND_WHITE = "\u001B[107m";
	public static final String BACKGROUND_GRAY = "\u001B[100m";
	public static final String BACKGROUND_LIGHT_GRAY = "\u001B[47m";

	public static final String BACKGROUND_RED = "\u001B[41m";
	public static final String BACKGROUND_GREEN = "\u001B[42m";
	public static final String BACKGROUND_YELLOW = "\u001B[43m";
	public static final String BACKGROUND_BLUE = "\u001B[44m";
	public static final String BACKGROUND_MAGENTA = "\u001B[45m";
	public static final String BACKGROUND_CYAN = "\u001B[46m";
	
	public static final String BACKGROUND_LIGHT_RED = "\u001B[101m";
	public static final String BACKGROUND_LIGHT_GREEN = "\u001B[102m";
	public static final String BACKGROUND_LIGHT_YELLOW = "\u001B[103m";
	public static final String BACKGROUND_LIGHT_BLUE = "\u001B[104m";
	public static final String BACKGROUND_LIGHT_MAGENTA = "\u001B[105m";
	public static final String BACKGROUND_LIGHT_CYAN = "\u001B[106m";
	
	public static final String BLINK = "\u001B[5m";
	public static final String RAPID_BLINK = "\u001B[6m";
	
	public static final String ITALIC = "\u001B[3m";
	public static final String BOLD = "\u001B[1m";
	public static final String UNDERLINE = "\u001B[4m";
	public static final String NO_UNDERLINE = "\u001B[24m";
	
}
