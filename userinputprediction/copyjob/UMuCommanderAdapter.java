package de.htw_berlin.userinputprediction.copyjob;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.util.FileSet;

public class UMuCommanderAdapter {

	public static String [] getFileSetAsStringArray (FileSet aFileSet) {
		int numberOfSourceFiles =0;
		String [] foundPaths = null;
		if (aFileSet!=null) {
			numberOfSourceFiles = aFileSet.capacity();
			foundPaths =new String [numberOfSourceFiles];
			for (int i = 0; i < numberOfSourceFiles; i++) {
				foundPaths[i] = UMuCommanderAdapter.getAbstractFileAsString(aFileSet.get(i));
			}
		}
		return foundPaths;
	}
	public static String getAbstractFileAsString(AbstractFile anAbstractFile) {
		return anAbstractFile.getAbsolutePath(true);
	}
}
