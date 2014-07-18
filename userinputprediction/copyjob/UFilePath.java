package de.htw_berlin.userinputprediction.copyjob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class UFilePath implements Serializable {
	// examples for: /myDocuments/friends/Berlin/hello.txt
	private String fileName; // "hello.txt"
	private String fileExtension; // "txt"
	private String fileBaseName; // "hello"
	private String prefix; // "/", also e.g. "C:"
	private boolean typeFolderNotFile; // true
	private int numberOfDirs; // 3
	private int indexOfBaseDir; // 2
	private String [] dirHierarchy; // {"myDocuments","friends","Berlin"}
	
	public UFilePath (String filePath) {
		String pathNormalized = FilenameUtils.normalize(filePath,true); // converting win to unix format
		this.fileName = FilenameUtils.getName(pathNormalized);
		this.typeFolderNotFile = this.fileName.isEmpty();
		this.fileBaseName = FilenameUtils.getBaseName(pathNormalized);
		this.fileExtension = FilenameUtils.getExtension(pathNormalized);
		this.prefix = FilenameUtils.getPrefix(pathNormalized);
		String baseDir = FilenameUtils.getPathNoEndSeparator(pathNormalized);
		this.dirHierarchy = UFilePath.generateDirHirachyFromPathWithoutPrefix(baseDir);
		if (this.dirHierarchy==null) {
			this.numberOfDirs = 0;
			this.indexOfBaseDir =-1;
		} else {
			this.numberOfDirs = this.dirHierarchy.length;
			this.indexOfBaseDir = this.numberOfDirs -1;
		}
	}
	
	public static String[] generateDirHirachyFromPathWithoutPrefix (String absolutePath) {
		String [] generatedDirHirachy = null;
		if (absolutePath!=null && !absolutePath.isEmpty()) {
			List <String> newDirHirachy = new ArrayList <String>();
			// loop through path (bottom-up)
			while (!absolutePath.isEmpty()) {
				newDirHirachy.add(FilenameUtils.getName(absolutePath));
				absolutePath = FilenameUtils.getPathNoEndSeparator(absolutePath);
			}
			int numberOfPathElements = newDirHirachy.size();
			generatedDirHirachy = new String [numberOfPathElements];
			// Copy to String-array (in inverse order)
			for (int i = 0; i < numberOfPathElements; i++) {
				generatedDirHirachy[i]=newDirHirachy.get(numberOfPathElements-i-1);
			}
		}
		return generatedDirHirachy;
	}
	
	public int bottomUpIndexOfDirWithRegualarIndex (int index) {
		int bottomUpIndex = -1;
		if (index<this.numberOfDirs) {
			return (this.numberOfDirs-(index+1));
		}
		return bottomUpIndex;
	}
	
	public String getFilePathUpToIndex (int index) {
		String aggregatedFilePath = "";
		if (index>0 && index <= this.indexOfBaseDir) {
			aggregatedFilePath = aggregatedFilePath.concat(this.prefix);
			for (int i = 0; i <= index; i++) {
				aggregatedFilePath = aggregatedFilePath.concat(this.dirHierarchy[i]+"/");
			}
		}
		return aggregatedFilePath;
	}
	
	public String getFolderNameAtIndex(int index) {
		if (index>=0 && index <= this.indexOfBaseDir) {
			return this.dirHierarchy[index];
		} else {
			return null;
		}
	}
	
	public String getBaseDirPath() {
		String completeBaseDirPath = this.getFilePathUpToIndex(this.indexOfBaseDir); 
		return completeBaseDirPath;
	}
	
	public String toString() {
		String completeFilePath = this.getFilePathUpToIndex(this.indexOfBaseDir) 
				+ this.fileName;
		return completeFilePath;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getFileBaseName() {
		return fileBaseName;
	}

	public String getPrefix() {
		return prefix;
	}

	public boolean isTypeFolderNotFile() {
		return typeFolderNotFile;
	}

	public int getNumberOfDirs() {
		return numberOfDirs;
	}

	public int getIndexOfBaseDir() {
		return indexOfBaseDir;
	}

	public String[] getDirHierarchy() {
		return dirHierarchy;
	}


}
