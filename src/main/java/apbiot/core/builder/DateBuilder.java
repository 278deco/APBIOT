package apbiot.core.builder;

import java.time.Duration;
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
	 * @param timeZone The time zone which will be used by the date
	 */
	public DateBuilder(ZoneId timeZone) {
		this.date = ZonedDateTime.now(timeZone);
	}
	
	/**
	 * Create a new instance of DateBuilder
	 * @param date A defined LocalDate
	 * @see java.time.ZonedDateTime
	 */
	public DateBuilder(ZonedDateTime date) {
		this.date = date;
	}
	
	/**
	 * Create a new instance of DateBuilder
	 * @param dateString A date contained into a String
	 * @param dateFormat The date format contained in the String
	 * @see java.time.format.DateTimeFormatter
	 */
	public DateBuilder(String dateString, DateTimeFormatter dateFormat) {
		this.date = ZonedDateTime.parse(dateString, dateFormat);
	}
	
	public EditableTimeBuilder toEditable() {
		return new EditableTimeBuilder(getDate());
	}
	
	/**
	 * Get the date formatted with pattern dd^MM^yyyy where ^ is the separator parameter if {@code lg == Locale.FRENCH}<br>
	 * Get the date formatted with pattern yyyy^MM^dd where ^ is the separator parameter else
	 * @param lg The language used to format the date (position of the month, year and day)
	 * @param separator The character between the date
	 * @return the formatted date
	 * @deprecated since 4.0
	 */
	public String getFormattedDate(Locale lg, char separator) {
		return lg == Locale.FRENCH ? date.format(DateTimeFormatter.ofPattern("dd"+separator+"MM"+separator+"yyyy")) : date.format(DateTimeFormatter.ofPattern("yyyy"+separator+"MM"+separator+"dd"));
	}
	
	/**
	 * Format the date contained in this instance of {@link DateBuilder} using the given {@link DateTimeFormatter}
	 * @param formatter The given formatter
	 * @return the newly formatted date
	 */
	public String getFormattedDate(DateTimeFormatter formatter) {
		return this.date.format(formatter);
	}
	
	/**
	 * Get the date formatted with pattern yyyy-MM-dd
	 * @return the formatted date
	 */
	public String getFormattedDate() {
		return getFormattedDate('-', false);
	}
	
	/**
	 * Get the date formatted with pattern yyyy-MM-dd if {@code reverseYearDay == false}<br>
	 * Get the date formatted with pattern dd-MM-yyyy else
	 * @return the formatted date
	 */
	public String getFormattedDate(boolean reverseYearDay) {
		return getFormattedDate('-', reverseYearDay);
	}
	
	/**
	 * Get the date formatted with pattern yyyy^MM^dd where ^ is the separator parameter if {@code reverseYearDay == false}<br>
	 * Get the date formatted with pattern dd^MM^yyyy where ^ is the separator parameter else
	 * @param separator The character between the date
	 * @return the formatted date
	 */
	public String getFormattedDate(char separator, boolean reverseYearDay) {
		return reverseYearDay ? date.format(DateTimeFormatter.ofPattern("dd"+separator+"MM"+separator+"yyyy")) : date.format(DateTimeFormatter.ofPattern("yyyy"+separator+"MM"+separator+"dd"));
	}
	
	/**
	 * Get the date formatted without the year with pattern MM-dd
	 * @return the formatted date
	 */
	public String getFormattedDateWithoutYear() {
		return date.format(DateTimeFormatter.ofPattern("MM-dd"));
	}
	
	/**
	 * Get the time formatted with pattern HH:mm:ss
	 * @return the formatted time
	 */
	public String getFormattedTime() {
		return date.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	
	/**
	 * Get the date formatted with the time formatted
	 * @return the formatted date and time
	 * @see #getFormattedDate()
	 * @see #getFormattedTime()
	 */
	public String getFormattedDateTime() {
		return getFormattedDate()+" "+getFormattedTime();
	}
	
	/**
	 * Get the date formatted to be human readable<br>
	 * Display the month with his name
	 * @param language The language used to format
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
	 * @param month The number of the year to be compared
	 * @return if the year are equals
	 */
	public boolean compareYear(int year) {
		return date.getYear() == year;
	}
	
	/**
	 * Compare the month with the date's month of this instance
	 * @param month The number of the month to be compared
	 * @return if the months are equals
	 */
	public boolean compareMonth(int month) {
		return date.getMonthValue() == month;
	}
	
	/**
	 * Compare the day with the date's day of this instance
	 * @param day The day to be compared
	 * @return if the days are equals
	 */
	public boolean compareDay(int day) {
		return date.getDayOfMonth() == day;
	}
	
	/**
	 * Compare the hour with the time's hour of this instance
	 * @param hour The hour to be compared
	 * @return if the hours are equals
	 */
	public boolean compareHour(int hour) {
		return date.getHour() == hour;
	}
	
	/**
	 * Compare the minute with the time's minute of this instance
	 * @param minute The minute to be compared
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
	 * @param builder The date to be compared
	 * @return if the dates are equals
	 */
	public boolean isDateEqual(DateBuilder builder) {
		return this.compareYear(builder.getDate().getYear()) && this.compareMonth(builder.getDate().getMonthValue()) && this.compareDay(builder.date.getDayOfMonth());
	}
	
	public boolean isZonedDateTimeEqual(DateBuilder builder) {
		return this.date.isEqual(builder.getDate());
	}
	
	/**
	 * Compare a date with the date of this instance<br>
	 * This function will only compare the month and the day, not caring about year
	 * @param builder The date to be compared
	 * @return if the dates are equals
	 */
	public boolean isDateEqualIgnoringYear(DateBuilder builder) {
		return this.compareMonth(builder.getDate().getMonthValue()) && this.compareDay(builder.date.getDayOfMonth());
	}
	
	/**
	 * Compare if the date contained in this instance of {@link DateBuilder} is before the given date
	 * @param dateInstance The date to be compared
	 * @return If this date's instance is before the given date
	 */
	public boolean isDateBefore(DateBuilder dateInstance) {
		return this.getDate().isBefore(getDate());
	}
	
	/**
	 * Tell if the date contained in this instance of {@link DateBuilder} is before the current date
	 * @return If this date is before current date
	 */
	public boolean isDateBeforeCurrentDate() {
		return getDate().isBefore(ZonedDateTime.now(getDate().getZone()));
	}
	
	/**
	 * Compare if the date contained in this instance of {@link DateBuilder} is after the given date
	 * @param dateInstance The date to be compared
	 * @return If this date's instance is after the given date
	 */
	public boolean isDateAfter(DateBuilder dateInstance) {
		return this.getDate().isAfter(dateInstance.getDate());
	}
	
	/**
	 * Tell if the date contained in this instance of {@link DateBuilder} is after the current date
	 * @return If this date is after current date
	 */
	public boolean isDateAfterCurrentDate() {
		return getDate().isAfter(ZonedDateTime.now(getDate().getZone()));
	}
	
	/**
	 * Get the interval between the date contained in {@link DateBuilder} instance and the given date<br>
	 * The comparison takes place with a specific {@link ChronoUnit} and the result will be the interval between dates' given unit
	 * @param builder The end date
	 * @param unit The unit used in the comparison
	 * @return a interval between the two dates
	 */
	public Long getIntervalBetweenDates(DateBuilder builder, ChronoUnit unit) {
		return unit.between(getDate(), builder.getDate());
	}
	
	/**
	 * Compare the duration between the date contained in {@link DateBuilder} instance and the given date using {@link Duration} class
	 * @param compared The date which will be compared to this instance's one
	 * @return The duration between the two dates
	 * @throws ArithmeticException
	 */
	public Duration getDurationBetweenDates(DateBuilder compared) throws ArithmeticException {
		return Duration.between(getDate(), compared.getDate());
	}
}
