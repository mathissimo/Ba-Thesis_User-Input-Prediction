package de.htw_berlin.userinputprediction.properties;

import java.sql.Timestamp;

import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;

public class UFrequencyEngine {

	public static double relativeDeviationFromAverageLag (UCopyJobSeries aCopyJobSeries) {
		long [] lagVector = UFrequencyEngine.lagVectorFromUCopyJobSeries(aCopyJobSeries);
		return UFrequencyEngine.relativeDeviationFromAverageLag(lagVector);
	}

	public static double relativeDeviationFromAverageLag (long [] lagVector) {
		double relativeDeviation = -1.0f;
		// at least three timestamps to get two time lags
		if (lagVector.length>=2) {
			relativeDeviation = UFrequencyEngine.standardDevFromAverageLag(lagVector)/UFrequencyEngine.meanFromVector(lagVector);
		}
		return relativeDeviation;
	}

	public static long[] lagVectorFromUCopyJobSeries (UCopyJobSeries aCopyJobSeries) {
		Timestamp [] jobOccurances = aCopyJobSeries.getCopyJobsTimeEvent();
		return UFrequencyEngine.lagVectorFromTimestamps(jobOccurances);
	}

	public static long[] lagVectorFromTimestamps (Timestamp[] timestamps) {
		long[] lagVector = null;
		int numberOfCopyJobs = timestamps.length;
		if (numberOfCopyJobs>=2) {
			// calc lags
			int numberOfLags = numberOfCopyJobs-1;
			lagVector = new long[numberOfLags];
			for (int i = 0; i < numberOfLags; i++) {
				lagVector[i] = UFrequencyEngine.timeDiffInMilliSec(timestamps[i],timestamps[i+1]);
			}
		}
		return lagVector;
	}

	public static double standardDevFromAverageLag (UCopyJobSeries aCopyJobSeries) {
		return UFrequencyEngine.standardDevFromAverageLag(UFrequencyEngine.lagVectorFromUCopyJobSeries(aCopyJobSeries));
	}

	public static double standardDevFromAverageLag (long[] dataVector) {
		// calc arithmetic mean
		double mean = 0.0f;
		double stdDev = -1.0f;
		int numberOfValues = dataVector.length;
		if (numberOfValues >= 2) {
			mean = UFrequencyEngine.meanFromVector(dataVector);
			// calc standard-deviation from mean
			double variance = 0.0f;
			for (int i = 0; i < numberOfValues; i++) {
				variance += Math.pow(dataVector[i]-mean,2);
			}
			variance = variance / numberOfValues; // Population Standard Deviation
			// calc stddev
			stdDev = Math.sqrt(variance);
		}
		return stdDev;
	}

	public static long averageTimeInterval (UCopyJobSeries aCopyJobSeries) {
		return UFrequencyEngine.meanFromVector(UFrequencyEngine.lagVectorFromUCopyJobSeries(aCopyJobSeries));
	}

	public static Boolean calendarMatchingFound(UCopyJobSeries aCopyJobSeries) {
		return UDatePatternEngine.timestampsRepeatOnCalendarPattern(aCopyJobSeries.getCopyJobsTimeEvent());
	}
	
	public static float averageRelativeDeviation (long intervalLength, UCopyJobSeries aCopyJobSeries) {
		return UFrequencyEngine.averageRelativeDeviation(intervalLength, aCopyJobSeries.getCopyJobsTimeEvent());
	}
	
	public static float averageRelativeDeviation(long intervalLength, Timestamp timestamps[]) {
		float avrIntervalDev = -1;
		long devSum = 0;
		long [] devs = UFrequencyEngine.deviationsFromInterval(intervalLength, timestamps);
		for (int i = 0; i < timestamps.length; i++) {
			devSum = devSum + devs [i];
		}
		avrIntervalDev = (devSum / timestamps.length);
		avrIntervalDev = avrIntervalDev / intervalLength;
		return avrIntervalDev;
	}

	public static long [] deviationsFromInterval (long intervalLength, Timestamp timestamps[]) {
		long [] intervalDev = new long [timestamps.length];
		long timeLooper;
		for (int i = 0; i < intervalDev.length; i++) {
			timeLooper = timestamps[i].getTime();
			intervalDev[i]=Math.abs(nearestInterval(intervalLength, timeLooper)-timeLooper);
		}
		return intervalDev;
	}
	
	public static long nearestInterval (long intervalLength,long value) {
		double moduloValue = value % intervalLength;
		long quotient = value/intervalLength;
		if (moduloValue>=intervalLength/2) {
			quotient++;
		}
		return quotient*intervalLength;
	}


	public static long meanFromVector (long[] dataVector) {
		double mean = 0.0f;
		int numberOfValues = dataVector.length;
		if (numberOfValues >= 2) {
			for (int i = 0; i < numberOfValues; i++) {
				mean += dataVector[i];
			}
			mean = mean/numberOfValues;
		}
		return Math.round(mean);
	}

	public static long timeDiffInMilliSec (Timestamp earlierDate,Timestamp laterDate) {
		return (laterDate.getTime()-earlierDate.getTime());
	}
}
