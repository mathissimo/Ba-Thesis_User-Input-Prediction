package de.htw_berlin.userinputprediction.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.trainingdata.UClassifierTrainingDataGenerator;
import de.htw_berlin.userinputprediction.trainingdata.UClassifierTrainingDataSettings;

public class UClassifierTrainingDataGeneratorTest {
	private UClassifierTrainingDataGenerator gen;
	@BeforeClass
	public void beforeClass() {
		gen = new UClassifierTrainingDataGenerator();
	}


	@Test
	public void UClassifierTrainingDataGenerator() {
	}

	@Test
	public void createCopyJobSeries() {
	}

	@Test
	public void dateAsStringWithPattern() {
	}

	@Test
	public void generateDatePatternDSF() {
//		UClassifierTrainingDataSettings tdSettings = new UClassifierTrainingDataSettings();
//		tdSettings.setToInvalid (10, true, true);
//		UCopyJobSeries cjs = gen.generateCopyJobSeries(tdSettings);
//		System.out.print("\ncjs: \n"+cjs.toString());
		UClassifierTrainingDataSettings tdSettings = new UClassifierTrainingDataSettings();
		tdSettings.setToValid(10, true);
		tdSettings.calendarNotRegularReptitionPattern=true;
		tdSettings.monthlyNotAnnualyCalendarPattern=true;
		UCopyJobSeries cjs = gen.generateCopyJobSeries(tdSettings);
		System.out.print("\ncjs: \n"+cjs.toString());
//		Timestamp ts1 = Timestamp.valueOf("2000-01-19 12:0:0.0");
//		Timestamp ts2 = Timestamp.valueOf("2003-01-19 12:0:0.0");
//		String [] aArray = gen.generateRandomDatePatternDSF(10, ts1, ts2, UClassifierTrainingDataGenerator.DMY,1);
//		System.out.print("\n\n Array \n"+Arrays.toString(aArray)+"\n*****\n");
//		Timestamp ts1 = Timestamp.valueOf("2000-01-19 12:0:0.0");
//		Timestamp [] aArray = gen.generateTimestampSeriesWithPattern(ts1, 1*24*60*60*1000, 60*60*1000, 10);
//		System.out.print("\n\n Array \n"+Arrays.toString(aArray)+"\n*****\n");
//		String [] paths = gen.generatePathEnd(true, 20);
//		String [] paths = gen.generatePathSet(4, 10, true, true);
//		System.out.print("\n\n pathEnds\n"+Arrays.toString(paths)+"\n*****\n");
//		gen.generatePathSet(5, 9, true, true);
//		String [] dsfs = gen.generateDatePatternDSF(true, 20, UClassifierTrainingDataGenerator.DMY, 1, 11, 2000, 1);
//		System.out.print("\n\n DSFS\n"+Arrays.toString(dsfs)+"\n*****\n");
		
	}

	@Test
	public void generatePath() {
	}
	
}
