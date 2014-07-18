package de.htw_berlin.userinputprediction.copyjob;

import com.mucommander.commons.file.AbstractFile;

public class UCopyJobDestination {
	private UFilePath destUFilePaths;
	
	public UCopyJobDestination(AbstractFile destFolder) {
		this(destFolder.getAbsolutePath(true));
	}

	public UCopyJobDestination(String destFolder) {
		this.destUFilePaths = new UFilePath(destFolder);
	}
	
	public String toString() {
		return this.destUFilePaths.toString();
	}

	public UFilePath getDestUFilePaths() {
		return destUFilePaths;
	}
	
}
