package apbiot.core.command;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.informations.CommandGatewaySlashInformations;
import apbiot.core.commandator.HelpDescription;
import apbiot.core.objects.Argument;
import apbiot.core.objects.interfaces.ICommandCategory;

public abstract class NativeCommandInstance extends AbstractCommandInstance {

	private List<Argument> arguments;
	private HelpDescription helpDesc;
	
	public NativeCommandInstance(List<String> cmdName, String description, ICommandCategory category) {
		super(cmdName, description, category);
		
		this.arguments = setArguments(new ArrayList<>());
		this.helpDesc = setHelpDescription();
	}
	
	public NativeCommandInstance(List<String> cmdName, String description, ICommandCategory category, String staticID) {
		super(cmdName, description, category, staticID);
		
		this.arguments = setArguments(new ArrayList<>());
		this.helpDesc = setHelpDescription();
	}
	
	@Override
	public void execute(CommandGatewaySlashInformations gatewayInformation) { }
	
	@Override
	public void buildCommand() { built = true; }

	/**
	 * Define the help description of the command
	 * @see apbiot.core.commandator.HelpDescription
	 * @return an instance of HelpDescription
	 */
	protected abstract HelpDescription setHelpDescription();
	
	/**
	 * Get the HelpDescription of the command
	 * @see apbiot.core.commandator.HelpDescription
	 * @return the HelpDescription
	 */
	public HelpDescription getHelpDescription() {
		return helpDesc;
	}
	
	/**
	 * Define the arguments of the command
	 * @param args - the arguments
	 * @see apbiot.core.objects.Argument
	 * @return a List of Argument
	 */
	protected abstract List<Argument> setArguments(ArrayList<Argument> args);
	
	/**
	 * Get the required arguments for the command
	 * @see apbiot.core.objects.Argument
	 * @return the required arguments
	 */
	public List<Argument> getRequiredArguments() {
		return arguments;
	}
	
}
