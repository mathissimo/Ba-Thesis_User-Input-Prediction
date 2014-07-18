package de.htw_berlin.userinputprediction.properties;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.htw_berlin.userinputprediction.copyjob.UCopyJobDestination;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.copyjob.UFilePath;

public class ULocationEngine {
	public static final String PATTERN_VALIDATION_FAIL = "no_pattern_found";
	public static final int DSF_SEARCH_ALL_EQUAL = -1;
	public static final int DSF_SEARCH_FAILED = -1000;
	public static final float LVDIST_FAILED = -1000f;
	public static final String [] DATE_DIGIT_SEPERATORS = {"-",".","_",",",":"," ",""};
;
	
	
	
	public static Boolean allDestinationsAreEqual(UCopyJobSeries aCopyJobSeries) {
		UFilePath[] filePaths = ULocationEngine.destinationUFilePathFromUCopyJobSeries(aCopyJobSeries);
		return ULocationEngine.allPathsAreEqual(filePaths);
	}
	
	public static Boolean allPathsAreEqual(UFilePath[] filePaths) {
		Boolean equalPaths =false;
		if (ULocationEngine.allPathHaveSameDepth(filePaths)) {
			String refPath = filePaths[0].toString();
			equalPaths =true;
			for (UFilePath looper : filePaths) {
				if (!looper.toString().equals(refPath)) {
					equalPaths = false;
					break;
				}
			}
		}
		return equalPaths;
	}
	
	public static UFilePath[] destinationUFilePathFromUCopyJobSeries(UCopyJobSeries aCopyJobSeries) {
		List<UCopyJobDestination> allCopyJobsDestination = aCopyJobSeries.getCopyJobsDestinations();
		UFilePath[] filePaths = new UFilePath[allCopyJobsDestination.size()];
		for (int i = 0; i < allCopyJobsDestination.size(); i++) {
			filePaths[i]=allCopyJobsDestination.get(i).getDestUFilePaths();
		}
		return filePaths;
	}
	
	public static Boolean allPathHaveSameDepth (UFilePath[] filePaths) {
		Boolean sameFolderDepth = false;
		if (filePaths.length>1) {
			sameFolderDepth = true;
			int refFolderNumber = filePaths[0].getNumberOfDirs();
			for (UFilePath looper : filePaths) {
				if (looper.getNumberOfDirs()!=refFolderNumber) {
					// all path need to have the same depth. Otherwise error
					sameFolderDepth = false;
					break;
				}
			}
		}
		return sameFolderDepth;
	}
	
	public static Boolean constantSuperFolderPatternFound (UCopyJobSeries aCopyJobSeries) {
		// extract filePaths
		UFilePath [] filePaths = ULocationEngine.destinationUFilePathFromUCopyJobSeries(aCopyJobSeries);
		return ULocationEngine.constantSuperFolderPatternFound(filePaths);
	}
	
	public static Boolean constantSuperFolderPatternFound (UFilePath [] filePaths) {
		Boolean constantPattern = false;
		// determine index of Dynamic Super Folder
		int dsfIndex = ULocationEngine.indexOfDynamicSuperFolder(filePaths);
		// if successful check for a valid date-pattern at DSF 
		if (dsfIndex >= 0) {
			String foundPattern = ULocationEngine.validDatePatternFoundOnDirWithIndex(filePaths, dsfIndex);
			if (!foundPattern.equals(ULocationEngine.PATTERN_VALIDATION_FAIL)) {
				constantPattern = true;
			}
		}
		return constantPattern;
	}
	
	public static int indexOfDynamicSuperFolder(UCopyJobSeries aCopyJobSeries) {
		UFilePath[] filePaths = ULocationEngine.destinationUFilePathFromUCopyJobSeries(aCopyJobSeries);
		return ULocationEngine.indexOfDynamicSuperFolder(filePaths);
	}
	
