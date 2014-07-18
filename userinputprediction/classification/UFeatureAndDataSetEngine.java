package de.htw_berlin.userinputprediction.classification;

import java.util.Collection;
import java.util.HashMap;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import de.htw_berlin.userinputprediction.coordination.UCopyJobService;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.properties.UCopyJobSeriesProperties;

public class UFeatureAndDataSetEngine {
	// Add new feature:
	// 1. alphabetic order, label is last
	// 2. Adapt NBR_FEATURES
	// 3. Adapt generation of FeatureVector attributes:
	//    generateAttributeVector ()
	// 4. Adapt methods
	//    stringForBoolean (int index, Boolean value)
	//    indexOfAttributeInFeatureVector(String aFeatureVectorName)
	public static final String FT_CAL_INT_MATCH = "calendar_interval_matching";
	public static final String FT_CONST_FOLD_PAT = "constant_super-folder_pattern";
	public static final String FT_HOUR_INT_DEV = "hourly_interval_deviation";
	public static final String FT_LABEL = "backup_case";
	public static final int NBR_FEATURES = 4;

	public static Instances createDataSetFromCopyJobHistory(
			HashMap<Integer,UCopyJobSeries> aCopyJobHistory, 
			String aDataSetName, 
			FastVector aFeatureVector 
			) {
		int numberOfInstances = UCopyJobService.numberOfCopyJobsInHistory(aCopyJobHistory);
		Instances aDataSet = UFeatureAndDataSetEngine.initDataSet(aDataSetName, aFeatureVector,numberOfInstances);
		Collection <UCopyJobSeries> seriesCollection = aCopyJobHistory.values();
		for (UCopyJobSeries seriesLooper : seriesCollection) {
			aDataSet.add(
					UFeatureAndDataSetEngine.createInstanceFromCopyJobSeries(
							seriesLooper,
							aDataSet,
							aFeatureVector)
					);
		}
		return aDataSet;
	}
	
	public static Instance createInstanceFromCopyJobSeries(
			UCopyJobSeries aCopyJobSeries,
			Instances aDataSet,
			FastVector aFeatureVector
			) {
		UCopyJobSeriesProperties properties = aCopyJobSeries.getProperties();
		return UFeatureAndDataSetEngine.createInstanceWith(
				aDataSet,
				aFeatureVector,
				properties.getAverageDeviationFromFullHour(), 
				properties.getCalendarMatching(), 
				properties.getConstantSuperFolderPattern(), 
				aCopyJobSeries.isLabel
				);
	}
	
	public static Instance createInstanceWith (
			Instances aDataSet,
			FastVector featureVector,
			float hourlyIntervalDeviation,
			Boolean calendarNotRegularTimePattern,
			Boolean constantSuperFolderPattern,
			Boolean labelValue
			) {
		// Create the instance
		Instance newInstance = new Instance(NBR_FEATURES);
		newInstance.setDataset(aDataSet);
		// insert feature-values
		newInstance.setValue(
				(Attribute) featureVector.elementAt(0), 
				UFeatureAndDataSetEngine.stringForBoolean(FT_CAL_INT_MATCH, calendarNotRegularTimePattern)
				);      
		newInstance.setValue(
				(Attribute) featureVector.elementAt(1), 
				UFeatureAndDataSetEngine.stringForBoolean(FT_CONST_FOLD_PAT, constantSuperFolderPattern)
				);      
		newInstance.setValue(
				(Attribute) featureVector.elementAt(2),
				hourlyIntervalDeviation
				);
		if (labelValue!=null) {
			newInstance.setValue(
					(Attribute) featureVector.elementAt(3), 
					UFeatureAndDataSetEngine.stringForBoolean(FT_LABEL, labelValue)
					);      
		}
		return newInstance;
	}
	
	public static Instances initDataSet (String aName, FastVector aFeatureVector, int numberOfData) {
		Instances newDataSet = new Instances(aName, aFeatureVector, numberOfData);
		newDataSet.setClassIndex(NBR_FEATURES-1);
		return newDataSet;
	}

