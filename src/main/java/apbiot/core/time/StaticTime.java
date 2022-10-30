package apbiot.core.time;

import apbiot.core.MainInitializer;

/**
 * A static class used to stored a time with an unit
 * To create a dynamic timer / cooldown use {@link apbiot.core.time.Time}
 * @author 278deco
 */
public class StaticTime {

	public enum TimeUnit {
		
		NULL("null", 0, -1),
		NANOSECOND("ns", 1, 0),
		MILISECOND("us", 2, 1000000),
		SECOND("s",3, 1000),
		HOUR("h",4, 3600),
		DAY("h",5, 24),
		WEEK("w",6, 7);
		
		private String name;
		private int index;
		private int operation;
		private TimeUnit(String name, int i, int ope) {
			this.name = name;
			this.index = i;
			this.operation = ope;
		}
		
		public static TimeUnit of(String name) {
			for(TimeUnit unit : TimeUnit.values()) {
				if(unit.getName().equalsIgnoreCase(name)) return unit;
			}
			
			return NULL;
		}
		
		public static TimeUnit of(int index) {
			for(TimeUnit unit : TimeUnit.values()) {
				if(unit.getIndex() == index) return unit;
			}
			
			return NULL;
		}
		
		public String getName() {
			return name;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public int getOperation() {
			return this.operation;
		}
	}
	
	protected Long durationTime;
	protected Long initalDurationTime;
	protected TimeUnit timeUnit;
	protected boolean hasBeenInit;
	
	/**
	 * create a new static timer instance
	 * @param duration - the duration of the timer
	 * @param unit - the unit of the stocked time
	 */
	public StaticTime create(Long duration, TimeUnit unit) {
		this.timeUnit = unit;
		this.initalDurationTime = duration;
		this.durationTime = convertTimeUnit(duration, TimeUnit.NANOSECOND);
		this.hasBeenInit = true;
		return this;
	}
	
	/**
	 * @deprecated cannot recreate the timer like this
	 * @since 2.0
	 * @param unit - the unit of the stocked time
	 */
	public StaticTime create(TimeUnit unit) {
		if(!this.hasBeenInit) {
			MainInitializer.LOGGER.warn("Unexpected error while managing time",new IllegalAccessException("This instance of time hasn't been init"));
			return this;
		}
		this.timeUnit = unit;
		return this;
	}
	
	/**
	 * Convert a time in a new unit (seconds to hours, weeks to days)
	 * @param toConvert - the variable which contains the "time"
	 * @param newUnit - the unit of the output
	 * @return a converted time in the an unit we want
	 * @see apbiot.core.time.StaticTime.TimeUnit
	 */
	public Long convertTimeUnit(Long toConvert, TimeUnit newUnit) {
		long temporary = toConvert;
		if(timeUnit.getIndex() == -1 || timeUnit.getIndex() == -1) return 0L;
		
		int i = timeUnit.getIndex();
		while(i != newUnit.getIndex()) {
			if(isGreaterThanCurrentUnit(newUnit)) {
				if(i+1 < TimeUnit.values().length)
					temporary/=TimeUnit.values()[i+1].getOperation();
				i+=1;
			}else {
				temporary*=TimeUnit.values()[i].getOperation();
				
				i-=1;
			}
		}
		return temporary;
	}
	
	/**
	 * Check if a unit is "greater" than another (seconds is greater than milliseconds, days are smaller than weeks)
	 * @param old - the unit we want to compare
	 * @param compared - the comparator
	 * @return boolean if the comparator is greater than the compare
	 * @see apbiot.core.time.StaticTime.TimeUnit
	 */
	protected boolean isGreaterThanCurrentUnit(TimeUnit compared) {
		return compared.getIndex() > timeUnit.getIndex();
	}
	
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	
	public Long getConvertedDurationTime() {
		return durationTime;
	}
	
	public Long getInitialDurationTime() {
		return initalDurationTime;
	}
	
	public boolean equals(Time comparedTime) {
		return (comparedTime.getConvertedDurationTime() == durationTime && comparedTime.getTimeUnit() == timeUnit);
	}
	
}
