package de.htw_berlin.userinputprediction.properties;

import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;

public class UCopyJobSeriesProperties {
	public static final float THRESHOLD_PLANED_BACKUP_MIN_REL_STD_DEV = 0.8f;
	// Frequency Criterion
	private int copyJobCount;
	private long averageTimeInterval;
	private double standardDeviation;
	private Boolean calendarMatching;
	// Location Criterion
	private Boolean constantDestination;
	private Boolean constantSuperFolderPattern;
	private float alikeDestinationSuperFolder;
	private float averageDeviationFromFullHour;

	public UCopyJobSeriesProperties(UCopyJobSeries evaluatedSeries) {
		this.updateProperties(evaluatedSeries);
	}

	public void updateProperties(UCopyJobSeries evaluatedSeries) {
		// Frequency Criterion
		this.copyJobCount = evaluatedSeries.getNumberOfCopyJobs();
		if (this.copyJobCount>=2) { // averageTimeIntervall only available for 2 and more Copyjobs
			this.standardDeviation = UFrequencyEngine.standardDevFromAverageLag(evaluatedSeries);
			this.averageTimeInterval = UFrequencyEngine.averageTimeInterval(evaluatedSeries);
		} else {
			this.standardDeviation = 0;
			this.averageTimeInterval = 0;
		}
		this.averageDeviationFromFullHour = UFrequencyEngine.averageRelativeDeviation(60*60*1000, evaluatedSeries);
		this.calendarMatching = UFrequencyEngine.calendarMatchingFound(evaluatedSeries);

		// Location Criterion
		this.constantDestination = ULocationEngine.allDestinationsAreEqual(evaluatedSeries);
		// check only for constant Folder pattern if destination is not constant 
		if (!this.constantDestination) {
			this.constantSuperFolderPattern = ULocationEngine.constantSuperFolderPatternFound(evaluatedSeries);
		} else {
			this.constantSuperFolderPattern = false;
		}
		// check only for alike value if destination is neither constant nor with constant pattern  
		if (this.constantDestination || this.constantSuperFolderPattern) {
			this.alikeDestinationSuperFolder = 0.0f;
		} else {
			this.alikeDestinationSuperFolder = ULocationEngine.alikeDestinationDSF(evaluatedSeries);
		}
	}

	public Boolean isQualifiedForBackupAtHand () {
		Boolean qualification = false;
		// exclude Backup Case if property indicates
		// that a necessary condition is not matched.
		if (
				this.isRepetitiveBackup()
				|| this.isPlanedBackup()
			) {
			qualification = true;
		}
		return qualification;
	}

	// necessary condition: reptitive backup
	public Boolean isRepetitiveBackup () {
		if (this.copyJobCount <2) {
			return false;
		} else {
			return true;
		}
	}

	public Boolean isPlanedBackup() {
		Boolean qualification = true;
		if (
				!this.calendarMatching
				|| this.getRelativeStandardDeviation() > THRESHOLD_PLANED_BACKUP_MIN_REL_STD_DEV
				) {
			qualification = false;
		}
		return qualification;
	}

	public double getRelativeStandardDeviation () {
		double relDev = -1.0f;
		if (this.averageTimeInterval>0) {
			relDev=this.standardDeviation/this.averageTimeInterval;
		}
		return relDev;
	}

	public int getCopyJobCount() {
		return copyJobCount;
	}
	public long getAverageTimeInterval() {
		return averageTimeInterval;
	}
	public double getStandardDeviation() {
		return standardDeviation;
	}
	public Boolean getCalendarMatching() {
		return calendarMatching;
	}

	public Boolean getConstantDestination() {
		return constantDestination;
	}

	public Boolean getConstantSuperFolderPattern() {
		return constantSuperFolderPattern;
	}

	public float getAlikeDestinationSuperFolder() {
		return alikeDestinationSuperFolder;
	}

	public float getAverageDeviationFromFullHour() {
		return averageDeviationFromFullHour;
	}
}
