package de.htw_berlin.userinputprediction.tests;

import java.sql.Timestamp;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.htw_berlin.userinputprediction.coordination.UCopyJobService;
import de.htw_berlin.userinputprediction.copyjob.UCopyJob;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobDestination;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobObjects;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.utils.UPersistanceService;

public class UCopyJobServiceTest {
	private UCopyJobService aCopyJobManager;


	@BeforeMethod
	public void beforeMethod() throws Exception {
		aCopyJobManager = new UCopyJobService(UPersistanceService.FILENAME_GENERATED_TRAININGDATA_TEST2);
		Assert.assertNotNull(aCopyJobManager);
		Assert.assertTrue(aCopyJobManager.deletePersistance());
	}


	@Test
	public void addCopyJob1()  throws Exception {
		// Copyjob Series 1
		String [] sourcePaths1 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath1 = "/alpha/beta/gamma/copyToLocation/01.01.2001/";
		String [] sourcePaths2 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath2 = "/alpha/beta/gamma/copyToLocation/27.02.2001/";
		String [] sourcePaths21 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath21 = "/alpha/beta/gamma/copyToLocation/27.02.2001/";
		// Copyjob Series 2
		String [] sourcePaths3 = {"/alpha/beta/gamma1/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath3 = "/alpha/beta/gamma/copyToLocation/delta1/";
		// Copyjob Series 3
		String [] sourcePaths4 = {"/alpha/beta/gamma1/test1.txt", "/alpha/beta/gamma2/test2.txt"};
		String destPath4 = "/alpha/beta/gamma/copyToLocation/delta2/";
		// Copyjob Series 4
		String [] sourcePaths5 = {"/alpha/beta/gamma3/test1.txt", "/alpha/beta/gamma4/test2.txt"};
		String destPath5 = "/alpha/beta/gamma/copyToLocation/delta3/";

		UCopyJobObjects cjo1 = new UCopyJobObjects (sourcePaths1);
		UCopyJobObjects cjo2 = new UCopyJobObjects (sourcePaths2);
		UCopyJobObjects cjo21 = new UCopyJobObjects (sourcePaths21);
		UCopyJobObjects cjo3 = new UCopyJobObjects (sourcePaths3);
		UCopyJobObjects cjo4 = new UCopyJobObjects (sourcePaths4);
		UCopyJobObjects cjo5 = new UCopyJobObjects (sourcePaths5);

		UCopyJobDestination cjd1 = new UCopyJobDestination(destPath1);
		UCopyJobDestination cjd2 = new UCopyJobDestination(destPath2);
		UCopyJobDestination cjd21 = new UCopyJobDestination(destPath21);
		UCopyJobDestination cjd3 = new UCopyJobDestination(destPath3);
		UCopyJobDestination cjd4 = new UCopyJobDestination(destPath4);
		UCopyJobDestination cjd5 = new UCopyJobDestination(destPath5);

		UCopyJob cj1; 
		UCopyJob cj2; 
		UCopyJob cj21; 
		UCopyJob cj3; 
		UCopyJob cj4; 
		UCopyJob cj5;

		UCopyJobSeries cjs1;
		UCopyJobSeries cjs2;
		UCopyJobSeries cjs3;
		UCopyJobSeries cjs4;

		cj1 = new UCopyJob(cjo1, cjd1, aCopyJobManager.getCurrentTime()); 
		cj2 = new UCopyJob(cjo2, cjd2, aCopyJobManager.getCurrentTime()); 
		cj21 = new UCopyJob(cjo21, cjd21, aCopyJobManager.getCurrentTime()); 
		cj3 = new UCopyJob(cjo3, cjd3, aCopyJobManager.getCurrentTime()); 
		cj4 = new UCopyJob(cjo4, cjd4, aCopyJobManager.getCurrentTime()); 
		cj5 = new UCopyJob(cjo5, cjd5, aCopyJobManager.getCurrentTime()); 

		cjs1 = new UCopyJobSeries(cj1);
		cjs1.addCopyJob(cj2);
		cjs1.addCopyJob(cj21);
		cjs2 = new UCopyJobSeries(cj3);
		cjs3 = new UCopyJobSeries(cj4);
		cjs4 = new UCopyJobSeries(cj5);

		Assert.assertTrue(this.sameCopyJobObjects(cjo1, cjo1));
		Assert.assertTrue(this.sameCopyJobDestinations(cjd1, cjd1));
		Assert.assertTrue(this.sameCopyJob(cj1, cj1));
		Assert.assertTrue(this.sameCopyJobSeries(cjs1, cjs1));

		aCopyJobManager.addNewCopyJob(cj1);
		aCopyJobManager.addNewCopyJob(cj2);
		aCopyJobManager.addNewCopyJob(cj21);
		aCopyJobManager.addNewCopyJob(cj3);
		aCopyJobManager.addNewCopyJob(cj4);
		aCopyJobManager.addNewCopyJob(cj5);

		UCopyJobSeries foundSeries1 = aCopyJobManager.getCopyJobSeriesWithID(cj1.getJobID());
		UCopyJobSeries foundSeries2 = aCopyJobManager.getCopyJobSeriesWithID(cj3.getJobID());
		UCopyJobSeries foundSeries3 = aCopyJobManager.getCopyJobSeriesWithID(cj4.getJobID());
		UCopyJobSeries foundSeries4 = aCopyJobManager.getCopyJobSeriesWithID(cj5.getJobID());

		Assert.assertEquals(foundSeries1.getJobID(),cjs1.getJobID());
		Assert.assertEquals(foundSeries2.getJobID(),cjs2.getJobID());
		Assert.assertEquals(foundSeries3.getJobID(),cjs3.getJobID());
		Assert.assertEquals(foundSeries4.getJobID(),cjs4.getJobID());

		Assert.assertTrue(this.sameCopyJobSeries(foundSeries1, cjs1));
		Assert.assertTrue(this.sameCopyJobSeries(foundSeries2, cjs2));
		Assert.assertTrue(this.sameCopyJobSeries(foundSeries3, cjs3));
		Assert.assertTrue(this.sameCopyJobSeries(foundSeries4, cjs4));

	}

	@Test
	public void addCopyJob2()  throws Exception {
		// Copyjob Series 1
		String [] sourcePaths1 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String [] sourcePaths2 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String [] sourcePaths21 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String [] sourcePaths22 = {"/alpha/beta/gamma/test1.txt", "/alpha/beta/gamma/test2.txt"};
		String destPath1 = "/alpha/beta/gamma/copyToLocation/01.01.2001/";
		String destPath2 = "/alpha/beta/gamma/copyToLocation/01.02.2001/";
		String destPath21 = "/alpha/beta/gamma/copyToLocation/01.03.2001/";
		String destPath22 = "/alpha/beta/gamma/copyToLocation/01.04.2001/";

		UCopyJobObjects cjo1 = new UCopyJobObjects (sourcePaths1);
		UCopyJobObjects cjo2 = new UCopyJobObjects (sourcePaths2);
		UCopyJobObjects cjo21 = new UCopyJobObjects (sourcePaths21);
		UCopyJobObjects cjo22 = new UCopyJobObjects (sourcePaths22);

		UCopyJobDestination cjd1 = new UCopyJobDestination(destPath1);
		UCopyJobDestination cjd2 = new UCopyJobDestination(destPath2);
		UCopyJobDestination cjd21 = new UCopyJobDestination(destPath21);
		UCopyJobDestination cjd22 = new UCopyJobDestination(destPath22);

		UCopyJob cj1; 
		UCopyJob cj2; 
		UCopyJob cj21; 
		UCopyJob cj22; 

		UCopyJobSeries cjs1;

		Timestamp ts1 = Timestamp.valueOf("2000-01-19 0:0:0.0");
		Timestamp ts2 = Timestamp.valueOf("2000-02-19 0:0:0.0");
		Timestamp ts3 = Timestamp.valueOf("2000-03-19 0:0:0.0");
		Timestamp ts4 = Timestamp.valueOf("2000-04-19 0:0:0.0");

		cj1 = new UCopyJob(cjo1, cjd1, ts1); 
		cj2 = new UCopyJob(cjo2, cjd2, ts2); 
		cj21 = new UCopyJob(cjo21, cjd21, ts3); 
		cj22 = new UCopyJob(cjo22, cjd22, ts4); 

		cjs1 = new UCopyJobSeries(cj1);
		cjs1.addCopyJob(cj2);
		cjs1.addCopyJob(cj21);
		cjs1.addCopyJob(cj22);

		Assert.assertTrue(this.sameCopyJobObjects(cjo1, cjo1));
		Assert.assertTrue(this.sameCopyJobDestinations(cjd1, cjd1));
		Assert.assertTrue(this.sameCopyJob(cj1, cj1));
		Assert.assertTrue(this.sameCopyJobSeries(cjs1, cjs1));

		// Expecting a backup-case -> Popup
		// --> Exception expected, because Popup could not initialized
		try {
			aCopyJobManager.addNewCopyJob(cj1);
		} catch (Exception e) {
			Assert.assertEquals(e.getClass(), IllegalStateException.class);
		}
		try {
			aCopyJobManager.addNewCopyJob(cj2);
		} catch (Exception e) {
			Assert.assertEquals(e.getClass(), IllegalStateException.class);
		}
		try {
			aCopyJobManager.addNewCopyJob(cj21);
		} catch (Exception e) {
			Assert.assertEquals(e.getClass(), IllegalStateException.class);
		}
		try {
			aCopyJobManager.addNewCopyJob(cj22);
			Assert.fail(); 
		} catch (Exception e) {
			Assert.assertEquals(e.getClass(), IllegalStateException.class);
		}
	}

	public Boolean sameCopyJobSeries(UCopyJobSeries s1, UCopyJobSeries s2) {
		if (s1.getNumberOfCopyJobs() != s2.getNumberOfCopyJobs()) {
			return false;
		}
		HashMap<Integer,UCopyJob>  sMap1 = s1.getCopyJobSeries();
		HashMap<Integer,UCopyJob>  sMap2 = s2.getCopyJobSeries();
		for (int i = 0; i < sMap1.size(); i++) {
			if (!this.sameCopyJob(sMap1.get(i), sMap2.get(i))) {
				return false;
			}
		}
		return true;
	}

	public Boolean sameCopyJob (UCopyJob c1, UCopyJob c2) {
		if (this.sameCopyJobObjects(c1.getObjects(), c2.getObjects())) {
			return this.sameCopyJobDestinations(c1.getDestination(), c2.getDestination());
		}
		return false;
	}

	public Boolean sameCopyJobObjects (UCopyJobObjects o1, UCopyJobObjects o2) {
		Boolean result = true;
		if (o1.getNumberOfSources()!=o2.getNumberOfSources()) {
			return false;
		}
		String[] o1sources = o1.getSourcePathsAsString();
		String[] o2sources = o2.getSourcePathsAsString();
		for (int i = 0; i < o1sources.length; i++) {
			if (!o1sources[i].equals(o2sources[i])) {
				result = false;
			}
		}
		return result;
	}

	public Boolean sameCopyJobDestinations (UCopyJobDestination d1, UCopyJobDestination d2) {
		return d1.toString().equals(d2.toString());
	}
}
