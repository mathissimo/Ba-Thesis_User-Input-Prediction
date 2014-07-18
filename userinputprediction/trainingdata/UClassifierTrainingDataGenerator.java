package de.htw_berlin.userinputprediction.trainingdata;

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;

import de.htw_berlin.userinputprediction.coordination.UCopyJobService;
import de.htw_berlin.userinputprediction.copyjob.UCopyJob;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobDestination;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobObjects;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.properties.UDatePatternEngine;
import de.htw_berlin.userinputprediction.properties.ULocationEngine;
import de.htw_berlin.userinputprediction.utils.UPersistanceService;

public class UClassifierTrainingDataGenerator {
	
	// mmtd:
	// - Tippfehler bei DSF
	// - Schwankung ums Intervallgrenze

	public static final int DMY = 0;
	public static final int YMD = 1;
	public static final int MDY = 2;
	public static final int NBR_DIGIT_POSITION = 3;

	public static String [] basePaths = {
			"/users/peter/documents",	
			"/users/peter/documents/private",	
			"/users/peter/documents/job",	
			"/users/peter/documents/home",	
			"/users/peter/documents/kids",	
			"/users/peter/documents/community",	
			"/users/peter/music",	
			"/users/peter/music/peters_music",	
			"/users/peter/music/violas_music",	
			"/users/peter/music/pauls_music",	
			"/users/peter/music/sandys_music",	
			"/users/peter/music/sandys_music"	
	};
	public static String [] midPaths = {
			"/summer",	
			"/winter",	
			"/fall",	
			"/spring",	
			"/project_a",	
			"/project_b",	
			"/project_c",	
			"/project_d",	
			"/project_e",	
			"/project_f",	
			"/project_g",	
			"/project_h"	
	};
	public static String [] endPathsFile = {
			"/pic_*.jpg",	
			"/song_*.mp3",	
			"/document_*.doc"	
	};

	public static String [] endPathsFolder = {
			"/homework_*/",	
			"/vacation_*/",	
			"/friends_episode_*/"	
	};

	public static String [] endPathsDestFolder = {
			"/",	
			"/complete/"	
	};
	
	public static String [] dsfCalDatePatSeperator = ULocationEngine.DATE_DIGIT_SEPERATORS;

	private int nmbBase, nmbMid, nmbEndFile, nmbEndFolder, nmbEndDestFolder;

	public UClassifierTrainingDataGenerator (){
		this.nmbBase = UClassifierTrainingDataGenerator.basePaths.length;
		this.nmbMid = UClassifierTrainingDataGenerator.midPaths.length;
		this.nmbEndFile = UClassifierTrainingDataGenerator.endPathsFile.length;
		this.nmbEndFolder = UClassifierTrainingDataGenerator.endPathsFolder.length;
		this.nmbEndDestFolder = UClassifierTrainingDataGenerator.endPathsDestFolder.length;
	}

	public UCopyJobSeries generateCopyJobSeries(UClassifierTrainingDataSettings settings) {
			UCopyJobSeries newCopyJobSeries = null;
			// create destinations
			String [] destinationFilePath =  this.generatePathSet(settings,false);
			UCopyJobDestination[] cjDestinations = this.uCopyJobDestinationFromFilePathString(destinationFilePath);
			// create CopyjobObjects
			String [] sourceFilePath =  this.generatePathSet(settings,true);
			UCopyJobObjects []	cjObjects = this.uCopyJobObjectsFromFilePathString(settings.numberOfCopyJobs, sourceFilePath);
			// create Timestamps
			Timestamp [] cjTimestamps = null;
			// Generate Valid data ? (+ invalid data, but with valid Timestamps)
			if (settings.generateValidData || !settings.randomCopyJobTimestamps) {
				// valid timestamps: with time pattern
				if (settings.calendarNotRegularReptitionPattern) {
					// calendar pattern
					cjTimestamps = this.generateTimestampSeriesWithCalendarPattern(
							settings.startSeries, 
							settings.monthlyNotAnnualyCalendarPattern,
							settings.standardDeviation, 
							settings.numberOfCopyJobs
							);
				} else {
					// regular repeating date pattern
					cjTimestamps = this.generateTimestampSeriesWithPattern(
							settings.startSeries, 
							settings.averageInterval, 
							settings.standardDeviation, 
							settings.numberOfCopyJobs
							);
				}
			} else {
				// invalid timestamps: 
				cjTimestamps = this.generateRandomTimestampSeries(
						settings.startSeries, 
						settings.endSeries, 
						settings.numberOfCopyJobs
						);
			}
			// create Copyjobs
			for (int cjCounter = 0; cjCounter < settings.numberOfCopyJobs; cjCounter++) {
				UCopyJob newCopyJob = new UCopyJob(cjObjects[cjCounter], cjDestinations[cjCounter], cjTimestamps[cjCounter]);
				if (cjCounter==0) {
					// first copyjob: initialize series
					newCopyJobSeries = new UCopyJobSeries(newCopyJob);
				} else {
					// following: just add
					newCopyJobSeries.addCopyJob(newCopyJob);
				}
			}
			// label Copyjob Series
			if (newCopyJobSeries!=null) {
				newCopyJobSeries.isLabel=settings.generateValidData;
			}
			return newCopyJobSeries;
		}

