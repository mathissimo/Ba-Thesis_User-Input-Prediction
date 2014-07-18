package de.htw_berlin.userinputprediction.tests;

import java.sql.Timestamp;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.htw_berlin.userinputprediction.properties.UFrequencyEngine;

public class UFrequencyEngineTest {
	@BeforeClass
	public void beforeClass() {
	}

	@Test
	public void lagVectorFromUCopyJobSeries() {
		Timestamp [] timestamps = {
				Timestamp.valueOf("2000-01-19 0:0:0.0"),
				Timestamp.valueOf("2000-01-19 0:0:10.0"),
				Timestamp.valueOf("2000-01-19 0:0:20.0"),
				Timestamp.valueOf("2000-01-19 0:0:30.0"),
				Timestamp.valueOf("2000-01-19 1:0:40.0"),
		};
		long [] expected = {
				10*1000,
				10*1000,
				10*1000,
				1*60*60*1000+10*1000
		};
		Assert.assertEquals(UFrequencyEngine.lagVectorFromTimestamps(timestamps), expected);
	}

	@Test
	public void nearestInterval() {
		Assert.assertEquals(UFrequencyEngine.nearestInterval(10, 42), 40);
		Assert.assertEquals(UFrequencyEngine.nearestInterval(10, 44), 40);
		Assert.assertEquals(UFrequencyEngine.nearestInterval(10, 48), 50);
		Assert.assertEquals(UFrequencyEngine.nearestInterval(10, 45), 50);
	}

	@Test
	public void deviationsFromInterval() {
		Timestamp [] timestamps = {
				Timestamp.valueOf("2000-01-19 0:0:0.0"),
				Timestamp.valueOf("2000-01-19 0:0:10.0"),
				Timestamp.valueOf("2000-01-19 0:0:20.0"),
				Timestamp.valueOf("2000-01-19 0:0:30.0"),
				Timestamp.valueOf("2000-01-19 0:0:40.0"),
		};
		long [] expectedValues = {
				0,
				10*1000,
				20*1000,
				20*1000,
				10*1000
		};
		Assert.assertEquals(UFrequencyEngine.deviationsFromInterval(50*1000, timestamps), expectedValues);
	};

	@Test
	public void averageRelativeDeviation() {
		Timestamp [] timestamps = {
				Timestamp.valueOf("2000-01-19 0:0:0.0"),
				Timestamp.valueOf("2000-01-19 0:0:10.0"),
				Timestamp.valueOf("2000-01-19 0:0:20.0"),
				Timestamp.valueOf("2000-01-19 0:0:30.0"),
				Timestamp.valueOf("2000-01-19 0:0:40.0"),
		};
		float expected = 12.0f*1000/(50*1000);
		Assert.assertEquals(UFrequencyEngine.averageRelativeDeviation(50*1000, timestamps),expected);
	};

	@Test
	public void meanFromVector() {
		Timestamp [] timestamps = {
				Timestamp.valueOf("2000-01-19 0:0:0.0"),
				Timestamp.valueOf("2000-01-19 0:0:10.0"),
				Timestamp.valueOf("2000-01-19 0:0:20.0"),
				Timestamp.valueOf("2000-01-19 0:0:30.0"),
				Timestamp.valueOf("2000-01-19 0:0:40.0"),
		};
		long [] timeVector = UFrequencyEngine.lagVectorFromTimestamps(timestamps);
		Assert.assertEquals(UFrequencyEngine.meanFromVector(timeVector), 10*1000);
	}

	@Test
	public void relativeDeviationFromAverageLag() {
		// mean: 10 sec
		// dev: 5 sec
		// avrdev: 5/10 
		Timestamp [] timestamps = {
				Timestamp.valueOf("2000-01-19 0:0:0.0"),
				Timestamp.valueOf("2000-01-19 0:0:5.0"),
				Timestamp.valueOf("2000-01-19 0:0:20.0"),
				Timestamp.valueOf("2000-01-19 0:0:25.0"),
				Timestamp.valueOf("2000-01-19 0:0:40.0"),
		};
		long [] timeVector = UFrequencyEngine.lagVectorFromTimestamps(timestamps);
		Assert.assertEquals(UFrequencyEngine.relativeDeviationFromAverageLag(timeVector), 0.5d);
	}

	@Test
	public void standardDevFromAverageLaglong() {
		// mean: 10 sec
		// dev: 5 sec
		Timestamp [] timestamps = {
				Timestamp.valueOf("2000-01-19 0:0:0.0"),
				Timestamp.valueOf("2000-01-19 0:0:5.0"),
				Timestamp.valueOf("2000-01-19 0:0:20.0"),
				Timestamp.valueOf("2000-01-19 0:0:25.0"),
				Timestamp.valueOf("2000-01-19 0:0:40.0"),
		};
		long [] timeVector = UFrequencyEngine.lagVectorFromTimestamps(timestamps);
		Assert.assertEquals(UFrequencyEngine.standardDevFromAverageLag(timeVector), 5000.0d);
	}

}
