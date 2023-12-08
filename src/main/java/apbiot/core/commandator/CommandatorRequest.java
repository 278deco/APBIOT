package apbiot.core.commandator;

import java.util.List;
import java.util.Set;

import apbiot.core.objects.Tuple;

/**
 * An IA using k-NN algorithm to determine what command the user wanted to write
 * Request class
 * @author 278deco
 */
public class CommandatorRequest extends CommandatorMethods implements Runnable {

	private volatile String requestedCommand;
	private volatile String userCommand;
	
	public CommandatorRequest(Set<String> cmdList, String userCommand) {
		this.commandsList = cmdList;
		this.userCommand = userCommand;
	}
	
	@Override
	public void run() {
		commandsList = compareCommandSize(userCommand);
		
		List<Tuple<Integer, String>> letterInCommon = knnLetterInCommon(userCommand, 3);
		List<Tuple<Integer, String>> letterAtSamePlace = knnLetterSamePlace(userCommand, 3);
		
		requestedCommand = getBestProposalCommand(letterInCommon, letterAtSamePlace);
	}
	
	public String getProposalCommand() {
		return requestedCommand;
	}
}
