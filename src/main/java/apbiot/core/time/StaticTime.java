package apbiot.core.time;

import java.util.concurrent.TimeUnit;

/**
 * A static class used to stored a time with an unit
 * To create a dynamic timer / cooldown use {@link apbiot.core.time.Time}
 * @author 278deco
 */
public class StaticTime {
	
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
		this.durationTime = unit.toNanos(duration);
		this.hasBeenInit = true;
		return this;
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
