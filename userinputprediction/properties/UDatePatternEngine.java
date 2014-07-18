package de.htw_berlin.userinputprediction.properties;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.htw_berlin.userinputprediction.utils.SimpleDateFormatWithPatternString;

public class UDatePatternEngine {
	
	public static Boolean timestampsRepeatOnCalendarPattern(Timestamp[] aTimestampList) {
		Boolean patternCorrect = false;
		if (aTimestampList!=null && aTimestampList.length>=2) {
			// use first timestamp as reference
			int refDay = UDatePatternEngine.getDayOfMonth(aTimestampList[0]);
			int refMonth = UDatePatternEngine.getMonth(aTimestampList[0]);
			int refYear = UDatePatternEngine.getYear(aTimestampList[0]);
			// check annual rhythm first since it is a subset to monthly rhythm
			Boolean annualPatternFound = true;
			int yearCounter=0;
			int looperYear = 0;
			for (Timestamp looper : aTimestampList) {
				// first: check if day-of-month and month conform
				if (UDatePatternEngine.timestampMatchesCalendarPattern(looper, refDay, refMonth)) {
					// check further conditions:
					// Every year, exactly one time
					looperYear = UDatePatternEngine.getYear(looper);
					if (looperYear==refYear+yearCounter) {
						yearCounter++;
					} else {
						// one not matching timestamp > series void
						annualPatternFound = false;
						break;
					}
				} else {
					// one not matching timestamp > series void
					annualPatternFound = false;
					break;
				}
			}
			if (annualPatternFound) {
				patternCorrect = true;
			} else {
				// check momthly pattern
				Calendar refCal = UDatePatternEngine.timestampToCal(aTimestampList[0]);
				Calendar looperCal = null;
				Boolean monthlyPatternFound = true;
				for (Timestamp looper : aTimestampList) {
					if (UDatePatternEngine.timestampMatchesCalendarPattern(looper, refDay, 0)) {
						// check further conditions:
						// once every month
						looperCal = UDatePatternEngine.timestampToCal(looper);
						if (UDatePatternEngine.sameMonthYEAR(looperCal, refCal)) {
							refCal.add(Calendar.MONTH, 1);
						} else {
							// one not matching timestamp > series void
							monthlyPatternFound = false;
							break;
						}
					} else {
						// one not matching timestamp > series void
						monthlyPatternFound = false;
						break;
					}
				}
				// set global success flag to monthly flag
				patternCorrect = monthlyPatternFound;
			}
		}
		return patternCorrect;
	}
	
	public static Boolean timestampMatchesCalendarPattern(Timestamp aTimestamp, int dayOfMonth, int month) {
		Boolean patternCorrect = false;
		if (aTimestamp!=null) {
			if (month==0) {
				// monthly pattern
				if (dayOfMonth == UDatePatternEngine.getDayOfMonth(aTimestamp)) {
					patternCorrect = true;
				}
			} else {
				// annual pattern
				if (dayOfMonth == UDatePatternEngine.getDayOfMonth(aTimestamp)
						&& month == UDatePatternEngine.getMonth(aTimestamp)) {
					patternCorrect = true;
				}
			}
		}
		return patternCorrect;
	}
	
	public static int getDayOfMonth(Timestamp aTimestamp) {
	    return UDatePatternEngine.timestampToCal(aTimestamp).get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getMonth (Timestamp aTimestamp) {
		return UDatePatternEngine.timestampToCal(aTimestamp).get(Calendar.MONTH)+1;
	}
	
	public static int getYear (Timestamp aTimestamp) {
		return UDatePatternEngine.timestampToCal(aTimestamp).get(Calendar.YEAR);
	}
	
	public static Calendar addMonth (Calendar aCalendar) {
		
		aCalendar.add(Calendar.MONTH, 1);
		return aCalendar;
	}
	
	public static Calendar addYear (Calendar aCalendar) {
		aCalendar.add(Calendar.YEAR, 1);
		return aCalendar;
	}
	
	public static Boolean sameYear (Calendar cal1, Calendar cal2) {
		if (cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Boolean sameMonthYEAR (Calendar cal1, Calendar cal2) {
		if (cal1.get(Calendar.MONTH)==cal2.get(Calendar.MONTH)
				&& cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Calendar timestampToCal (Timestamp aTimestamp) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(aTimestamp);
		return aCalendar;
	}

	public static List<SimpleDateFormatWithPatternString> getValidDateFormats () {
		String dateFormatString; 
		List<SimpleDateFormatWithPatternString> dateFormats = new ArrayList<SimpleDateFormatWithPatternString>();
		for (String looper : ULocationEngine.DATE_DIGIT_SEPERATORS) {
			dateFormatString = "dd"+looper+"MM"+looper+"yyyy";
			dateFormats.add(new SimpleDateFormatWithPatternString(dateFormatString));
			dateFormatString = "yyyy"+looper+"MM"+looper+"dd";
			dateFormats.add(new SimpleDateFormatWithPatternString(dateFormatString));
			dateFormatString = "MM"+looper+"dd"+looper+"yyyy";
			dateFormats.add(new SimpleDateFormatWithPatternString(dateFormatString));
		}
		return dateFormats;
	}

	public static String stringsConformsToValidDatePattern (String[] dirNames) {
		List<SimpleDateFormatWithPatternString> validDateFormats = getValidDateFormats();
		Date date = null;
		boolean allDirNamesAreValid=false; // flag for a successful pass of all dirNames
		String foundDateFormatString = ULocationEngine.PATTERN_VALIDATION_FAIL; // saves patternString of applied SimpleDateFormat
			// loop over valid dateFormats
			for (SimpleDateFormatWithPatternString aValidDateFormat : validDateFormats) {
				aValidDateFormat.setLenient(false);
				allDirNamesAreValid=true;
				foundDateFormatString = aValidDateFormat.getDatePatternString();
				for (String aDirName : dirNames) {
					// Validate dirName
					if (!UDatePatternEngine.stringConformsToDatePattern(aDirName, aValidDateFormat)) {
						// quit looping dirNames, if one dirName is invalid
						allDirNamesAreValid=false;
						foundDateFormatString=ULocationEngine.PATTERN_VALIDATION_FAIL;
						break;
					}
				}
				// if one DateFormat worked for all dirNames than return success
				if (allDirNamesAreValid) {
					// successfully found one validDatePattern for all dirNames
					break;
				}
			}
		return foundDateFormatString;
	}

	public static boolean stringConformsToDatePattern (String aString, SimpleDateFormatWithPatternString aDateFormat) {
		boolean datePatternfound = false;
		Date parsedDate = null;
		try {
			parsedDate = aDateFormat.parse(aString);
		} catch (ParseException e) {}
		if (parsedDate != null) {
			datePatternfound = true;
		}
		return datePatternfound;
	}
}
