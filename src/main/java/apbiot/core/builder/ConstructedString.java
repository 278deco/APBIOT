package apbiot.core.builder;

import java.util.List;

/**
 * String helper class, match string with arguments and the arguments themself
 * @author 278deco
 */
public class ConstructedString {

	public static final String DELIMITER = "#";
	private String data;
	
	/**
	 * Create a new instance of ConstructedString
	 * @param message - a base message
	 */
	public ConstructedString(String message) {
		this.data = message;
	}
	
	/**
	 * Main method that tranforms placeholders to real arguments
	 * @param list - the list of arguments
	 * @return the same string with the arguments in it
	 */
	public synchronized ConstructedString decode(List<DataInput> list) {
		for(int i = 0; i < list.size(); i++) {
			if(data.contains("#"+(i+1)+""+list.get(i).getSavedType()) && list.get(i).getSavedArgument() != null) {
				data = data.replace("#"+(i+1)+""+list.get(i).getSavedType(), list.get(i).getSavedArgument());
			}
		}
		
		return this;
	}
	
	/**
	 * Get the number of placeholders containing into the string
	 * @return the number of placeholders
	 */
	public synchronized int getNumberOfArguments() {
		int argsNb = 0;
		
		for(int i = 0; i < data.length(); i++) {
			if(i+1 < data.length()) {
				if(data.charAt(i) == '#' && String.valueOf(data.charAt(i+1)).matches("[1-9]")) {
					argsNb+=1;
				}
			}
		}
		
		return argsNb;
	}
	
	/**
	 * Used to get the string
	 * @return the constructed string
	 */
	public String toString() {
		return data;
	}
	
	/**
	 * Contains the list of symbols which define the type of the argument
	 * @author 278deco
	 */
	public class DataType {
		public static final String INTEGER = "%i";
		public static final String FLOAT = "%f";
		public static final String STRING = "%s";
		public static final String BOOLEAN = "%b";
		public static final String DOUBLE = "%d";
		
	}
	
	/**
	 * Used to give the arguments to the main method
	 * @author 278deco
	 */
	public static class DataInput {
		private String type, arg;
		
		public DataInput(String type, String argument) {
			this.type = type;
			this.arg = argument;
		}
		
		protected String getSavedType() {
			return type;
		}
		protected String getSavedArgument() {
			return arg;
		}
		
	}
	
}
