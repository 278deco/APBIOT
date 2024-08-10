package apbiot.core.commandator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import apbiot.core.objects.enums.ApplicationCommandType;

/**
 * A pseudo-AI using k-NN algorithm to determine what command the user wanted to write 
 * Main class
 * @author 278deco
 * @see apbiot.core.commandator.CommandatorRequest
 * @see apbiot.core.commandator.CommandatorMethods
 * @version 1.1.0
 */
public class Commandator {
	
	private Set<CommandatorEntry> commandsList = new HashSet<CommandatorEntry>();
	
	/**
	 * Create a new instance of Commandator
	 * @param commands the list of commands
	 */
	public Commandator(Set<CommandatorEntry> commands) {
		this.commandsList.addAll(commands);
	}
	
	/**
	 * Create a new instance of Commandator
	 * @param nativeCommands The set of commands
	 * @param slashCommands The set of commands
	 */
	public Commandator(Set<String> nativeCommands, Set<String> slashCommands) {
		nativeCommands.forEach(cmdName -> {
			this.commandsList.add(new CommandatorEntry(cmdName, ApplicationCommandType.NATIVE));
			
		});
		
		slashCommands.forEach(cmdName -> {
			this.commandsList.add(new CommandatorEntry(cmdName, ApplicationCommandType.CHAT_INPUT));
		});
	}
	
	/**
	 * Launch a new request to the algorithm and find a result
	 * @param userCmd The command entered by the user
	 * @return the result of the request
	 * @throws InterruptedException
	 */
	public Optional<CommandatorEntry> newRequest(String userCmd) throws InterruptedException {
		CommandatorRequest request = new CommandatorRequest(commandsList, userCmd);
		Thread t = new Thread(request);
		t.start();
		t.join();
		
		return request.getProposalCommand();
	}
}