	public String[] generatePathSet(UClassifierTrainingDataSettings settings, Boolean isCopyJobObjectNotDestination) {
		int number;
		int randomNumberFolder = -1;
		if (isCopyJobObjectNotDestination) {
			number = settings.nmbFile+settings.nmbFolder; // multiple path for objects
			randomNumberFolder = (int) Math.round(Math.random()*1000000);
		} else {
			number = settings.numberOfCopyJobs;  // one path if used for destination
		}
		String [] paths = new String [number];
		String [] dsfs = new String [number];

		// generate pathEnds
		String [] pathEnds = new String [number];
		if (isCopyJobObjectNotDestination) {
			// for Copyjob Objects
			// first folder, than files
			pathEnds = (String[]) ArrayUtils.addAll(
					this.generatePathsEnd(false, settings.nmbFolder), 
					this.generatePathsEnd(true, settings.nmbFile)
					);
		} else {
			// for Copyjob Destinations
			int randomPathEnd = (int) Math.round((this.nmbEndDestFolder-1)*Math.random());
			for (int i = 0; i < pathEnds.length; i++) {
				pathEnds[i]=UClassifierTrainingDataGenerator.endPathsDestFolder[randomPathEnd];
			}
		}
		// Valid Data (+ invalid data but with valid DSF)?
		if (settings.generateValidData || !settings.invalidDSF) {
			// generate DSF?
			if (!isCopyJobObjectNotDestination && settings.withDSF) {
				// generate DSF series
				if (settings.calendarNotRegularReptitionPattern) {
					// Calendar Matching DSF
					int [] startDate = this.intVectorFromTimestam(settings.startSeries);
					dsfs = this.generateCalendarDatePatternDSF(Math.round(Math.random())==0, number, settings.dsfDateDigitPattern, startDate[0], startDate[1], startDate[2], settings.seperatorIndex);
				} else {
					// Random Valid Dates
					dsfs = this.generateRandomDatePatternDSF(number, settings.startSeries, settings.endSeries, settings.dsfDateDigitPattern, settings.seperatorIndex);
				}
			} else {
				// no DSF
				// empty dsfs
				for (int i = 0; i < dsfs.length; i++) {
					dsfs[i]="";
				}
			}
		} else {
			// Invalid data: invalid DSF
			int randomMidPathIndex =0;
			for (int i = 0; i < dsfs.length; i++) {
				randomMidPathIndex = (int) Math.round((this.nmbMid-1)*Math.random());
				dsfs[i]=UClassifierTrainingDataGenerator.midPaths[randomMidPathIndex];
			}
		}
		// assemble paths
		for (int i = 0; i < paths.length; i++) {
			paths[i] = this.assemblePath(settings.basePathIndex, settings.midPathIndex, dsfs[i], pathEnds[i], randomNumberFolder);
		}
		return paths;
	}

	public String [] generatePathsEnd(Boolean fileNotFolder, int number) {
		String pathEnds [] = new String [number];
		int pathEndIndex;
		if (fileNotFolder) {
			pathEndIndex = (int) Math.round((this.nmbEndFile-1)*Math.random());
		} else {
			pathEndIndex = (int) Math.round((this.nmbEndFolder-1)*Math.random());
		}
		for (int i = 0; i < pathEnds.length; i++) {
			pathEnds[i]= this.generatePathEndWithSerial(fileNotFolder, pathEndIndex, i);
		}
		return pathEnds;
	}

