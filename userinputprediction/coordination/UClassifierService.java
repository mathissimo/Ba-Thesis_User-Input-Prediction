package de.htw_berlin.userinputprediction.coordination;

import java.util.Collection;
import java.util.HashMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.UpdateableClassifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import de.htw_berlin.userinputprediction.classification.UClassifierAdapter;
import de.htw_berlin.userinputprediction.classification.UFeatureAndDataSetEngine;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.utils.UPersistanceService;

public class UClassifierService {
	private static final int THRESHOLD_USING_PRELOADED_TRAININGDATA = 100;
	public Classifier classifierModel;
	public Evaluation continuesEvaluation;
	public FastVector featureVector;
	public Instances trainingDataSet;

	public UClassifierService(HashMap<Integer,UCopyJobSeries> aCopyJobHistory) throws Exception {
		this(aCopyJobHistory, UPersistanceService.FILENAME_GENERATED_TRAININGDATA_DEFAULT);
	}
	
	public UClassifierService(HashMap<Integer,UCopyJobSeries> aCopyJobHistory, String testDataFileName) throws Exception {
		this.featureVector = UFeatureAndDataSetEngine.generateAttributeVector();
		HashMap<Integer,UCopyJobSeries> labeledTrainingData = this.labeledCopyJobSeriesFromHistory(aCopyJobHistory);
		String trainingDataSetLabel = "historic_training_data";
		// if not sufficient labeled historic data available..
		if (labeledTrainingData.size()<UClassifierService.THRESHOLD_USING_PRELOADED_TRAININGDATA) { // first c
			// .. mix with training data
			labeledTrainingData.putAll(
					UPersistanceService.loadCopyJobHistory(testDataFileName)
					);
			trainingDataSetLabel = "historic_and_preloaded_training_data";
		}
		// init model
		this.classifierModel = UClassifierAdapter.initializedClassifierModel();
		// get prepared trainingdata
		this.trainingDataSet = UFeatureAndDataSetEngine.createDataSetFromCopyJobHistory(
				labeledTrainingData, 
				trainingDataSetLabel, 
				this.featureVector
				);
		// init evaluation
		this.continuesEvaluation = UClassifierAdapter.initializedClassifierModelEvaluation(trainingDataSet);
		// train classifiert
		this.classifierModel = UClassifierAdapter.trainFirstTimeClassifierModel(
				this.classifierModel, 
				trainingDataSet); 
		// update evaluation
		this.continuesEvaluation = UClassifierAdapter.evaluateModel(this.classifierModel, this.continuesEvaluation, trainingDataSet);
	}
	
	public Boolean classifyCopyJobSeries(UCopyJobSeries aCopyJobSeries) throws Exception {
		if (aCopyJobSeries.getProperties().isQualifiedForBackupAtHand()) {
			Instance newTestData = UFeatureAndDataSetEngine.createInstanceFromCopyJobSeries(
					aCopyJobSeries, 
					this.trainingDataSet, 
					this.featureVector
					);
			return UClassifierAdapter.classifyTestData(
					newTestData, 
					this.classifierModel
					);
		}
		return false;
	}
	
	public void updateClassifierWithCopyJobSeriesAndFeedback(UCopyJobSeries aCopyJobSeries, Boolean label) throws Exception {
		aCopyJobSeries.isLabel = label;
		Instance newTrainingData = UFeatureAndDataSetEngine.createInstanceFromCopyJobSeries(
				aCopyJobSeries, 
				this.trainingDataSet, 
				this.featureVector
				);
		this.updateClassifierWithTrainingData(newTrainingData);
	}
	
	public void updateClassifierWithTrainingData(Instance trainingData) throws Exception {
		this.classifierModel = (Classifier) UClassifierAdapter.updateClassifierModel((UpdateableClassifier)this.classifierModel, trainingData);
		this.trainingDataSet.add(trainingData);
		this.continuesEvaluation = UClassifierAdapter.evaluateModel(this.classifierModel, this.continuesEvaluation, this.trainingDataSet);
	}
	
	public HashMap<Integer,UCopyJobSeries> labeledCopyJobSeriesFromHistory(
			HashMap<Integer,UCopyJobSeries> aCopyJobHistory) {
		// loop through history and search for Copyjob Series with label-value set (i.e. not null)
		HashMap<Integer,UCopyJobSeries> labeldCopyJobSeries = new HashMap<Integer,UCopyJobSeries>();
		Collection <UCopyJobSeries> seriesCollection = aCopyJobHistory.values();
		for (UCopyJobSeries seriesLooper : seriesCollection) {
			if (seriesLooper.isLabel!=null) {
				labeldCopyJobSeries.put(seriesLooper.getJobID(), seriesLooper);
			}
		}
		return labeldCopyJobSeries;
	}
	
}
