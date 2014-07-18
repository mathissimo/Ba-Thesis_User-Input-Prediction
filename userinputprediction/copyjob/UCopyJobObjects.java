package de.htw_berlin.userinputprediction.copyjob;

import java.io.Serializable;

public class UCopyJobObjects implements Serializable {
	private UFilePath [] sourceUFilePaths;
	private int objectsID;
	private int numberOfSources;
	
//	public UCopyJobObjects(FileSet sourceFiles) {
//		this(UCopyJobObjects.getSourcePathsAsString(sourceFiles));
//	}
	
	public UCopyJobObjects(String[] sourceFiles) {
		this.sourceUFilePaths = this.generateUFilePathsFromString(sourceFiles);
		this.numberOfSources=sourceFiles.length;
		this.objectsID = this.hashCodeFromPathSet();
	}
	
	private UFilePath [] generateUFilePathsFromString (String [] filePaths) {
		UFilePath [] newUFilePaths = null;
		if (filePaths!=null && filePaths.length>0) {
			newUFilePaths = new UFilePath[filePaths.length];
			for (int i = 0; i < filePaths.length; i++) {
				newUFilePaths[i]=new UFilePath(filePaths[i]);
			}
		}
		return newUFilePaths;
	}
	
	private int hashCodeFromPathSet() {
		String aggregatedPaths = new String();
		for (UFilePath looper : this.sourceUFilePaths) {
			aggregatedPaths=aggregatedPaths.concat(looper.toString());
		}
		return aggregatedPaths.hashCode();
	}

	public UFilePath[] getSourceUFilePaths() {
		return sourceUFilePaths;
	}
	
	public String[] getSourcePathsAsString() {
		int numberOfSourceFiles =0;
		String [] foundPaths = null;
		if (this.sourceUFilePaths!=null) {
			numberOfSourceFiles = this.sourceUFilePaths.length;
			foundPaths =new String [numberOfSourceFiles];
			for (int i = 0; i < numberOfSourceFiles; i++) {
				foundPaths[i] = this.sourceUFilePaths[i].toString();
			}
		}
		return foundPaths;
	}
	
	public String toString() {
		String cjOAsString = "UCopyJobObjects (ID: "+this.objectsID+")\n";
		String [] sourcepaths = this.getSourcePathsAsString();
		for (String looper : sourcepaths) {
			cjOAsString = cjOAsString + "  " + looper + "\n";
		}
		return cjOAsString;
	}


	public int getObjectsID() {
		return objectsID;
	}

	public int getNumberOfSources() {
		return numberOfSources;
	}

}
