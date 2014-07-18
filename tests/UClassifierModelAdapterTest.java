package de.htw_berlin.userinputprediction.tests;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instances;
import de.htw_berlin.userinputprediction.classification.UClassifierAdapter;
import de.htw_berlin.userinputprediction.classification.UFeatureAndDataSetEngine;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.utils.UPersistanceService;

public class UClassifierModelAdapterTest {
	private Instances trainingDataSet;

	@BeforeClass
	public void beforeClass() throws Exception {
		HashMap<Integer,UCopyJobSeries> labeledTrainingData = new HashMap<Integer, UCopyJobSeries>(); 
		this.trainingDataSet = UFeatureAndDataSetEngine.createDataSetFromCopyJobHistory(
				UPersistanceService.loadCopyJobHistory(
						UPersistanceService.FILENAME_GENERATED_TRAININGDATA_TEST
						),
				"preloaded_training_data", 
				UFeatureAndDataSetEngine.generateAttributeVector()
				);
		Assert.assertNotNull(this.trainingDataSet);
		Assert.assertTrue(this.trainingDataSet.numInstances()==1000);
	}

	@Test
	public void classifyTrainingData() throws Exception {
		Classifier aClassifier = UClassifierAdapter.initializedClassifierModel();
		aClassifier = UClassifierAdapter.trainFirstTimeClassifierModel(aClassifier, this.trainingDataSet);
		int sumPositive = 0;
		for (int i = 0; i < this.trainingDataSet.numInstances(); i++) {
			Boolean newLabel = UClassifierAdapter.classifyTestData(
					this.trainingDataSet.instance(i), 
					aClassifier
					);
			if (newLabel) {
				sumPositive++;
			}
		}
		float relativeSuccess = ((float)sumPositive) / this.trainingDataSet.numAttributes();
		// more correct than false classification
		Assert.assertTrue(relativeSuccess>0.5f);
	}

	@Test
	public void updateClassifierModel() throws Exception {
		int nmbInstances = this.trainingDataSet.numAttributes();
		// train with one fifth dataset
		int indexFirstFifth = nmbInstances/5;
		Instances fifthTrainingSet = new Instances (this.trainingDataSet,0,indexFirstFifth);
		Classifier aClassifier = UClassifierAdapter.initializedClassifierModel();
		aClassifier = UClassifierAdapter.trainFirstTimeClassifierModel(
				aClassifier, 
				fifthTrainingSet
				);
		Evaluation aTest = UClassifierAdapter.initializedClassifierModelEvaluation(this.trainingDataSet);
		aTest = UClassifierAdapter.evaluateModel(
				aClassifier, 
				aTest, 
				this.trainingDataSet
				);
		// get successrate
		double recognitionWithFifthTrainingDataSet = aTest.pctCorrect();
		// update with second half
		for (int i = indexFirstFifth+1; i < nmbInstances; i++) {
			aClassifier = (Classifier) UClassifierAdapter.updateClassifierModel(
					(UpdateableClassifier) aClassifier, 
					this.trainingDataSet.instance(i)
					);
		}
		// recognition must have improved
		aTest = UClassifierAdapter.evaluateModel(
				aClassifier, 
				aTest, 
				this.trainingDataSet
				);
		double recognitionWithWholeTrainingDataSet = aTest.pctCorrect();
		Assert.assertTrue(
				recognitionWithFifthTrainingDataSet<recognitionWithWholeTrainingDataSet
				);
	}

	@Test
	public void initAndTrainingAndEvaluationOfModel() throws Exception {
		Classifier aClassifier = UClassifierAdapter.initializedClassifierModel();
		Evaluation aTest = UClassifierAdapter.initializedClassifierModelEvaluation(this.trainingDataSet);
		// validate that classifier is untrained 
		try {
			aTest = UClassifierAdapter.evaluateModel(
					aClassifier, 
					aTest, 
					this.trainingDataSet
					);
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(e.getClass(), IllegalArgumentException.class);
		}
		// train classifier and test again
		aClassifier = UClassifierAdapter.trainFirstTimeClassifierModel(aClassifier, this.trainingDataSet); 
		aTest = UClassifierAdapter.evaluateModel(
				aClassifier, 
				aTest, 
				this.trainingDataSet
				);
		// After Training: Minimum success (50%+ recognition)
		Assert.assertTrue(aTest.pctCorrect()>50);
	}
}
