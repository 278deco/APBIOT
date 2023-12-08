package apbiot.core.commandator;

import java.util.HashSet;
import java.util.Set;

/**
 * An IA using k-NN algorithm to determine what command the user wanted to write 
 * Main class
 * @author 278deco
 * @see apbiot.core.commandator.CommandatorRequest
 * @see apbiot.core.commandator.CommandatorMethods
 */
public class Commandator {
	
	private Set<String> commandsList = new HashSet<String>();
	
	/**
	 * Create a new instance of Commandator
	 * @param commands - the list of commands
	 */
	public Commandator(Set<String> commands) {
		this.commandsList.addAll(commands);
	}
	
	/**
	 * Create a new instance of Commandator
	 * @param nativeCommands The set of commands
	 * @param slashCommands The set of commands
	 */
	public Commandator(Set<Set<String>> nativeCommands, Set<Set<String>> slashCommands) {
		nativeCommands.forEach(cmdName -> {
			this.commandsList.addAll(cmdName);
		});
		
		slashCommands.forEach(cmdName -> {
			this.commandsList.addAll(cmdName);
		});
	}
	
	/**
	 * Launch a new request to the algorithm and find a result
	 * @param userCmd - the command entered by the user
	 * @return the result of the request
	 * @throws InterruptedException
	 */
	public String newRequest(String userCmd) throws InterruptedException {
		CommandatorRequest request = new CommandatorRequest(commandsList, userCmd);
		Thread t = new Thread(request);
		t.start();
		t.join();
		
		return request.getProposalCommand();
	}
}
