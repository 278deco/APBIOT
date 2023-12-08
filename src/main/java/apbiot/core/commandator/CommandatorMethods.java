package apbiot.core.commandator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import apbiot.core.helper.StringHelper;
import apbiot.core.objects.Tuple;

/**
 * An IA using k-NN algorithm to determine what command the user wanted to write
 * class containing the method
 * @version 1.1
 * @author 278deco
 */
public abstract class CommandatorMethods {

	protected volatile Set<String> commandsList;
	
	protected String getBestProposalCommand(List<Tuple<Integer, String>> knn1, List<Tuple<Integer, String>> knn2) {
		if(knn1.size() < 1 || knn2.size() < 1) return "";
		
		final String firstCmdKnn1 = knn1.get(0).getValueB();
		final String firstCmdKnn2 = knn2.get(0).getValueB();
		
		if(firstCmdKnn1.equals(firstCmdKnn2) && (knn1.get(0).getValueA() > 1 && knn2.get(0).getValueA() > 1)) {
			return firstCmdKnn1;
		}else {
			for(Tuple<Integer,String> tuple : knn2) {
				if(tuple.getValueB().equals(firstCmdKnn1) && (tuple.getValueA() > 1 && knn1.get(0).getValueA() > 1)) return firstCmdKnn1;
			}
			
			for(Tuple<Integer,String> tuple : knn1) {
				if(tuple.getValueB().equals(firstCmdKnn2) && (tuple.getValueA() > 1 && knn2.get(0).getValueA() > 1)) return firstCmdKnn2;
			}
			
			return "";
		}
	}
	
	protected List<Tuple<Integer, String>> knnLetterInCommon(String userCmd, int k) {
		final List<Tuple<Integer, String>> kList = letterInCommon(userCmd);
		
		return k > kList.size() ? kList : new ArrayList<>(kList.subList(0, k));
	}
	
	protected List<Tuple<Integer, String>> knnLetterSamePlace(String userCmd, int k) {
		final List<Tuple<Integer, String>> kList = letterSamePlace(userCmd);
		
		return k > kList.size() ? kList : new ArrayList<>(kList.subList(0, k));
	}
	
	protected List<Tuple<Integer, String>> letterInCommon(String userCmd) {
		final List<Tuple<Integer, String>> rList = new ArrayList<>();
		
		for (String cmd : commandsList) {
			rList.add(Tuple.of(numberOfLetterInWords(userCmd, cmd), cmd));
		}
		
		rList.sort((t1, t2) -> { return t2.getValueA() - t1.getValueA(); });
		return rList;
	}
	
	protected List<Tuple<Integer, String>> letterSamePlace(String userCmd) {
		final List<Tuple<Integer, String>> rList = new ArrayList<>();
		
		for (String cmd : commandsList) {
			rList.add(Tuple.of(numberOfLetterSamePlace(cmd, userCmd), cmd));
		}
		
		rList.sort((t1, t2) -> { return t2.getValueA() - t1.getValueA(); });
		return rList;
	}
	
	protected Set<String> compareCommandSize(String userCmd) {
		final Set<String> rList = new HashSet<>();
		
		this.commandsList.forEach(cmd -> {
			if(cmd.length() >= (userCmd.length() - 2) && cmd.length() <= (userCmd.length() + 2)) rList.add(cmd);
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
