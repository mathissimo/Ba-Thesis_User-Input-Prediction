package de.htw_berlin.userinputprediction.tests;


import java.sql.Timestamp;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.htw_berlin.userinputprediction.copyjob.UCopyJob;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobDestination;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobObjects;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.properties.UCopyJobSeriesProperties;

public class UCopyJobSeriesPropertiesTest {

	@Test
	public void getAverageTimeInterval() {
		String [] sourcePaths1 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath1 = "/alpha/beta/gamma/copyToLocation/01.01.2001/";
		String [] sourcePaths2 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath2 = "/alpha/beta/gamma/copyToLocation/01.02.2001/";
		String [] sourcePaths3 = {"/alpha/beta/gamma1/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath3 = "/alpha/beta/gamma/copyToLocation/01.03.2001/";

		UCopyJobObjects cjo1 = new UCopyJobObjects (sourcePaths1);
		UCopyJobObjects cjo2 = new UCopyJobObjects (sourcePaths2);
		UCopyJobObjects cjo3 = new UCopyJobObjects (sourcePaths3);

		UCopyJobDestination cjd1 = new UCopyJobDestination(destPath1);
		UCopyJobDestination cjd2 = new UCopyJobDestination(destPath2);
		UCopyJobDestination cjd3 = new UCopyJobDestination(destPath3);

		Timestamp ts1 = Timestamp.valueOf("2000-01-19 0:0:0.0");
		Timestamp ts2 = Timestamp.valueOf("2000-01-22 0:0:0.0");
		Timestamp ts3 = Timestamp.valueOf("2000-01-23 0:0:0.0");

		UCopyJob cj1; 
		UCopyJob cj2; 
		UCopyJob cj3; 

		UCopyJobSeries cjs1;

		UCopyJobSeriesProperties prop1;

		cj1 = new UCopyJob(cjo1, cjd1, ts1); 
		cj2 = new UCopyJob(cjo2, cjd2, ts2); 
		cj3 = new UCopyJob(cjo3, cjd3, ts3); 

		cjs1 = new UCopyJobSeries(cj1);
		cjs1.addCopyJob(cj2);
		cjs1.addCopyJob(cj3);

		prop1 = new UCopyJobSeriesProperties(cjs1);

		Assert.assertEquals(prop1.getAverageTimeInterval(), 2*24*60*60*1000);
	}

	@Test
	public void getCalendarMatching() {
		String [] sourcePaths1 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath1 = "/alpha/beta/gamma/copyToLocation/01.01.2001/";
		String [] sourcePaths2 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath2 = "/alpha/beta/gamma/copyToLocation/01.02.2001/";
		String [] sourcePaths3 = {"/alpha/beta/gamma1/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath3 = "/alpha/beta/gamma/copyToLocation/01.03.2001/";

		UCopyJobObjects cjo1 = new UCopyJobObjects (sourcePaths1);
		UCopyJobObjects cjo2 = new UCopyJobObjects (sourcePaths2);
		UCopyJobObjects cjo3 = new UCopyJobObjects (sourcePaths3);

		UCopyJobDestination cjd1 = new UCopyJobDestination(destPath1);
		UCopyJobDestination cjd2 = new UCopyJobDestination(destPath2);
		UCopyJobDestination cjd3 = new UCopyJobDestination(destPath3);

		Timestamp ts1 = Timestamp.valueOf("2000-01-01 0:0:0.0");
		Timestamp ts2 = Timestamp.valueOf("2001-01-01 0:0:0.0");
		Timestamp ts3 = Timestamp.valueOf("2002-01-01 0:0:0.0");

		UCopyJob cj1; 
		UCopyJob cj2; 
		UCopyJob cj3; 

		UCopyJobSeries cjs1;

		UCopyJobSeriesProperties prop1;

		cj1 = new UCopyJob(cjo1, cjd1, ts1); 
		cj2 = new UCopyJob(cjo2, cjd2, ts2); 
		cj3 = new UCopyJob(cjo3, cjd3, ts3); 

		cjs1 = new UCopyJobSeries(cj1);
		cjs1.addCopyJob(cj2);
		cjs1.addCopyJob(cj3);

		prop1 = new UCopyJobSeriesProperties(cjs1);

		Assert.assertTrue(prop1.getCalendarMatching());
	}

	@Test
	public void getCopyJobCount() {
		String [] sourcePaths1 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath1 = "/alpha/beta/gamma/copyToLocation/01.01.2001/";
		String [] sourcePaths2 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath2 = "/alpha/beta/gamma/copyToLocation/01.02.2001/";
		String [] sourcePaths3 = {"/alpha/beta/gamma1/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath3 = "/alpha/beta/gamma/copyToLocation/01.03.2001/";

		UCopyJobObjects cjo1 = new UCopyJobObjects (sourcePaths1);
		UCopyJobObjects cjo2 = new UCopyJobObjects (sourcePaths2);
		UCopyJobObjects cjo3 = new UCopyJobObjects (sourcePaths3);

		UCopyJobDestination cjd1 = new UCopyJobDestination(destPath1);
		UCopyJobDestination cjd2 = new UCopyJobDestination(destPath2);
		UCopyJobDestination cjd3 = new UCopyJobDestination(destPath3);

		Timestamp ts1 = Timestamp.valueOf("2000-01-19 0:0:0.0");
		Timestamp ts2 = Timestamp.valueOf("2000-01-22 0:0:0.0");
		Timestamp ts3 = Timestamp.valueOf("2000-01-23 0:0:0.0");

		UCopyJob cj1; 
		UCopyJob cj2; 
		UCopyJob cj3; 

		UCopyJobSeries cjs1;

		UCopyJobSeriesProperties prop1;

		cj1 = new UCopyJob(cjo1, cjd1, ts1); 
		cj2 = new UCopyJob(cjo2, cjd2, ts2); 
		cj3 = new UCopyJob(cjo3, cjd3, ts3); 

		cjs1 = new UCopyJobSeries(cj1);
		cjs1.addCopyJob(cj2);
		cjs1.addCopyJob(cj3);

		prop1 = new UCopyJobSeriesProperties(cjs1);

		Assert.assertEquals(prop1.getCopyJobCount(), 3);
	}

	@Test
	public void getStandardDeviation() {
		String [] sourcePaths1 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath1 = "/alpha/beta/gamma/copyToLocation/01.01.2001/";
		String [] sourcePaths2 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath2 = "/alpha/beta/gamma/copyToLocation/01.02.2001/";
		String [] sourcePaths3 = {"/alpha/beta/gamma1/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath3 = "/alpha/beta/gamma/copyToLocation/01.03.2001/";

		UCopyJobObjects cjo1 = new UCopyJobObjects (sourcePaths1);
		UCopyJobObjects cjo2 = new UCopyJobObjects (sourcePaths2);
		UCopyJobObjects cjo3 = new UCopyJobObjects (sourcePaths3);

		UCopyJobDestination cjd1 = new UCopyJobDestination(destPath1);
		UCopyJobDestination cjd2 = new UCopyJobDestination(destPath2);
		UCopyJobDestination cjd3 = new UCopyJobDestination(destPath3);

		Timestamp ts1 = Timestamp.valueOf("2000-01-19 0:0:0.0");
		Timestamp ts2 = Timestamp.valueOf("2000-01-22 0:0:0.0");
		Timestamp ts3 = Timestamp.valueOf("2000-01-23 0:0:0.0");

		UCopyJob cj1; 
		UCopyJob cj2; 
		UCopyJob cj3; 

		UCopyJobSeries cjs1;

		UCopyJobSeriesProperties prop1;

		cj1 = new UCopyJob(cjo1, cjd1, ts1); 
		cj2 = new UCopyJob(cjo2, cjd2, ts2); 
		cj3 = new UCopyJob(cjo3, cjd3, ts3); 

		cjs1 = new UCopyJobSeries(cj1);
		cjs1.addCopyJob(cj2);
		cjs1.addCopyJob(cj3);

		prop1 = new UCopyJobSeriesProperties(cjs1);

		Assert.assertEquals(prop1.getAverageTimeInterval(), 2*24*60*60*1000);
	}
}
