package apbiot.core.time;

import java.util.concurrent.TimeUnit;

/**
 * A dynamic class that manage time and used to create cooldown
 * @author 278deco
 */
public class Time extends StaticTime {
	
	private Long elapsedTime;
	private boolean hasBeenInit;
	
	private Long sysTime;
	
	/**
	 * create a new timer instance
	 * @param duration - the duration of the timer
	 * @param unit - the unit of the stocked time
	 */
	public Time create(Long duration, TimeUnit unit) {
		this.timeUnit = unit;
		this.durationTime = unit.toNanos(duration);
		this.sysTime = System.nanoTime();
		this.elapsedTime = 0L;
		this.hasBeenInit = true;
		return this;
	}
	
	/**
	 * @deprecated cannot recreate the timer like this
	 * @since 2.0
	 * @param unit - the unit of the stocked time
	 * @throws IllegalAccessException 
	 */
	@Deprecated
	public Time create(TimeUnit unit) throws IllegalAccessException {
		if(!this.hasBeenInit) throw new IllegalAccessException("This instance of time hasn't been init");
		
		this.timeUnit = unit;
		this.sysTime = System.nanoTime();
		this.elapsedTime = 0L;
		return this;
	}
	
	/**
	 * Check if the timer is finished
	 * @return boolean if is finished
	 */
	public boolean isFinish() {
		update();
		return elapsedTime > durationTime;
	}
	
	/**
	 * Update the timer (update all the variables)
	 */
	public void update() {
		this.elapsedTime = System.nanoTime() - sysTime;
	}
	
	public Long getStartingTime() {
		return sysTime;
	}
	
	public Long getElapsedTime() {
		return elapsedTime;
	}
	
	public Long getRemainingTime() {
		return (durationTime - elapsedTime);
	}
	
	public void reset() {
		elapsedTime = 0L;
		timeUnit = null;
		sysTime = 0L;
	}
	
	/**
	 * Subtract a specified time to the stocked time in this instance
	 * @param duration - the duration you want to substract
	 * @param unit - the unit of the duration
	 */
	public void subtractTime(Long duration, TimeUnit unit) {
		if(unit == this.timeUnit) {
			this.durationTime -= duration;
		}
	}
	
	/**
	 * Subtract a specified time to the stocked time in this instance
	 * @param time - a Time instance
	 */
	public void subtractTime(Time time) {
		if(time.getTimeUnit() == this.timeUnit) {
			this.durationTime -= time.getConvertedDurationTime();
		}
	}
	
	/**
	 * Add a specified time to the stocked time in this instance
	 * @param duration - the duration you want to add
	 * @param unit - the unit of the duration
	 */
	public void addTime(Long duration, TimeUnit unit) {
		if(unit == this.timeUnit) {
			this.durationTime += duration;
		}
	}
	
	/**
	 * Add a specified time to the stocked time in this instance
	 * @param time - a Time instance
	 */
	public void addTime(Time time) {
		if(time.getTimeUnit() == this.timeUnit) {
			this.durationTime += time.getConvertedDurationTime();
		}
	}
	
}
