package de.htw_berlin.userinputprediction.trainingdata;

import java.sql.Timestamp;

import de.htw_berlin.userinputprediction.properties.ULocationEngine;

public class UClassifierTrainingDataSettings {
	// valid series parameters
	public Boolean generateValidData;  // General
	public int numberOfCopyJobs;  // copyjobs in einer serie
	public Boolean withDSF;            // Dynamic Super Folder: variabler Verzeichnisname an einer Stelle im Pfad (nur Destination)
	public Boolean calendarNotRegularReptitionPattern; // calendar pattern: immer am x. des Monats/Jahres, Regualr Pattern: festes invervall (48h..)
	public Boolean monthlyNotAnnualyCalendarPattern; // calendar pattern immer x.des Monats oder x.y des Jahres 
	public int dsfDateDigitPattern; // verdrehung der Ziffern bei dsf-foldername 23.01.2014 oder 2014-01-23
	public int seperatorIndex; // in dsf
	public int basePathIndex; // index für puzzelstücke um Pfad zusammenzusetzen
	public int midPathIndex;// index für puzzelstücke um Pfad zusammenzusetzen
	public int endPathDestFolderIndex;// index für puzzelstücke um Pfad zusammenzusetzen
	public Timestamp startSeries; // Anfang einer Serie
	public Timestamp endSeries;
	public long averageInterval; // bei regurlar Pattern: Abstand der Copyjobs (z.b 48h)
	public long standardDeviation; // Schwankungsbreite um Interval (etwas zufällig)
	public int nmbFolder;  // anzahl der Folder bei den Sources
	public int nmbFile;
	// invalid series parameters
	public Boolean randomCopyJobTimestamps = false; // invalide serie: copyjobs ohne festes intervall (total zufällig)
	public Boolean invalidDSF = false; // invalide serie: DSf hat weder calender noch regular pattern

	// methods are hard-wired "pre-sets"
	public void setToValid(int numberOfCopyJobs, Boolean withDSF) {
		this.numberOfCopyJobs = numberOfCopyJobs;
		this.withDSF = withDSF;
		this.generateValidData = true;
		if (withDSF) {
			this.randomDSFDatePattern();
		}
		this.randomValidTimestamps();
		this.randomPathIndex();
		this.standardTimestampsAndInterval();
		this.randomFileFolderNmb();
	}

	public void setToInvalid(int numberOfCopyJobs, Boolean randomCopyJobTimestamps, Boolean invalidDSF) {
		this.invalidDSF = invalidDSF;
		this.withDSF = invalidDSF;
		this.numberOfCopyJobs = numberOfCopyJobs;
		this.generateValidData = false;
		if (withDSF) {
			this.randomDSFDatePattern();
		}
		if (randomCopyJobTimestamps) {
			this.randomValidTimestamps();
		}
		this.randomPathIndex();
		this.standardTimestampsAndInterval();
		this.randomFileFolderNmb();
	}

	public void randomDSFDatePattern() {
		this.withDSF = true;
		this.dsfDateDigitPattern =  (int) Math.round((UClassifierTrainingDataGenerator.NBR_DIGIT_POSITION-1)*Math.random());
		this.seperatorIndex = (int) Math.round((ULocationEngine.DATE_DIGIT_SEPERATORS.length-1)*Math.random());
		this.randomValidTimestamps();
	}

	public void randomValidTimestamps() {
		this.calendarNotRegularReptitionPattern = (Math.round(Math.random())==0);
		this.monthlyNotAnnualyCalendarPattern = (Math.round(Math.random())==0);
	}

	public void randomPathIndex () {
		this.basePathIndex = (int) Math.round((UClassifierTrainingDataGenerator.basePaths.length-1)*Math.random());
		this.midPathIndex = (int) Math.round((UClassifierTrainingDataGenerator.midPaths.length-1)*Math.random());
		this.endPathDestFolderIndex = (int) Math.round((UClassifierTrainingDataGenerator.endPathsDestFolder.length-1)*Math.random());
	}

	public void standardTimestampsAndInterval (){
//		int randomMinute = (int) Math.round(59*Math.random());
//		int randomSecond = (int) Math.round(59*Math.random());
//		int randomMicroSecond = (int) Math.round(1000*Math.random());
//		this.startSeries = Timestamp.valueOf("2010-01-01 12:"+randomMinute+":"+randomSecond+"."+randomMicroSecond);
//		this.endSeries = Timestamp.valueOf("2012-12-31 12:0:0.0");
		this.startSeries = Timestamp.valueOf("2010-01-01 14:28:0.0");
		this.endSeries = Timestamp.valueOf("2012-12-31 12:0:0.0");
		this.averageInterval = 7*24*3600*1000; // 7 days
		this.standardDeviation = 5*3600*1000; // 5h
	}

	public void randomFileFolderNmb() {
		this.nmbFile = (int) Math.round(5*Math.random());
		this.nmbFolder = (int) Math.round(5*Math.random());
		if (this.nmbFile + this.nmbFolder == 0) {
			this.nmbFile = 1;
		}
	}

	public String toString() {
		String output = "\n\nSettings Training Data Generator\n";
		output = output + "generateValidData: " + generateValidData + "\n";
		output = output + "numberOfCopyJobs: " + numberOfCopyJobs + "\n";
		output = output + "withDSF: " + withDSF + "\n";
		output = output + "calendarNotRegularReptitionPattern: " + calendarNotRegularReptitionPattern + "\n";
		output = output + "monthlyNotAnnualyCalendarPattern: " + monthlyNotAnnualyCalendarPattern + "\n";
		output = output + "dsfDateDigitPattern: " + dsfDateDigitPattern + "\n";
		output = output + "seperatorIndex: " + seperatorIndex + "\n";
		output = output + "basePathIndex: " + basePathIndex + "\n";
		output = output + "midPathIndex: " + midPathIndex + "\n";
		output = output + "endPathDestFolderIndex: " + endPathDestFolderIndex + "\n";
		output = output + "startSeries: " + startSeries + "\n";
		output = output + "endSeries: " + endSeries + "\n";
		output = output + "averageInterval: " + averageInterval + "\n";
		output = output + "standardDeviation: " + standardDeviation + "\n";
		output = output + "nmbFolder: " + nmbFolder + "\n";
		output = output + "nmbFile: " + nmbFile + "\n";
		output = output + "randomCopyJobTimestamps: " + randomCopyJobTimestamps + "\n"; 
		output = output + "invalidDSF: " + invalidDSF + "\n\n";
		return output;
	}
}
