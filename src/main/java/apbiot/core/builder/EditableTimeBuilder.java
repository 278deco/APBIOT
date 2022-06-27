package apbiot.core.builder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
	public EditableTimeBuilder addHour(int hour) {
		this.date = this.date.plusHours(hour);
		return this;
	}
	
	/**
	 * Increase the number of minutes
	 * @param hour - the number of minutes to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder addMinute(int minutes) {
		this.date = this.date.plusMinutes(minutes);
		return this;
	}
	
	/**
	 * Increase the number of seconds
	 * @param hour - the number of seconds to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder addSecond(int seconds) {
		this.date = this.date.plusSeconds(seconds);
		return this;
	}
	
	/**
	 * Increase the number of years
	 * @param hour - the number of years to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder addYear(int year) {
		this.date = this.date.plusYears(year);
		return this;
	}
	
	/**
	 * Increase the number of months
	 * @param hour - the number of months to be added
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder addMonth(int mouth) {
		this.date = this.date.plusMonths(mouth);
		return this;
	}
	
	/**
	 * Decrease the number of hours
	 * @param hour - the number of hours to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder removeHour(int hour) {
		this.date = this.date.minusHours(hour);
		return this;
	}
	
	/**
	 * Decrease the number of minutes
	 * @param hour - the number of minutes to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder removeMinute(int minutes) {
		this.date = this.date.minusMinutes(minutes);
		return this;
	}
	
	/**
	 * Decrease the number of seconds
	 * @param hour - the number of seconds to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder removeSecond(int seconds) {
		this.date = this.date.minusSeconds(seconds);
		return this;
	}
	
	/**
	 * Decrease the number of years
	 * @param hour - the number of years to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder removeYear(int year) {
		this.date = this.date.minusYears(year);
		return this;
	}
	
	/**
	 * Decrease the number of months
	 * @param hour - the number of months to be decreased
	 * @return an instance of EditableTimeBuilder
	 */
	public EditableTimeBuilder removeMonth(int mouth) {
		this.date = this.date.minusMonths(mouth);
		return this;
	}
 
}
