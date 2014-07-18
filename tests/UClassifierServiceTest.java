package de.htw_berlin.userinputprediction.tests;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import weka.core.Instances;
import de.htw_berlin.userinputprediction.coordination.UClassifierService;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;

public class UClassifierServiceTest {
	private UClassifierService cMgr;
	private Instances trainingDataSet;
	
	@BeforeClass
	public void beforeClass() throws Exception {
		HashMap<Integer,UCopyJobSeries> labeledTrainingData = new HashMap<Integer, UCopyJobSeries>(); 
		this.cMgr = new UClassifierService(labeledTrainingData);
		this.trainingDataSet = this.cMgr.trainingDataSet;
	}

	@Test
	public void getGeneratedTrainingDataSet() {
		Assert.assertNotNull(this.trainingDataSet);
		Assert.assertEquals(
				this.trainingDataSet.numInstances(), 
				200  // equals number of copyjobseries in generated trainingdata
				);
	}
}
