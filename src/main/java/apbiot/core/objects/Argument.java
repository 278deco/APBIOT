package apbiot.core.objects;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.objects.enums.ArgumentLevel;
import apbiot.core.objects.enums.ArgumentType;

public class Argument {
	
	private List<String> argName;
	private String argDescription;
	private ArgumentLevel argLvl;
	private ArgumentType argType;
	
	public Argument(String name, String description, ArgumentLevel level, ArgumentType type) {
		this.argName = new ArrayList<>();
		this.argName.add(name);
		this.argLvl = level;
		this.argType = type;
		this.argDescription = description;
	}
	
	public Argument(List<String> nameList, String description, ArgumentLevel level, ArgumentType type) {
		this.argName = nameList;
		this.argLvl = level;
		this.argType = type;
		this.argDescription = description;
	}
	
	public String getPrincipalName() {
		return argName.size() > 0 ? argName.get(0) : "null";
	}
	
	public List<String> getNames() {
		return argName;
	}
	
	public boolean haveMultipleName() {
		return argName.size() > 1;
	}
	
	public String getDescription() {
		return argDescription;
	}
	
	public ArgumentLevel getLevel() {
		return argLvl;
	}
	
	public ArgumentType getType() {
		return argType;
	}

}
