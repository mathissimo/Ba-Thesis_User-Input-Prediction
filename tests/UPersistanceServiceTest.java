package de.htw_berlin.userinputprediction.tests;

import java.sql.Timestamp;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.htw_berlin.userinputprediction.coordination.UCopyJobService;
import de.htw_berlin.userinputprediction.copyjob.UCopyJob;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobDestination;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobObjects;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.utils.UPersistanceService;

public class UPersistanceServiceTest {
	private static final String TESTFILE_NAME = "persistance/testfile.db4o"; 
	private HashMap<Integer,UCopyJobSeries> cjHistory1;
	private UCopyJobSeries cjs1,cjs11;
	@BeforeClass
	public void beforeClass() {// first Copyjob Series
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

		cj11 = new UCopyJob(cjo11, cjd11, ts11); 
		cj21 = new UCopyJob(cjo21, cjd21, ts21); 
		cj31 = new UCopyJob(cjo31, cjd31, ts31); 

		cjs11 = new UCopyJobSeries(cj11);
		cjs11.addCopyJob(cj21);
		cjs11.addCopyJob(cj31);
		cjs11.isLabel = false;

		// create Copyjob History
		cjHistory1 = new HashMap<Integer,UCopyJobSeries>();
		cjHistory1.put(cjs1.getJobID(), cjs1);
		cjHistory1.put(cjs11.getJobID(), cjs11);
	}

	@AfterClass
	public void afterClass() {
		Assert.assertTrue(UPersistanceService.deleteDB());
		Assert.assertTrue(UPersistanceService.deleteDB(UPersistanceServiceTest.TESTFILE_NAME));
		//		UPersistanceManager.deleteDB();
		//		UPersistanceManager.deleteDB(UPersistanceManagerTest.TESTFILE_NAME);
	}

	@BeforeTest
	public void beforeTest() {
		//		UPersistanceManager.deleteDB();
		//		UPersistanceManager.deleteDB(UPersistanceManagerTest.TESTFILE_NAME);
		Assert.assertTrue(UPersistanceService.deleteDB());
		Assert.assertTrue(UPersistanceService.deleteDB(UPersistanceServiceTest.TESTFILE_NAME));
	}

	@Test
	public void saveCopyJobsHistory() throws Exception  {
		Assert.assertTrue( 
				UPersistanceService.saveCopyJobsHistory(
						cjHistory1, 
						UPersistanceServiceTest.TESTFILE_NAME
						)
				);
		HashMap<Integer,UCopyJobSeries> loadedHistory = 
				UPersistanceService.loadCopyJobHistory(
						UPersistanceServiceTest.TESTFILE_NAME
						);
		Assert.assertEquals(
				loadedHistory.size(),
				cjHistory1.size()
				);
		Assert.assertEquals(
				UCopyJobService.numberOfCopyJobsInHistory(loadedHistory),
				UCopyJobService.numberOfCopyJobsInHistory(cjHistory1)
				);
		Assert.assertTrue(
				loadedHistory.containsKey(cjs1.getJobID())
				);
		Assert.assertTrue(
				loadedHistory.containsKey(cjs11.getJobID())
				);
		//		Assert.assertTrue(
		//				);
	}

	@Test
	public void saveKnownCopyJobs() throws Exception  {
		Assert.assertTrue( 
				UPersistanceService.saveKnownCopyJobs(
						cjHistory1 
						)
				);
		HashMap<Integer,UCopyJobSeries> loadedHistory = 
				UPersistanceService.loadKnownCopyJobs();
		Assert.assertEquals(
				loadedHistory.size(),
				cjHistory1.size()
				);
		Assert.assertEquals(
				UCopyJobService.numberOfCopyJobsInHistory(loadedHistory),
				UCopyJobService.numberOfCopyJobsInHistory(cjHistory1)
				);
		Assert.assertTrue(
				loadedHistory.containsKey(cjs1.getJobID())
				);
		Assert.assertTrue(
				loadedHistory.containsKey(cjs11.getJobID())
				);
	}
}
