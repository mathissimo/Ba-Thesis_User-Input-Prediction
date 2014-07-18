package de.htw_berlin.userinputprediction.tests;

import java.sql.Timestamp;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import de.htw_berlin.userinputprediction.classification.UFeatureAndDataSetEngine;
import de.htw_berlin.userinputprediction.copyjob.UCopyJob;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobDestination;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobObjects;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.properties.UCopyJobSeriesProperties;

public class UFeatureAndDataSetEngineTest {

	@Test
	public void createDataSetFromCopyJobSeries() {
		// first Copyjob Series
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

		Timestamp ts1 = Timestamp.valueOf("2000-01-19 0:10:0.0");
		Timestamp ts2 = Timestamp.valueOf("2000-02-19 0:50:0.0");
		Timestamp ts3 = Timestamp.valueOf("2000-03-19 0:10:0.0");

		UCopyJob cj1; 
		UCopyJob cj2; 
		UCopyJob cj3; 

		UCopyJobSeries cjs1;

		cj1 = new UCopyJob(cjo1, cjd1, ts1); 
		cj2 = new UCopyJob(cjo2, cjd2, ts2); 
		cj3 = new UCopyJob(cjo3, cjd3, ts3); 

		cjs1 = new UCopyJobSeries(cj1);
		cjs1.addCopyJob(cj2);
		cjs1.addCopyJob(cj3);
		cjs1.isLabel = true;

		// Second Copyjob Series
		String [] sourcePaths11 = {"/alpha/beta/gamma/hello1.txt", "/alpha/beta/gamma/hello2.txt"};
		String destPath11 = "/alpha/beta/gamma/copyToLocation/";
		String [] sourcePaths21 = {"/alpha/beta/gamma/hello1.txt", "/alpha/beta/gamma/hello2.txt"};
		String destPath21 = "/alpha/beta/gamma/copyToLocation/";
		String [] sourcePaths31 = {"/alpha/beta/gamma1/hello1.txt", "/alpha/beta/gamma/hello2.txt"};
		String destPath31 = "/alpha/beta/gamma/copyToLocation/";
		
		UCopyJobObjects cjo11 = new UCopyJobObjects (sourcePaths11);
		UCopyJobObjects cjo21 = new UCopyJobObjects (sourcePaths21);
		UCopyJobObjects cjo31 = new UCopyJobObjects (sourcePaths31);
		
		UCopyJobDestination cjd11 = new UCopyJobDestination(destPath11);
		UCopyJobDestination cjd21 = new UCopyJobDestination(destPath21);
		UCopyJobDestination cjd31 = new UCopyJobDestination(destPath31);
		
		Timestamp ts11 = Timestamp.valueOf("2000-01-19 0:10:0.0");
		Timestamp ts21 = Timestamp.valueOf("2000-01-20 0:50:0.0");
		Timestamp ts31 = Timestamp.valueOf("2000-01-21 0:10:0.0");
		
		UCopyJob cj11; 
		UCopyJob cj21; 
		UCopyJob cj31; 
		
		UCopyJobSeries cjs11;
		
		cj11 = new UCopyJob(cjo11, cjd11, ts11); 
		cj21 = new UCopyJob(cjo21, cjd21, ts21); 
		cj31 = new UCopyJob(cjo31, cjd31, ts31); 
		
		cjs11 = new UCopyJobSeries(cj11);
		cjs11.addCopyJob(cj21);
		cjs11.addCopyJob(cj31);
		cjs11.isLabel = false;
		
		// create Copyjob History
		HashMap<Integer,UCopyJobSeries> aCopyJobHistory1 = new HashMap<Integer,UCopyJobSeries>();
		aCopyJobHistory1.put(cjs1.getJobID(), cjs1);
		aCopyJobHistory1.put(cjs11.getJobID(), cjs11);
		
		FastVector featureVector = UFeatureAndDataSetEngine.generateAttributeVector();
		Instances aDataSet = UFeatureAndDataSetEngine.createDataSetFromCopyJobHistory(aCopyJobHistory1, "history1", featureVector);
		
		// test first Copyjob Series
		Instance anInstance = aDataSet.instance(0);

		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CAL_INT_MATCH)
						), 
						0.0d // -> weka internal numeric value for true
				);
		
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CONST_FOLD_PAT)
						), 
						0.0d // -> weka internal numeric value for true
				);
		
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_HOUR_INT_DEV)
						), 
						10.0d*60*1000/(60.0d*60*1000), //expected
						1.0d/(10^6) //tolerance
				);
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_LABEL)
						), 
						0.0d
				);
		
		// test second Copyjob Series
		anInstance = aDataSet.instance(1);
		
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CAL_INT_MATCH)
						), 
						1.0d // -> weka internal numeric value for false
				);
		
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CONST_FOLD_PAT)
						), 
						1.0d // -> weka internal numeric value for false
				);
		
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_HOUR_INT_DEV)
						), 
						10.0d*60*1000/(60.0d*60*1000), //expected
						1.0d/(10^6) //tolerance
				);
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_LABEL)
						), 
						1.0d
				);
	}

	@Test
	public void createInstanceFromCopyJobSeries() {
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

		Timestamp ts1 = Timestamp.valueOf("2000-01-19 0:10:0.0");
		Timestamp ts2 = Timestamp.valueOf("2000-02-19 0:50:0.0");
		Timestamp ts3 = Timestamp.valueOf("2000-03-19 0:10:0.0");

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
		cjs1.isLabel = true;

		FastVector featureVector = UFeatureAndDataSetEngine.generateAttributeVector();
		Instances aDataSet = new Instances("cjs1", featureVector, cjs1.getNumberOfCopyJobs());
		
		Instance anInstance = UFeatureAndDataSetEngine.createInstanceFromCopyJobSeries(
				cjs1, 
				aDataSet, 
				featureVector
				);

		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CAL_INT_MATCH)
						), 
						0.0d // -> weka internal numeric value for true
				);
		
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CONST_FOLD_PAT)
						), 
						0.0d // -> weka internal numeric value for false
				);
		
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_HOUR_INT_DEV)
						), 
						10.0d*60*1000/(60.0d*60*1000), //expected
						1.0d/(10^6) //tolerance
				);
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_LABEL)
						), 
						0.0d
				);
	}

	@Test
	public void createInstanceWith() {
		FastVector featureVector = UFeatureAndDataSetEngine.generateAttributeVector();
		Instances aDataSet = new Instances("EngineTest", featureVector, 10); 
		Instance anInstance = UFeatureAndDataSetEngine.createInstanceWith(
				aDataSet,
				featureVector, 
				0.5f, 
				true,   
				false, 
				true);
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CAL_INT_MATCH)
						), 
						0.0d // -> weka internal numeric value for true
				);
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_CONST_FOLD_PAT)
						), 
						1.0d // -> weka internal numeric value for false
				);
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_HOUR_INT_DEV)
						), 
						0.5d
				);
		Assert.assertEquals(
				anInstance.value(
						UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(
								UFeatureAndDataSetEngine.FT_LABEL)
						), 
						0.0d
				);
	}

}
