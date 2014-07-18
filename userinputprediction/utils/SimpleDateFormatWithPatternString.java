package de.htw_berlin.userinputprediction.utils;

import java.text.SimpleDateFormat;

public class SimpleDateFormatWithPatternString extends SimpleDateFormat {
	private String datePatternString;
	
	public SimpleDateFormatWithPatternString (String datePatternString) {
		super (datePatternString);
		this.datePatternString=datePatternString;
	}

	public String getDatePatternString() {
		return datePatternString;
	}

}