	public static int indexOfDynamicSuperFolder(UFilePath[] filePaths) {
		int index = ULocationEngine.DSF_SEARCH_FAILED; // default: error
		int numberOfPaths = filePaths.length;
		// Min 2 paths necessary
		if (numberOfPaths>1) {
			int refFolderNumber = filePaths[0].getNumberOfDirs();
			// same folder depth required 
			if (ULocationEngine.allPathHaveSameDepth(filePaths)) {
				// compare prefix first
				Boolean prefixAllEqual = true;
				String refPrefix=filePaths[0].getPrefix();
				for (UFilePath looper : filePaths) {
					if (!looper.getPrefix().equals(refPrefix)) {
						prefixAllEqual = false;
						break;
					}
				}
				// than compare folder hierarchy
				if (prefixAllEqual) {
					// loop + compare from root folder to base dir
					String refFolderName;
					Boolean differentFolderFound = false;
					for (int i = 0; i < refFolderNumber; i++) {
						refFolderName = filePaths[0].getFolderNameAtIndex(i);
						// loop through filePaths
						for (UFilePath looper : filePaths) {
							if (!looper.getFolderNameAtIndex(i).equals(refFolderName)) {
								differentFolderFound = true;
								break;
							}
						}
						// if successfully compared all path up to level i, save i to index
						if (differentFolderFound) {
							// However, compare failed, if second differing folder is found later
							if (index==ULocationEngine.DSF_SEARCH_FAILED) {
								index = i;
								differentFolderFound = false;
							} else {
								index = ULocationEngine.DSF_SEARCH_FAILED;
								break;
							}
						} else {
							// no differences found, all path are equal:
							if (index==ULocationEngine.DSF_SEARCH_FAILED
									&& i==refFolderNumber-1) {
								index = ULocationEngine.DSF_SEARCH_ALL_EQUAL;
							}
						}
					}
				}
			}
		}
		return index;
	}
	
	public static String validDatePatternFoundOnDirWithIndex (UFilePath[] filePaths,int indexOfDir) {
		String [] dirNames = ULocationEngine.folderNamesAtHierarchieLevel(filePaths, indexOfDir);
		if (dirNames != null & dirNames.length>0) {
			return UDatePatternEngine.stringsConformsToValidDatePattern(dirNames);
		}
		return ULocationEngine.PATTERN_VALIDATION_FAIL;
	}
	
	public static String [] folderNamesAtHierarchieLevel (UFilePath[] filePaths,int indexOfDir){
		String [] dirNameOnlyArray = null;
		if (filePaths != null & filePaths.length>0) {
			dirNameOnlyArray = new String [filePaths.length];
			for (int i = 0; i < filePaths.length; i++) {
				if (filePaths[i].getNumberOfDirs()>indexOfDir) {
					// check if indexedDir exists and collect dirName
					dirNameOnlyArray [i] = filePaths[i].getDirHierarchy()[indexOfDir];
				} else {
					// else failed folder extraction
					dirNameOnlyArray = null;
					break;
				}
			}
		}
		return dirNameOnlyArray;
	}
	
	public static float alikeDestinationDSF(UCopyJobSeries aCopyJobSeries) {
		UFilePath[] filePaths = ULocationEngine.destinationUFilePathFromUCopyJobSeries(aCopyJobSeries);
		return ULocationEngine.alikeDestinationDSF(filePaths);
	}
	
	public static float alikeDestinationDSF(UFilePath[] filePaths) {
		float alikeDistance = 0.0f;
		// check for DSF and get index
		int indexDSF = ULocationEngine.indexOfDynamicSuperFolder(filePaths);
		// if a DSF could be identified..
		if (indexDSF>=0) {
			// extract Array of DSF names
			String [] destPathDSF = ULocationEngine.folderNamesAtHierarchieLevel(filePaths, indexDSF);
			float avrLvDist = ULocationEngine.avrLevenshteinDistNxM(destPathDSF);
			if (avrLvDist>=0) {
				alikeDistance = avrLvDist;
			}
		}
		return alikeDistance;
	}
	
	public static float avrLevenshteinDistNxM(String[] strings) {
		float distance = ULocationEngine.LVDIST_FAILED;
		if (strings.length>=2) {
			distance = 0.0f;
			try {
				// NxM loop over string-array
				for (int i = 0; i < strings.length; i++) {
					for (int j = i+1; j < strings.length; j++) {
						distance = distance + StringUtils.getLevenshteinDistance(strings[i], strings[j]);
					}
				}
			} catch (IllegalArgumentException e) {
				distance = ULocationEngine.LVDIST_FAILED;
			}
		}
		if (distance>0) {
			int numberOfCompares = strings.length*(strings.length-1)/2;
			return distance/numberOfCompares;
		}
		return distance;
	}
}
