package apbiot.core.commandator;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.objects.Tuple;

/**
 * An IA using k-NN algorithm to determine what command the user wanted to write
 * class containing the method
 * @author 278deco
 */
public abstract class CommandatorMethods {

	protected volatile List<String> commandsList;
	
	protected String getBestProposalCommand(List<Tuple<Integer, String>> knn1, List<Tuple<Integer, String>> knn2) {
		if(knn1.size() < 1 || knn2.size() < 1) return "";
		
		String firstCmdKnn1 = knn1.get(0).getValueB();
		String firstCmdKnn2 = knn2.get(0).getValueB();
		
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
		List<Tuple<Integer, String>> kList = letterInCommon(userCmd);
		
		if(k > kList.size()) {
			return kList;
		}else {
			List<Tuple<Integer, String>> rList = new ArrayList<>();
			
			for(int i = 0; i < k; i++) {
				rList.add(kList.get(i));
			}
			return rList;
		}
	}
	
	protected List<Tuple<Integer, String>> knnLetterSamePlace(String userCmd, int k) {
		List<Tuple<Integer, String>> kList = letterSamePlace(userCmd);
		
		if(k > kList.size()) {
			return kList;
		}else {
			List<Tuple<Integer, String>> rList = new ArrayList<>();
			
			for(int i = 0; i < k; i++) {
				rList.add(kList.get(i));
			}
			return rList;
		}
	}
	
	protected List<Tuple<Integer, String>> letterInCommon(String userCmd) {
		List<Tuple<Integer, String>> rList = new ArrayList<>();
		
		for (String cmd : commandsList) {
			rList.add(Tuple.of(numberOfLetterInWords(userCmd, cmd), cmd));
		}
		
		rList.sort((t1, t2) -> { return t2.getValueA() - t1.getValueA(); });
		return rList;
	}
	
	protected List<Tuple<Integer, String>> letterSamePlace(String userCmd) {
		List<Tuple<Integer, String>> rList = new ArrayList<>();
		
		for (String cmd : commandsList) {
			rList.add(Tuple.of(numberOfLetterSamePlace(cmd, userCmd), cmd));
		}
		
		rList.sort((t1, t2) -> { return t2.getValueA() - t1.getValueA(); });
		return rList;
	}
	
	protected List<String> compareCommandSize(String userCmd) {
		List<String> rList = new ArrayList<>();
		
		for (String cmd : commandsList) {
			if(cmd.length() >= (userCmd.length() - 2) && cmd.length() <= (userCmd.length() + 2)) rList.add(cmd);
		}
		return rList;
	}
	
	protected int numberOfLetterInWords(String cmd1, String cmd2) {
		int letters = 0;
		
		for (int i = 0; i < cmd1.length(); i++) {
			if(cmd2.contains(String.valueOf(cmd1.charAt(i)))) {
				letters+=1;
				cmd2.replace(cmd1.charAt(i), ' ');
			}
		}		
		return letters;
	}
	
	protected int numberOfLetterSamePlace(String cmd1, String cmd2) {
		int letters = 0;
		
		for(int i = 0; i < cmd1.length(); i++) {
			if(i == cmd2.length()) {
				return letters;
			}else {
				if(cmd1.charAt(i) == cmd2.charAt(i)) letters+=1;
			}
		}
		return letters;
	}
	
}
