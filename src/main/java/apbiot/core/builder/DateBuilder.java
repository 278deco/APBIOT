package apbiot.core.builder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DateBuilder {
	protected ZonedDateTime date;
	
	/**
	 * Create a new instance of DateBuilder
	 */
	public DateBuilder(ZoneId timeZone) {
		this.date = ZonedDateTime.now(timeZone);
	}
	
	/**
	 * Create a new instance of DateBuilder
	 * @param date - a defined LocalDate
	 * @see java.time.ZonedDateTime
	 */
	public DateBuilder(ZonedDateTime date) {
		this.date = date;
	}
	
	/**
	 * Create a new instance of DateBuilder
	 * @param dateString - a date contained into a String
	 * @param dateFormat - the date format contained in the String
	 * @see java.time.format.DateTimeFormatter
	 */
	public DateBuilder(String dateString, DateTimeFormatter dateFormat) {
		this.date = ZonedDateTime.parse(dateString, dateFormat);
	}
	
	/**
	 * Get the date formatted
	 * @param language - the language used to format the date (position of the month, year and day)
	 * @param separator - the character between the date
	 * @return the formatted date
	 */
	public String getFormattedDate(Locale lg, char separator) {
		return lg == Locale.FRENCH ? date.format(DateTimeFormatter.ofPattern("dd"+separator+"MM"+separator+"yyyy")) : date.format(DateTimeFormatter.ofPattern("yyyy"+separator+"MM"+separator+"dd"));
	}
	
	/**
	 * Get the date formatted
	 * @return the formatted date
	 */
	public String getFormattedDate() {
		return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	/**
	 * Get the date formatted
	 * @param separator - the character between the date
	 * @return the formatted date
	 */
	public String getFormattedDate(char separator) {
		return date.format(DateTimeFormatter.ofPattern("yyyy"+separator+"MM"+separator+"dd"));
	}
	
	/**
	 * Get the date formatted without the year
	 * @return the formatted date
	 */
	public String getFormattedDateWithoutYear() {
		return date.format(DateTimeFormatter.ofPattern("MM-dd"));
	}
	
	/**
	 * Get the time formatted
	 * @return the formatted time
	 */
	public String getFormattedTime() {
		return date.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	
	/**
	 * Get the date formatted with the time formatted
	 * @return the formatted date and time
	 */
	public String getFormattedDateTime() {
		return getFormattedDate()+" "+getFormattedTime();
	}
	
	/**
	 * Get the date formatted to be human readable
	 * Display the month with his name
	 * @param language - the language used to format
	 * @return the formatted date
	 */
	public String getHumanDateFormat(Locale language) {
		return getDay()+" "+date.getMonth().getDisplayName(TextStyle.FULL, language)+" "+getYear();
	}
	
	/**
	 * Get the year 
	 * @return the year
	 */
	public String getYear() {
		return date.format(DateTimeFormatter.ofPattern("yyyy"));
	}
	
	/**
	 * Get the mouth 
	 * @return the mouth
	 */
	public String getMonth() {
		return date.format(DateTimeFormatter.ofPattern("MM"));
	}
	
	/**
	 * Get the day 
	 * @return the day
	 */
	public String getDay() {
		return date.format(DateTimeFormatter.ofPattern("dd"));
	}
	
	/**
	 * Get the hour 
	 * @return the hour
	 */
	public String getHour() {
		return date.format(DateTimeFormatter.ofPattern("HH"));
	}
	
	/**
	 * Get the minute 
	 * @return the minute
	 */
	public String getMinute() {
		return date.format(DateTimeFormatter.ofPattern("mm"));
	}
	
	/**
	 * Get the second 
	 * @return the second
	 */
	public String getSecond() {
		return date.format(DateTimeFormatter.ofPattern("ss"));
	}
	
	/**
	 * Compare the year with the date's year of this instance
	 * @param month - the number of the year to be compared
	 * @return if the year are equals
	 */
	public boolean compareYear(int year) {
		return date.getYear() == year;
	}
	
	/**
	 * Compare the month with the date's month of this instance
	 * @param month - the number of the month to be compared
	 * @return if the months are equals
	 */
	public boolean compareMonth(int month) {
		return date.getMonthValue() == month;
	}
	
	/**
	 * Compare the day with the date's day of this instance
	 * @param day - the day to be compared
	 * @return if the days are equals
	 */
	public boolean compareDay(int day) {
		return date.getDayOfMonth() == day;
	}
	
	/**
	 * Compare the hour with the time's hour of this instance
	 * @param hour - the hour to be compared
	 * @return if the hours are equals
	 */
	public boolean compareHour(int hour) {
		return date.getHour() == hour;
	}
	
	/**
	 * Compare the minute with the time's minute of this instance
	 * @param minute - the minute to be compared
	 * @return if the minutes are equals
	 */
	public boolean compareMinute(int minute) {
		return date.getMinute() == minute;
	}
	
	/**
	 * Get the ZonedDateTime contained in this instance
	 * @return the ZonedDateTime
	 */
	protected ZonedDateTime getDate() {
		return this.date;
	}
	
	/**
	 * Get the current time zone used
	 * @return the time zone
	 */
	public ZoneId getTimeZone() {
		return this.date.getZone();
	}
	
	/**
	 * Get the formatted current time zone used
	 * @return the time zone
	 */
	public String getTimeZoneName(Locale lang) {
		return this.date.getZone().getDisplayName(TextStyle.FULL, lang);
	}
	
	/**
	 * Compare a date with the date of this instance
	 * @param builder - the date to be compared
	 * @return if the dates are equals
	 */
	public boolean isDateEqual(DateBuilder builder) {
		return this.compareYear(builder.getDate().getYear()) && this.compareMonth(builder.getDate().getMonthValue()) && this.compareDay(builder.date.getDayOfMonth());
	}
	
	public boolean isZonedDateTimeEqual(DateBuilder builder) {
		return this.date.isEqual(builder.getDate());
	}
	
	/**
	 * Compare a date with the date of this instance
	 * This function will only compare the month and the day, not caring about year
	 * @param builder - the date to be compared
	 * @return if the dates are equals
	 */
	public boolean isDateEqualIgnoringYear(DateBuilder builder) {
		return this.compareMonth(builder.getDate().getMonthValue()) && this.compareDay(builder.date.getDayOfMonth());
	}
	
	/**
	 * Compare if a date is before the date of this instance
	 * @param builder - the date to be compared
	 * @return if the date is before this date
	 */
	public boolean isDateBefore(DateBuilder builder) {
		return builder.getDate().isBefore(getDate());
	}
	
	/**
	 * Compare if a date is after the date of this instance
	 * @param builder - the date to be compared
	 * @return if the date is after this date
	 */
	public boolean isDateAfter(DateBuilder builder) {
		return builder.getDate().isAfter(getDate());
	}
	
	/**
	 * Tell if the date is after the current time
	 * @return if the date is after
	 */
	public boolean isDateAfterNow() {
		return getDate().isAfter(ZonedDateTime.now(getDate().getZone()));
	}
	
	/**
	 * Get the interval between this date and an other date
	 * @param builder - the end date
	 * @param unit - the result unit
	 * @return a interval between the two dates
	 */
	public Long getPeriodBetweenDate(DateBuilder builder, ChronoUnit unit) {
		return unit.between(getDate(), builder.getDate());
	}
}
