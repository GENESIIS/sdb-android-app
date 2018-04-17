package lk.topjobs.android.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.format.Time;

/**
 * @author Harsha Kodagoda
 *
 */
public class DateTimeUtils {
	private static SimpleDateFormat dateFormatFull = null;
	private static SimpleDateFormat dateFormatYear = null;

	public static String formatDateSameYear(Date date) {
		if (dateFormatYear == null) {
			dateFormatYear = new SimpleDateFormat("MMM dd", Locale.getDefault());
		}
		return dateFormatYear.format(date);
	}

	public static String formatDateFull(Date date) {
		if (dateFormatFull == null) {
			dateFormatFull = new SimpleDateFormat("MMM dd, yyyy",
					Locale.getDefault());
		}
		return dateFormatFull.format(date);
	}

	public static String getRelativeDayString(Date postDate) {
		long today = Calendar.getInstance().getTimeInMillis();
		Calendar postCalendar = Calendar.getInstance();
		postCalendar.setTime(postDate);
		Time startTime = new Time();
		startTime.set(postDate.getTime());
		Time currentTime = new Time();
		currentTime.set(today);
		int startDay = Time.getJulianDay(postDate.getTime(), startTime.gmtoff);
		int currentDay = Time.getJulianDay(today, currentTime.gmtoff);
		int days = Math.abs(currentDay - startDay);
		if (today > postDate.getTime()) {
			int monthTotalDays = Calendar.getInstance().getActualMaximum(
					Calendar.DAY_OF_MONTH);
			if (days == 1) {
				return "yesterday";
			} else if (days == 0) {
				return "today";
			} else if (days < 7) {
				return days + " days ago";
			} else if (days >= 7 && days < monthTotalDays) {
				int weeks = days / 7;
				if (weeks == 1) {
					return "week ago";
				} else {
					return weeks + " weeks ago";
				}
			} else { 
				if (postCalendar.get(Calendar.YEAR) == Calendar.getInstance()
						.get(Calendar.YEAR)) {
					return formatDateSameYear(postDate);
				} else {
					return formatDateFull(postDate);
				}
			}
		} else {
			return formatDateFull(postDate);
		}
	}
}
