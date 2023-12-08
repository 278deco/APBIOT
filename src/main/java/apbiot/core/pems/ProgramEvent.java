package apbiot.core.pems;

import java.util.Optional;

public abstract class ProgramEvent {
	
	protected Object[] arguments;
	
	public ProgramEvent(Object[] arguments) {
		this.arguments = arguments;
	}
	
	public final Object getEventArgument(int index) {
		if(index >= 0 && index < this.arguments.length) {
			return this.arguments[index];
		}
		
		return null;
	}
	
	public <E> E getEventArgument(Class<E> castingClass, int index) {
		if(index >= 0 && index < this.arguments.length) {
			try {
				return castingClass.cast(this.arguments[index]);
			}catch(ClassCastException e) {
				return null;
			}
		}
		
		return null;
	}
	
	public final Optional<Object> getEventArgumentAsOptional(int index) {
		if(index >= 0 && index < this.arguments.length) {
			return Optional.ofNullable(this.arguments[index]);
		}
		
		return Optional.empty();
	}
	
	public <E> Optional<E> getEventArgumentAsOptional(Class<E> castingClass, int index) {
		if(index < 0 || index >= this.arguments.length) {
			try {
				return Optional.ofNullable(castingClass.cast(this.arguments[index]));
			}catch(ClassCastException e) {
				return Optional.empty();
			}
		}
		
		return Optional.empty();
	}
	
	public final Object getEventArgumentSize() {
		return this.arguments.length;
	}

	public abstract EventPriority getPriority();
	
	public enum EventPriority {
		
		HIGH,
		INTERMEDIATE,
		LOW;
	}
}