	public String generatePathEndWithSerial(Boolean isFileNotFolder, int pathEndIndex,int serialNumber) {
		String pathEnd = null;
		if (isFileNotFolder) {
			pathEnd = UClassifierTrainingDataGenerator.endPathsFile[pathEndIndex];
		} else {
			pathEnd = UClassifierTrainingDataGenerator.endPathsFolder[pathEndIndex];
		}
		pathEnd = pathEnd.replace("*",""+serialNumber);
		return pathEnd;
	}

	public String[] generateRandomDatePatternDSF (int number, Timestamp startT, Timestamp endT, int pattern, int seperatorIndex) {
		String [] dsfs = new String [number];
		String sep = UClassifierTrainingDataGenerator.dsfCalDatePatSeperator[seperatorIndex];
		Timestamp[] randomTimestamps = this.generateRandomTimestampSeries(startT, endT, number);
		int [] timeStampAsInt;
		for (int i = 0; i < dsfs.length; i++) {
			timeStampAsInt = this.intVectorFromTimestam(randomTimestamps[i]);
			dsfs[i]= this.dateAsStringWithPattern(pattern, timeStampAsInt[0], timeStampAsInt[1], timeStampAsInt[2], sep);
		}
		return dsfs;
	}

	public String[] generateCalendarDatePatternDSF (Boolean monthlyNotAnnualy, int number,int pattern, int firstDay, int firstMonth, int firstYear, int seperatorIndex) {
		String [] dsfs = new String [number];
		int [][] dates = this.generateCalendarDatePatternAsInt(monthlyNotAnnualy, number, firstDay, firstMonth, firstYear);
		String sep = UClassifierTrainingDataGenerator.dsfCalDatePatSeperator[seperatorIndex];
		for (int i = 0; i < dsfs.length; i++) {
			dsfs[i]= this.dateAsStringWithPattern(pattern, firstDay, dates[i][1], dates[i][2], sep);
		}
		return dsfs;
	}

	public int[][] generateCalendarDatePatternAsInt (Boolean monthlyNotAnnualy, int number, int firstDay, int firstMonth, int firstYear) {
		int [][] dates = new int [number][3];
		int month = firstMonth;
		int year = firstYear;
		for (int i = 0; i < number; i++) {
			if (monthlyNotAnnualy) {
				month =(firstMonth+i)%12;
				year = firstYear + ((firstMonth+i)/12);
				if (month == 0) {
					month=12;
					year--;
				}
			} else {
				year = firstYear + i;
			}
			dates[i][0] = firstDay;
			dates[i][1] = month;
			dates[i][2] = year;
		}
		return dates;
	}
	
	public Timestamp[] generateRandomTimestampSeries(Timestamp start, Timestamp end, int number) {
		Timestamp [] timestamps = new Timestamp [number];
		long timeIntervall = end.getTime()-start.getTime();
		long timeToAdd;
		for (int i = 0; i < timestamps.length; i++) {
			timeToAdd = Math.round(timeIntervall*Math.random());
			Timestamp newStamp = new Timestamp(start.getTime()+timeToAdd);
			timestamps[i]= newStamp;
		}
		return timestamps;
	}

	public Timestamp[] generateTimestampSeriesWithPattern(Timestamp start, long averageInterval, long standardDeviation, int number) {
		Timestamp [] timestamps = new Timestamp [number];
		long timeToAdd =0;
		long deviation = 0;
		for (int i = 0; i < timestamps.length; i++) {
			// deviation: deviation needs to be calculated for positive + negative
			deviation = Math.round(Math.random()*standardDeviation*2)-standardDeviation;
			timeToAdd = i * averageInterval-deviation;
			Timestamp newStamp = new Timestamp(start.getTime()+timeToAdd);
			timestamps[i]= newStamp;
		}
		return timestamps;
	}

