package apbiot.core.commandator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import apbiot.core.helper.StringHelper;
import apbiot.core.objects.Tuple;

/**
 * A pseudo-AI using k-NN algorithm to determine what command the user wanted to write
 * class containing the method
 * @version 1.1.0
 * @author 278deco
 */
public abstract class CommandatorMethods {

	protected volatile Set<CommandatorEntry> commandsList;
	
	protected Optional<CommandatorEntry> getBestProposalCommand(List<Tuple<Integer, CommandatorEntry>> knn1, List<Tuple<Integer, CommandatorEntry>> knn2) {
		if(knn1.size() < 1 || knn2.size() < 1) return Optional.empty();
		
		final CommandatorEntry firstCmdKnn1 = knn1.get(0).getValueB();
		final CommandatorEntry firstCmdKnn2 = knn2.get(0).getValueB();
		
		if(firstCmdKnn1.equals(firstCmdKnn2) && (knn1.get(0).getValueA() > 1 && knn2.get(0).getValueA() > 1)) {
			return Optional.of(firstCmdKnn1);
		}else {
			for(Tuple<Integer,CommandatorEntry> tuple : knn2) {
				if(tuple.getValueB().equals(firstCmdKnn1) && (tuple.getValueA() > 1 && knn1.get(0).getValueA() > 1)) return Optional.of(firstCmdKnn1);
			}
			
			for(Tuple<Integer,CommandatorEntry> tuple : knn1) {
				if(tuple.getValueB().equals(firstCmdKnn2) && (tuple.getValueA() > 1 && knn2.get(0).getValueA() > 1)) return Optional.of(firstCmdKnn2);
			}
			
			return Optional.empty();
		}
	}
	
	protected List<Tuple<Integer, CommandatorEntry>> knnLetterInCommon(String userCmd, int k) {
		final List<Tuple<Integer, CommandatorEntry>> kList = letterInCommon(userCmd);
		
		return k > kList.size() ? kList : new ArrayList<>(kList.subList(0, k));
	}
	
	protected List<Tuple<Integer, CommandatorEntry>> knnLetterSamePlace(String userCmd, int k) {
		final List<Tuple<Integer, CommandatorEntry>> kList = letterSamePlace(userCmd);
		
		return k > kList.size() ? kList : new ArrayList<>(kList.subList(0, k));
	}
	
	protected List<Tuple<Integer, CommandatorEntry>> letterInCommon(String userCmd) {
		final List<Tuple<Integer, CommandatorEntry>> rList = new ArrayList<>();
		
		for (CommandatorEntry entry : commandsList) {
			rList.add(Tuple.of(numberOfLetterInWords(userCmd, entry.getCommandName()), entry));
		}
		
		rList.sort((t1, t2) -> { return t2.getValueA() - t1.getValueA(); });
		return rList;
	}
	
	protected List<Tuple<Integer, CommandatorEntry>> letterSamePlace(String userCmd) {
		final List<Tuple<Integer, CommandatorEntry>> rList = new ArrayList<>();
		
		for (CommandatorEntry entry : commandsList) {
			rList.add(Tuple.of(numberOfLetterSamePlace(entry.getCommandName(), userCmd), entry));
		}
		
		rList.sort((t1, t2) -> { return t2.getValueA() - t1.getValueA(); });
		return rList;
	}
	
	protected Set<CommandatorEntry> compareCommandSize(String userCmd) {
		final Set<CommandatorEntry> rList = new HashSet<>();
		
		this.commandsList.forEach(entry -> {
			if(entry.getCommandName().length() >= (userCmd.length() - 2) && entry.getCommandName().length() <= (userCmd.length() + 2)) rList.add(entry);
		});
	
		return rList;
	}
	
	public static int numberOfLetterInWords(String compared, String comparator) {
		compared = StringHelper.getRawCharacterString(compared);
		comparator = StringHelper.getRawCharacterString(comparator);
		int letters = 0;
		
		for(char c : compared.toCharArray()) {
			final int i = comparator.indexOf(c);
			if(i != -1) {
				comparator.replace(c, ' ');
				letters+=1;
			}
			
		}	
		return letters;
	}
	
	protected static int numberOfLetterSamePlace(String compared, String comparator) {
		compared = StringHelper.getRawCharacterString(compared);
		comparator = StringHelper.getRawCharacterString(comparator);
		int letters = 0;
		
		for(int i = 0; i < compared.length(); i++) {
			if(i == comparator.length()) {
				return letters;
			}else {
				letters+= (compared.charAt(i) == comparator.charAt(i)) ? 1 : 0;
			}
		}
		return letters;
	}
	
}
