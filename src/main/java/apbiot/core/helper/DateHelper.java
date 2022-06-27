package apbiot.core.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateHelper {
	
	/**
	 * Get the formatted date time from a String
	 * @param dateTimeString - the String containing the date time
	 * @param format - the format of the date in the String
	 * @param zone - the time zone
	 * @see java.time.ZonedDateTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 */
	public static ZonedDateTime getFormattedDateTime(String dateTimeString, DateTimeFormatter format, ZoneId zone) {
		 LocalDateTime parse = LocalDateTime.parse(dateTimeString, format);
		 
		 return parse.atZone(zone);
	}
	
	/**
	 * Get the formatted date time from a String
	 * @param dateTimeString - the String containing the date time
	 * @param format - the format of the date in the String
	 * @param zone - the time zone
	 * @see java.time.ZonedDateTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 */
	public static ZonedDateTime getFormattedDateTime(String dateTimeString, String format, ZoneId zone) {
		 return getFormattedDateTime(dateTimeString, DateTimeFormatter.ofPattern(format), zone);
	}
	
	/**
	 * Get the formatted date time from a date String
	 * @param dateString - the String containing the date
	 * @param format - the format of the date in the String
	 * @param zone - the time zone
	 * @see java.time.LocalDate#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 */
	public static ZonedDateTime getFormattedDate(String dateString, DateTimeFormatter format, ZoneId zone) {
		LocalDate date = LocalDate.parse(dateString,format);

		return date.atStartOfDay(zone);
	}
	
	/**
	 * Get the formatted date time from a date String
	 * @param dateString - the String containing the date
	 * @param format - the format of the date in the String
	 * @param zone - the time zone
	 * @see java.time.LocalDate#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 */
	public static ZonedDateTime getFormattedDate(String dateString, String format, ZoneId zone) {
		return getFormattedDate(dateString, DateTimeFormatter.ofPattern(format), zone);
	}
	
	/**
	 * Get the formatted date time from a date String
	 * @param dateString - the String containing the date
	 * @param format - the format of the date in the String
	 * @param zone - the time zone
	 * @see java.time.LocalTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 */
	public static ZonedDateTime getFormattedTime(String timeString, DateTimeFormatter format, ZoneId zone) {
		LocalTime date = LocalTime.parse(timeString,format);
		
		return date.atDate(LocalDate.now(zone)).atZone(zone);
	}
	
	/**
	 * Get the formatted date time from a time String
	 * @param dateString - the String containing the time
	 * @param format - the format of the time in the String
	 * @param zone - the time zone
	 * @see java.time.LocalTime#parse(CharSequence, DateTimeFormatter)
	 * @return a parsed ZonedDateTime
	 */
	public static ZonedDateTime getFormattedTime(String timeString, String format, ZoneId zone) {
		return getFormattedTime(timeString, DateTimeFormatter.ofPattern(format), zone);
	}
	
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
	 * @param date - the date to parse
	 * @param format - the date format
	 * @param zone - the zone in which the date will be parsed
	 * @return if the date is after now
	 */
	public static boolean isValidActualDate(String date, DateTimeFormatter format, ZoneId zone) {
		try {
			return getFormattedDate(date, format, zone).isAfter(ZonedDateTime.now(zone));
		}catch(DateTimeParseException e) {
			return false;
		}
	}
	
}