	public Timestamp[] generateTimestampSeriesWithCalendarPattern(Timestamp start, Boolean monthlyNotAnnualy, long standardDeviation, int number) {
		int [] startDayInt = this.intVectorFromTimestam(start);
		// generate base date that match calendar patterns (series of int:day, month, year)
		int [][] baseDates = this.generateCalendarDatePatternAsInt(monthlyNotAnnualy, number, startDayInt[0], startDayInt[1], startDayInt[2]);
		// generate Timestamps
		Timestamp [] timestamps = new Timestamp [number];
		long deviation = 0;
		Timestamp baseDateAsTimestamp = null;
		String baseDateString;
		for (int i = 0; i < timestamps.length; i++) {
			// generate timestamp from base date
			baseDateString = String.format("%04d-%02d-%02d 14:00:0.0",
							baseDates[i][2],baseDates[i][1],baseDates[i][0]);
			// .. to get the time in millisec.
			baseDateAsTimestamp = Timestamp.valueOf(baseDateString);
			// deviation: deviation needs to be calculated for positive + negative
			deviation = Math.round(Math.random()*standardDeviation*2)-standardDeviation;
			Timestamp newStamp = new Timestamp(baseDateAsTimestamp.getTime()+deviation);
			timestamps[i]= newStamp;
		}
		return timestamps;
	}
	
	public String assemblePath(int baseIndex, int midIndex, String dsf, String pathEnd, int randomNumber) {
		String output;
		String randomNumberFolder = "";
		if (randomNumber>0) {
			randomNumberFolder = "/random_" + randomNumber ;
		}
		if (dsf==null || dsf.isEmpty()) {
			output = UClassifierTrainingDataGenerator.basePaths[baseIndex] + randomNumberFolder + UClassifierTrainingDataGenerator.midPaths[midIndex] + pathEnd;
		} else {
			output = UClassifierTrainingDataGenerator.basePaths[baseIndex] + randomNumberFolder + UClassifierTrainingDataGenerator.midPaths[midIndex] + "/" + dsf + pathEnd;
		}
		return output; 
	}
	
	
	public String dateAsStringWithPattern(int pattern, int day, int month, int year, String seperator) {
		String date = "";
		// normalize to two digits for month + day
		String dayString = String.format("%02d", day);
		String monthString = String.format("%02d", month);
		// apply pattern
		switch (pattern) {
		case UClassifierTrainingDataGenerator.DMY:
			date = dayString + seperator + monthString + seperator + year;
			break;
		case UClassifierTrainingDataGenerator.MDY:
			date = monthString + seperator + dayString + seperator + year;
			break;
		case UClassifierTrainingDataGenerator.YMD:
			date = year + seperator + monthString + seperator + dayString;
			break;
		}
		return date;
	}

	public int[] intVectorFromTimestam(Timestamp aTimestamp) {
		int [] dayMonthYear = new int [3];
		dayMonthYear[0] = UDatePatternEngine.getDayOfMonth(aTimestamp);
		dayMonthYear[1] = UDatePatternEngine.getMonth(aTimestamp);
		dayMonthYear[2] = UDatePatternEngine.getYear(aTimestamp);
		return dayMonthYear;
	}

	public UCopyJobDestination [] uCopyJobDestinationFromFilePathString(String [] filePathsString) {
		UCopyJobDestination [] destinations = new UCopyJobDestination [filePathsString.length];
		for (int i = 0; i < destinations.length; i++) {
			destinations[i] = new UCopyJobDestination(filePathsString[i]);
		}
		return destinations;
	}
	
