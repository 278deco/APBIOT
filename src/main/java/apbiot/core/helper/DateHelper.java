package apbiot.core.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DateHelper {
	
	/**
	 * Get the formatted date time from a String
	 * @param dateTimeString The String containing the date time
	 * @param format The format of the date in the String
	 * @param zone The time zone
	 * @see java.time.ZonedDateTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 * @since 3.0
	 */
	public static ZonedDateTime getFormattedDateTime(String dateTimeString, DateTimeFormatter format, ZoneId zone) {
		final LocalDateTime parse = LocalDateTime.parse(dateTimeString, format);
		 
		return parse.atZone(zone);
	}
	
	/**
	 * Get the formatted date time from a String
	 * @param dateTimeString The String containing the date time
	 * @param format The format of the date in the String
	 * @param zone The time zone
	 * @see java.time.ZonedDateTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 * @since 3.0
	 */
	public static ZonedDateTime getFormattedDateTime(String dateTimeString, String format, ZoneId zone) {
		 return getFormattedDateTime(dateTimeString, DateTimeFormatter.ofPattern(format), zone);
	}
	
	/**
	 * Get the formatted date time from a date String
	 * @param dateString The String containing the date
	 * @param format The format of the date in the String
	 * @param zone The time zone
	 * @see java.time.LocalDate#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 * @since 3.0
	 */
	public static ZonedDateTime getFormattedDate(String dateString, DateTimeFormatter format, ZoneId zone) {
		final LocalDate date = LocalDate.parse(dateString,format);

		return date.atStartOfDay(zone);
	}
	
	/**
	 * Get the formatted date time from a date String
	 * @param dateString The String containing the date
	 * @param format The format of the date in the String
	 * @param zone The time zone
	 * @see java.time.LocalDate#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 * @since 3.0
	 */
	public static ZonedDateTime getFormattedDate(String dateString, String format, ZoneId zone) {
		return getFormattedDate(dateString, DateTimeFormatter.ofPattern(format), zone);
	}
	
	/**
	 * Get the formatted date time from a date String
	 * @param dateString The String containing the date
	 * @param format The format of the date in the String
	 * @param zone The time zone
	 * @see java.time.LocalTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 * @since 3.0
	 */
	public static ZonedDateTime getFormattedTime(String timeString, DateTimeFormatter format, ZoneId zone) {
		final LocalTime date = LocalTime.parse(timeString,format);
		
		return date.atDate(LocalDate.now(zone)).atZone(zone);
	}
	
	/**
	 * Get the formatted date time from a time String
	 * @param dateString The String containing the time
	 * @param format The format of the time in the String
	 * @param zone The time zone
	 * @see java.time.LocalTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 */
	public static ZonedDateTime getFormattedTime(String timeString, String format, ZoneId zone) {
		return getFormattedTime(timeString, DateTimeFormatter.ofPattern(format), zone);
	}
	
	/**
	 * Determine if the provided date match the given {@link DateTimeFormatter}.
	 * @param date The date which will be checked
	 * @param format The format of the date
	 * @param zone The timezone of the given date
	 * @return if the date match the formatter
	 * @since 3.0
	 */
	public static boolean isValidDateFormat(String date, DateTimeFormatter format, ZoneId zone) {
		try {
			getFormattedDate(date, format, zone);
			return true;
		}catch(DateTimeParseException e) {
			return false;
		}
	}
	
	/**
	 * Tell if the string parsed date is after the current time
	 * @param date The date to parse
	 * @param format The date format
	 * @param zone The zone in which the date will be parsed
	 * @return if the date is after now
	 * @since 3.0
	 */
	public static boolean isValidActualDate(String date, DateTimeFormatter format, ZoneId zone) {
		try {
			return getFormattedDate(date, format, zone).isAfter(ZonedDateTime.now(zone));
		}catch(DateTimeParseException e) {
			return false;
		}
	}
	
	public static int timeUnitToCalendarField(TimeUnit unit) {
		switch(unit) {
			case DAYS: return Calendar.DAY_OF_WEEK;
			case HOURS: return Calendar.HOUR_OF_DAY;
			case MINUTES: return Calendar.MINUTE;
			case SECONDS: return Calendar.SECOND;
			case MILLISECONDS: return Calendar.MILLISECOND;
			default: throw new IllegalArgumentException(unit.toString()+" is not handled by calendar class");
		}
	}
	
}
