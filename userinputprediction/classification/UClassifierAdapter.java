package de.htw_berlin.userinputprediction.classification;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instance;
import weka.core.Instances;

public class UClassifierAdapter {

	public static Classifier initializedClassifierModel() {
		Classifier aModel = (Classifier)new NaiveBayesUpdateable();
		return aModel;
	}
	
	public static Classifier trainFirstTimeClassifierModel(
			Classifier aModel, 
			Instances trainingDataSet
			) throws Exception {
		aModel.buildClassifier(trainingDataSet);
		return aModel;
	}
	
	public static UpdateableClassifier updateClassifierModel(
			UpdateableClassifier aModel, 
			Instance trainingData
			) throws Exception {
		aModel.updateClassifier(trainingData);
		return aModel;
	}
	
	public static Evaluation initializedClassifierModelEvaluation(
			Instances trainingDataSet
			) throws Exception {
		return new Evaluation(trainingDataSet);
	}
	
	public static Evaluation evaluateModel(
			Classifier aModel,
			Evaluation initializedTest, 
			Instances testDataSet) throws Exception {
		initializedTest.evaluateModel(aModel, testDataSet);
		return initializedTest;
	}
	
	public static String testResultsAsString(Evaluation aTest) {
		return aTest.toSummaryString();
	}
	
	public static Boolean classifyTestData(
			Instance testData, 
			Classifier aModel
			) throws Exception {
		double labelAttribIndex  = aModel.classifyInstance(testData);
		if (labelAttribIndex  == 0) { // first nominal value --> true
			return true;
		}
		return false;
	}

}