	public UCopyJobObjects [] uCopyJobObjectsFromFilePathString(int numbOfObjects, String [] filePathsString) {
		UCopyJobObjects [] objects = new UCopyJobObjects [numbOfObjects];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = new UCopyJobObjects(filePathsString);
		}
		return objects;
	}
	
	public UClassifierTrainingDataSettings generateTrainingDataSettings(int settingID, int numberOfCopyJobs) {
		UClassifierTrainingDataSettings settings = new UClassifierTrainingDataSettings();
		switch (settingID) {
		case 1:	// Valid: incl DSF, random TimePattern
			settings.setToValid(numberOfCopyJobs, true);
			break;

		case 2: // Valid: no DSF, random TimePattern	
			settings.setToValid(numberOfCopyJobs, false);
			break;
			
		case 3: // Invalid: invalid Times, valid DSF
			settings.setToInvalid(numberOfCopyJobs, true, false);
			break;
			
		case 4: // Invalid, valid Times, invalid DSF	
			settings.setToInvalid(numberOfCopyJobs, false, true);
			break;
			
		case 5: // Invalid: invalid Times, invalid DSF	
			settings.setToInvalid(numberOfCopyJobs, true, true);
			break;
			
		}
		return settings;
	}
	
	public static void main(String[] args) throws Exception {

		///////////// Settings Training Data for ** Test ** ////////////
		// Settings for UClassifierModelEngineTest
//		int Valid_nmbCopyJobSeries = 50; // of each settings
//		int Invalid_nmbCopyJobSeries = 300; // of each settings
//		int Valid_copyJobsInSeriesMin = 7;  
//		int Valid_copyJobsInSeriesMax = 22;  
//		int Invalid_copyJobsInSeriesMin = 2;  
//		int Invalid_copyJobsInSeriesMax = 5;  
//		String fileNameTrainingData = UPersistanceService.FILENAME_GENERATED_TRAININGDATA_TEST;
//		int Valid_settingsIDstart = 1;
//		int Valid_settingsIDend = 2;
//		int Invalid_settingsIDstart = 3;
//		int Invalid_settingsIDend = 5;
//		// overwrite individual settings - otherwise leave null
//		 Boolean Valid_calendarNotRegularReptitionPattern= null;
//		 Boolean Valid_monthlyNotAnnualyCalendarPattern = null; 
//		 Boolean Invalid_calendarNotRegularReptitionPattern= null;
//		 Boolean Invalid_monthlyNotAnnualyCalendarPattern = null; 
//		 int Valid_averageInterval = -1; // untouched
//		 int Valid_standardDeviation = -1; // untouched
		///////////// Settings Training Data for ** Test **  ////////////
		
		///////////// Settings Training Data for ** Presentation ** ////////////
		int Valid_nmbCopyJobSeries = 200; // of each settings
		int Invalid_nmbCopyJobSeries = 200; // of each settings
		int Valid_copyJobsInSeriesMin = 3;  
		int Valid_copyJobsInSeriesMax = 3;  
		int Invalid_copyJobsInSeriesMin = 1;  
		int Invalid_copyJobsInSeriesMax = 2;  
		String fileNameTrainingData = UPersistanceService.FILENAME_GENERATED_TRAININGDATA_PRESENTATION;
		int Valid_settingsIDstart = 1;
		int Valid_settingsIDend = 1;
		int Invalid_settingsIDstart = 4;
		int Invalid_settingsIDend = 5;
		// overwrite individual settings - otherwise leave null
		 Boolean Valid_calendarNotRegularReptitionPattern= false;
		 Boolean Valid_monthlyNotAnnualyCalendarPattern = false; 
		 Boolean Invalid_calendarNotRegularReptitionPattern= true;
		 Boolean Invalid_monthlyNotAnnualyCalendarPattern = false;
		 int Valid_averageInterval = 20*1000; // 20 sec
		 int Valid_standardDeviation = 5*1000; // 1 sec

		///////////// Settings Training Data 2 ////////////
		
		///////////// Settings Training Data 2 ////////////
//		int Valid_nmbCopyJobSeries = 50; // of each settings
//		int Invalid_nmbCopyJobSeries = 200; // of each settings
//		int Valid_copyJobsInSeriesMin = 4;  
//		int Valid_copyJobsInSeriesMax = 4;  
//		int Invalid_copyJobsInSeriesMin = 1;  
//		int Invalid_copyJobsInSeriesMax = 2;  
//		String fileNameTrainingData = UPersistanceService.FILENAME_GENERATED_TRAININGDATA_TEST2;
//		int Valid_settingsIDstart = 1;
//		int Valid_settingsIDend = 2;
//		int Invalid_settingsIDstart = 3;
//		int Invalid_settingsIDend = 5;
//		// overwrite individual settings - otherwise leave null
//		 Boolean Valid_calendarNotRegularReptitionPattern= true;
//		 Boolean Valid_monthlyNotAnnualyCalendarPattern = true; 
//		 Boolean Invalid_calendarNotRegularReptitionPattern= false;
//		 Boolean Invalid_monthlyNotAnnualyCalendarPattern = false; 
//		 int Valid_averageInterval = -1; // untouched
//		 int Valid_standardDeviation = -1; // untouched
		///////////// Settings Training Data 2 ////////////
		
		///////////// Settings Training Data ////////////
//		int Valid_nmbCopyJobSeries = 50; // of each settings
//		int Invalid_nmbCopyJobSeries = 200; // of each settings
//		int Valid_copyJobsInSeriesMin = 7;  
//		int Valid_copyJobsInSeriesMax = 15;  
//		int Invalid_copyJobsInSeriesMin = 2;  
//		int Invalid_copyJobsInSeriesMax = 8;  
//		String fileNameTrainingData = UPersistanceService.FILENAME_GENERATED_TRAININGDATA_DEFAULT;
//		int Valid_settingsIDstart = 1;
//		int Valid_settingsIDend = 2;
//		int Invalid_settingsIDstart = 3;
//		int Invalid_settingsIDend = 5;
//		// overwrite individual settings - otherwise leave null
//		 Boolean Valid_calendarNotRegularReptitionPattern= null;
//		 Boolean Valid_monthlyNotAnnualyCalendarPattern = null; 
//		 Boolean Invalid_calendarNotRegularReptitionPattern= null;
//		 Boolean Invalid_monthlyNotAnnualyCalendarPattern = null; 
//		 int Valid_averageInterval = -1; // untouched
//		 int Valid_standardDeviation = -1; // untouched
		///////////// Settings Training Data ////////////
		
		UClassifierTrainingDataSettings settings = null;
		UClassifierTrainingDataGenerator generator = new UClassifierTrainingDataGenerator();
		// Valid training data
		int seriesLength = 0;
		int Valid_max_size_cjs = Valid_copyJobsInSeriesMax-Valid_copyJobsInSeriesMin;
		int Invalid_max_size_cjs = Invalid_copyJobsInSeriesMax-Invalid_copyJobsInSeriesMin;
		HashMap<Integer,UCopyJobSeries> trainingData = new HashMap<Integer,UCopyJobSeries>();
		for (int j = Valid_settingsIDstart; j <= Valid_settingsIDend; j++) {
			// generate random size of UCopyJobSeries
			seriesLength = (int) Math.round(Math.random()*Valid_max_size_cjs)+Valid_copyJobsInSeriesMin;
			settings = generator.generateTrainingDataSettings(j, seriesLength);
			// overwrite individual settings
			if (Valid_calendarNotRegularReptitionPattern!=null) {
				settings.calendarNotRegularReptitionPattern = Valid_calendarNotRegularReptitionPattern;
			}
			if (Valid_monthlyNotAnnualyCalendarPattern!=null) {
				settings.monthlyNotAnnualyCalendarPattern = Valid_monthlyNotAnnualyCalendarPattern;
			}
			if (Valid_averageInterval>0) {
				settings.averageInterval = Valid_averageInterval;
			}
			if (Valid_standardDeviation>0) {
				settings.standardDeviation = Valid_standardDeviation;
			}
			for (int i = 0; i < Valid_nmbCopyJobSeries; i++) {
				UCopyJobSeries newCjs = generator.generateCopyJobSeries(settings); 
				trainingData.put(newCjs.getJobID(), newCjs);
			}
		}
		System.out.println(settings.toString());
		
		// Invalid training data
		for (int j = Invalid_settingsIDstart; j <= Invalid_settingsIDend; j++) {
			seriesLength = (int) Math.round(Math.random()*Invalid_max_size_cjs)+Invalid_copyJobsInSeriesMin;
			settings = generator.generateTrainingDataSettings(j,seriesLength);
			// overwrite individual settings
			if (Invalid_calendarNotRegularReptitionPattern!=null) {
				settings.calendarNotRegularReptitionPattern = Invalid_calendarNotRegularReptitionPattern;
			}
			if (Invalid_monthlyNotAnnualyCalendarPattern!=null) {
				settings.monthlyNotAnnualyCalendarPattern = Invalid_monthlyNotAnnualyCalendarPattern;
			}
			for (int i = 0; i < Invalid_nmbCopyJobSeries; i++) {
				UCopyJobSeries newCjs = generator.generateCopyJobSeries(settings); 
				trainingData.put(newCjs.getJobID(), newCjs);
			}
		}
		UPersistanceService.saveCopyJobsHistory(trainingData,fileNameTrainingData);
		System.out.println(settings.toString());
		System.out.print(UCopyJobService.copyJobHistoryToString(trainingData));
	}
}