	public static FastVector generateAttributeVector () {
		FastVector newFeatureVector = new FastVector(NBR_FEATURES);
		// Setup Feature (=Attributes)
		// Frequence Criterion
		Attribute hourlyIntervalDev = new Attribute(FT_HOUR_INT_DEV);
		FastVector calendarMatchingValues = new FastVector(2);
		// Nominal attribute: declare Values
		calendarMatchingValues.addElement(
				UFeatureAndDataSetEngine.stringForBoolean(FT_CAL_INT_MATCH, true));
		calendarMatchingValues.addElement(
				UFeatureAndDataSetEngine.stringForBoolean(FT_CAL_INT_MATCH, false));
		Attribute calendarMatching = 
				new Attribute(FT_CAL_INT_MATCH,calendarMatchingValues);

		// Location Criterion
		FastVector constantSuperFolderValues = new FastVector(2);
		// Nominal attribute: declare Values
		constantSuperFolderValues.addElement(
				UFeatureAndDataSetEngine.stringForBoolean(FT_CONST_FOLD_PAT, true));
		constantSuperFolderValues.addElement(
				UFeatureAndDataSetEngine.stringForBoolean(FT_CONST_FOLD_PAT, false));
		Attribute constantSuperFolder = 
				new Attribute(FT_CONST_FOLD_PAT,constantSuperFolderValues);

		// Label for Classification
		FastVector classificationValues = new FastVector(2);
		// Nominal attribute: declare Values
		classificationValues.addElement(
				UFeatureAndDataSetEngine.stringForBoolean(FT_LABEL, true));
		classificationValues.addElement(
				UFeatureAndDataSetEngine.stringForBoolean(FT_LABEL, false));
		Attribute classification = 
				new Attribute(FT_LABEL,classificationValues);

		// Aggregate feature vector in alphabetic order
		newFeatureVector.addElement(calendarMatching);
		newFeatureVector.addElement(constantSuperFolder);
		newFeatureVector.addElement(hourlyIntervalDev);
		newFeatureVector.addElement(classification);

		return newFeatureVector;
	}
	
	public static int indexOfAttributeInFeatureVector(String aFeatureVectorName) {
		int index = -1;
		if (aFeatureVectorName.equals(FT_CAL_INT_MATCH)) {
			index = 0;
		} else if (aFeatureVectorName.equals(FT_CONST_FOLD_PAT)) {
			index = 1;
		} else if (aFeatureVectorName.equals(FT_HOUR_INT_DEV)) {
			index = 2;
		} else if (aFeatureVectorName.equals(FT_LABEL)) {
			index = 3;
		}
		return index;
	}

	public static String stringForBoolean (String featureName, Boolean value) {
		return UFeatureAndDataSetEngine.stringForBoolean(
				UFeatureAndDataSetEngine.indexOfAttributeInFeatureVector(featureName), 
				value
				);
	}
	public static String stringForBoolean (int index, Boolean value) {
		String featureAttributeValueAsString = null;
		switch (index) {
		case 0: // FT_CAL_INT_MATCH
			if (value) {
				featureAttributeValueAsString = "calendar_matching" ;
			} else {
				featureAttributeValueAsString = "regular_timpe-pattern_matching" ;
			}
			break;
		case 1: // FT_CONST_FOLD_PAT
			if (value) {
				featureAttributeValueAsString = "constant_folder" ;
			} else {
				featureAttributeValueAsString = "dsf_used" ;
			}
			break;
		case 2: // FT_HOUR_INT_DEV: numerical value
			break;
		case 3: // FT_LABEL
			if (value) {
				featureAttributeValueAsString = "is_backup_case" ;
			} else {
				featureAttributeValueAsString = "no_backup_case" ;
			}
			break;
		}
		return featureAttributeValueAsString;
	}
}


