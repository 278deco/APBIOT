package apbiot.core.commandator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import apbiot.core.objects.Tuple;

/**
 * A pseudo-AI using k-NN algorithm to determine what command the user wanted to write
 * Request class
 * @author 278deco
 * @version 1.1.0
 */
public class CommandatorRequest extends CommandatorMethods implements Runnable {

	private volatile Optional<CommandatorEntry> requestedCommand;
	private volatile String userCommand;
	
	public CommandatorRequest(Set<CommandatorEntry> cmdList, String userCommand) {
		this.commandsList = cmdList;
		this.userCommand = userCommand;
	}
	
	@Override
	public void run() {
		commandsList = compareCommandSize(userCommand);
		
		List<Tuple<Integer, CommandatorEntry>> letterInCommon = knnLetterInCommon(userCommand, 3);
		List<Tuple<Integer, CommandatorEntry>> letterAtSamePlace = knnLetterSamePlace(userCommand, 3);
		
		requestedCommand = getBestProposalCommand(letterInCommon, letterAtSamePlace);
	}
	
	public Optional<CommandatorEntry> getProposalCommand() {
		return requestedCommand;
	}
	
	public boolean isProposalCommandPresent() {
		return requestedCommand.isPresent();
	}
}
