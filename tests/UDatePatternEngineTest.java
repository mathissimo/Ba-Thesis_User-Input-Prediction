package de.htw_berlin.userinputprediction.tests;

import java.sql.Timestamp;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.htw_berlin.userinputprediction.properties.UDatePatternEngine;

public class UDatePatternEngineTest {
	private Timestamp ts1 = Timestamp.valueOf("2000-01-19 0:0:0.0");
	private Timestamp ts2 = Timestamp.valueOf("2000-02-19 0:0:0.0");
	private Timestamp ts3 = Timestamp.valueOf("2000-03-19 0:0:0.0");
	private Timestamp ts4 = Timestamp.valueOf("2000-04-19 0:0:0.0");
	private Timestamp ts5 = Timestamp.valueOf("2000-01-01 0:0:0.0");
	private Timestamp ts6 = Timestamp.valueOf("2001-01-01 0:0:0.0");
	private Timestamp ts7 = Timestamp.valueOf("2002-01-01 0:0:0.0");
	private Timestamp ts8 = Timestamp.valueOf("2003-01-01 0:0:0.0");
	// to break monthly pattern
	private Timestamp ts9 = Timestamp.valueOf("2000-06-19 0:0:0.0");
	// to break annual pattern
	private Timestamp ts10 = Timestamp.valueOf("2005-01-01 0:0:0.0");

	private Timestamp [] monthlyPattern = {
			ts1,
			ts2,
			ts3,
			ts4
	};
	private Timestamp [] annualPattern= {
			ts5,
			ts6,
			ts7,
			ts8
	};

	private Timestamp [] noPattern = {
			ts1,
			ts2,
			ts7,
			ts8
	};



	@BeforeClass
	private void setUp () {
	}

	@Test
	public void getDayOfMonth() {
		Assert.assertEquals(19, UDatePatternEngine.getDayOfMonth(ts1));
	}

	@Test
	public void getMonth() {
		Assert.assertEquals(1, UDatePatternEngine.getMonth(ts1));
	}

	@Test
	public void timestampMatchesCalendarPattern() {
		Assert.assertTrue(UDatePatternEngine.timestampMatchesCalendarPattern(ts1, 19, 1));
	}

	@Test
	public void timestampsRepeatOnCalendarPattern() {
		Assert.assertFalse(UDatePatternEngine.timestampsRepeatOnCalendarPattern(noPattern));
		Assert.assertTrue(UDatePatternEngine.timestampsRepeatOnCalendarPattern(monthlyPattern));
		Assert.assertTrue(UDatePatternEngine.timestampsRepeatOnCalendarPattern(annualPattern));
		Timestamp [] monthlyPattern2 = {
				ts1,
				ts2,
				ts3,
				ts4,
				ts9,
		};
		Timestamp [] annualPattern2 = {
				ts5,
				ts6,
				ts7,
				ts8,
				ts10
		};

		Assert.assertFalse(UDatePatternEngine.timestampsRepeatOnCalendarPattern(monthlyPattern2));
		Assert.assertFalse(UDatePatternEngine.timestampsRepeatOnCalendarPattern(annualPattern2));
	}
}
