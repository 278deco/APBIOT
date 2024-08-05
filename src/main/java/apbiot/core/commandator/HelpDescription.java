package apbiot.core.commandator;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.NativeCommandInstance;
import apbiot.core.helper.StringHelper;
import apbiot.core.objects.Argument;
import apbiot.core.objects.Tuple;
import apbiot.core.objects.enums.ArgumentLevel;

public class HelpDescription {
	
	private String helpDesc;
	
	/**
	 * Create a new instance of HelpDescription
	 * @param cmd - the command instance
	 */
	public HelpDescription(NativeCommandInstance cmd) {
		this.helpDesc = format(cmd);
	}
	
	/**
	 * Format the response returned to the user
	 * @param cmd - the command instance
	 * @return the string result
	 */
	private String format(NativeCommandInstance cmd) {
		StringBuilder sb = new StringBuilder();
		
		 sb.append("ℹ **Commande "+cmd.getDisplayName()+" :**");
		 sb.append("\n   __Argument(s) requis__ :");
		 Tuple<List<String>,List<String>> tp = formatArgument(cmd.getRequiredArguments());
		 sb.append("```\tObligatoire(s) ("+tp.getValueA().size()+"):\n"+StringHelper.listToString(tp.getValueA(), "\n")+"\n");
		 sb.append("\tOptionnels(s) ("+tp.getValueB().size()+"):\n"+StringHelper.listToString(tp.getValueB(), "\n")+"```");
		 sb.append("\n   __Description__ :```\t"+cmd.getDescription()+"```");
		 
		 return sb.toString();
	}
	
	/**
	 * Format the command's arguments to be human readable
	 * @param args - the command's arguments
	 * @return the formatted argument
	 */
	private Tuple<List<String>,List<String>> formatArgument(List<Argument> args) {
		List<String> required = new ArrayList<>(), optionnal = new ArrayList<>();
		
		for(Argument arg : args) {
			if(arg.getLevel() == ArgumentLevel.REQUIRED) {
				required.add("\t\t- "+arg.getPrincipalName()+" = "+arg.getDescription()+" ("+arg.getType().getTypeName()+")");
			}else {
				optionnal.add("\t\t- "+arg.getPrincipalName()+" = "+arg.getDescription()+" ("+arg.getType().getTypeName()+")");
			}
		}
		
		return Tuple.of(required, optionnal);
	}
	
	public String getDescription() {
		return helpDesc;
	}
	
}
