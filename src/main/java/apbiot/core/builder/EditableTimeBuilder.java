package apbiot.core.builder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import apbiot.core.objects.Tuple;

public class EditableTimeBuilder extends DateBuilder {

	/**
	 * Create a new instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder(ZoneId timeZone) {
		super(timeZone);
	}
	
	/**
	 * Create a new instance of EditableTimeBuilder
	 * @param date - a defined LocalDate
	 * @see java.time.ZonedDateTime
	 */
	public EditableTimeBuilder(ZonedDateTime date) {
		super(date);
	}
	
	/**
	 * Create a new instance of EditableTimeBuilder
	 * @param dateString - a date contained into a String
	 * @param dateFormat - the date format contained in the String
	 * @see java.time.format.DateTimeFormatter
	 */
	public EditableTimeBuilder(String dateString, DateTimeFormatter dateFormat) {
		super(dateString, dateFormat);
	}
	
	/**
	 * Increase the number of hours
	 * @param hour - the number of hours to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder addHour(int hour) {
		this.date = this.date.plusHours(hour);
		return this;
	}
	
	/**
	 * Increase the number of minutes
	 * @param hour - the number of minutes to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder addMinute(int minutes) {
		this.date = this.date.plusMinutes(minutes);
		return this;
	}
	
	/**
	 * Increase the number of seconds
	 * @param hour - the number of seconds to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder addSecond(int seconds) {
		this.date = this.date.plusSeconds(seconds);
		return this;
	}
	
	/**
	 * Increase the number of years
	 * @param hour - the number of years to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder addYear(int year) {
		this.date = this.date.plusYears(year);
		return this;
	}
	
	/**
	 * Increase the number of months
	 * @param hour The number of months to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder addMonth(int mouth) {
		this.date = this.date.plusMonths(mouth);
		return this;
	}
	
	/**
	 * Increase a field of the time depending on the {@link TimeUnit}
	 * @param value The number to be added to the field
	 * @param valueUnit The field which will be incremented
	 * @return an instance of {@link EditableTimeBuilder}
	 */
	public synchronized EditableTimeBuilder add(int value, TimeUnit valueUnit) {
		this.date = this.date.plus(value, valueUnit.toChronoUnit());
		return this;
	}
	
	/**
	 * Increase a field of the time depending on the {@link TimeUnit}
	 * @param values A tuple containing the number to be added to the field and the field  which will be incremented
	 * @return an instance of {@link EditableTimeBuilder}
	 */
	public synchronized EditableTimeBuilder add(Tuple<Integer, TimeUnit> values) {
		this.date = this.date.plus(values.getValueA(), values.getValueB().toChronoUnit());
		return this;
	}
	
	/**
	 * Decrease the number of hours
	 * @param hour The number of hours to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder removeHour(int hour) {
		this.date = this.date.minusHours(hour);
		return this;
	}
	
	/**
	 * Decrease the number of minutes
	 * @param hour - the number of minutes to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder removeMinute(int minutes) {
		this.date = this.date.minusMinutes(minutes);
		return this;
	}
	
	/**
	 * Decrease the number of seconds
	 * @param hour - the number of seconds to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder removeSecond(int seconds) {
		this.date = this.date.minusSeconds(seconds);
		return this;
	}
	
	/**
	 * Decrease the number of years
	 * @param hour - the number of years to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder removeYear(int year) {
		this.date = this.date.minusYears(year);
		return this;
	}
	
	/**
	 * Decrease the number of months
	 * @param hour - the number of months to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public synchronized EditableTimeBuilder removeMonth(int mouth) {
		this.date = this.date.minusMonths(mouth);
		return this;
	}
 
}
