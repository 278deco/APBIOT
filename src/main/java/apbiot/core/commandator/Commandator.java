package apbiot.core.commandator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An IA using k-NN algorithm to determine what command the user wanted to write 
 * Main class
 * @author 278deco
 * @see apbiot.core.commandator.CommandatorRequest
 * @see apbiot.core.commandator.CommandatorMethods
 */
public class Commandator {
	
	private List<String> commandsList = new ArrayList<String>();
	
	/**
	 * Create a new instance of Commandator
	 * @param commands - the list of commands
	 */
	public Commandator(List<String> commands) {
		this.commandsList.addAll(commands);
	}
	
	/**
	 * Create a new instance of Commandator
	 * @param nativeCommands - the list of commands
	 * * @param slashCommands - the list of commands
	 */
	public Commandator(List<String> nativeCommands, List<String> slashCommands) {
		this.commandsList.addAll(nativeCommands);
		this.commandsList.addAll(slashCommands);
	}
	
	/**
	 * Create a new instance of Commandator
	 * @param commands - the set of commands
	 */
	public Commandator(Set<List<String>> commands) {
		List<String> cmds = new ArrayList<>();
		for(List<String> lst : commands) {
			cmds.addAll(lst);
		}
		
		this.commandsList = cmds;
	}
	
	/**
	 * Create a new instance of Commandator
	 * @param commands - the set of commands
	 */
	public Commandator(Set<List<String>> nativeCommands, Set<List<String>> slashCommands) {
		for(List<String> lst : nativeCommands) {
			this.commandsList.addAll(lst);
		}
		
		for(List<String> lst : slashCommands) {
			this.commandsList.addAll(lst);
		}
	}
	
	/**
	 * Create a new instance of Commandator
	 * @param commands - the map of commands
	 */
	public Commandator(Map<List<String>, AbstractMethodError> commands) {
		for(List<String> lst : commands.keySet()) {
			commandsList.add(lst.get(0));
		}
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
