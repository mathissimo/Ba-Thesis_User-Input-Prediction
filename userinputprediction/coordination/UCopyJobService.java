package de.htw_berlin.userinputprediction.coordination;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.util.FileSet;

import de.htw_berlin.userinputprediction.copyjob.UCopyJob;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobDestination;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobObjects;
import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;
import de.htw_berlin.userinputprediction.copyjob.UMuCommanderAdapter;
import de.htw_berlin.userinputprediction.utils.UPersistanceService;

public class UCopyJobService {

	private static final String ASSITANCE_DEFAULT_MESSAGE = "Were you just perfoming a backup?";
	private HashMap<Integer,UCopyJobSeries> copyJobHistory;
	private UClassifierService classifierManager;

	public UCopyJobService() throws Exception {
		this.copyJobHistory = UPersistanceService.loadKnownCopyJobs();
		this.classifierManager = new UClassifierService(this.copyJobHistory);
	}

	public UCopyJobService(String trainingDataFilename) throws Exception {
		this.copyJobHistory = UPersistanceService.loadKnownCopyJobs();
		this.classifierManager = new UClassifierService(this.copyJobHistory,trainingDataFilename);
	}
	
	public void addNewCopyJob (FileSet sourceFiles, AbstractFile destFolder) throws Exception {
		// generate new Copyjob
		String [] sourceFilesAsString = UMuCommanderAdapter.getFileSetAsStringArray(sourceFiles);
		UCopyJobObjects newObjects = new UCopyJobObjects(sourceFilesAsString);
		UCopyJobDestination newDest = new UCopyJobDestination(destFolder);
		UCopyJob newCopyJob = new UCopyJob(newObjects,newDest,this.getCurrentTime());
		this.addNewCopyJob(newCopyJob);
	}

	public void addNewCopyJob (String [] sourceFiles, String destFolder) throws Exception {
		// generate new Copyjob
		UCopyJobObjects newObjects = new UCopyJobObjects(sourceFiles);
		UCopyJobDestination newDest = new UCopyJobDestination(destFolder);
		UCopyJob newCopyJob = new UCopyJob(newObjects,newDest,this.getCurrentTime());
		this.addNewCopyJob(newCopyJob);
	}

	public void addNewCopyJob (UCopyJob newCopyJob) throws Exception {
		// search for pre-existing Copyjob Series
		UCopyJobSeries dedicatedSeries = this.getCopyJobSeriesWithID(newCopyJob.getJobID());
		if (dedicatedSeries==null) {
			// No existing UCopyJobSeries matches > create new one
			dedicatedSeries = new UCopyJobSeries(newCopyJob);
			this.addCopyJobSeriesToHistory(dedicatedSeries);
		} else {
			// Update existing Copyjob Series
			dedicatedSeries.addCopyJob(newCopyJob);
		}
		System.out.println("\nNew Copyjob\n"+newCopyJob);
		System.out.println("\nDedicated Series:\n"+dedicatedSeries);
		// save to disk
		UPersistanceService.saveKnownCopyJobs(this.copyJobHistory);
		// classify CopyJobSeries if Backup
		if (this.classifierManager.classifyCopyJobSeries(dedicatedSeries)) {
			System.out.println("\n\n******** Backup Detected on: ********");
			System.out.println("\n\n"+dedicatedSeries.toString());
			Boolean feedback = this.offerAssistanceAndAskForFeedback();
			if (feedback!=null) {
				// update classifier
				this.classifierManager.updateClassifierWithCopyJobSeriesAndFeedback(
						dedicatedSeries, 
						feedback
						);
				// save data to disk
				UPersistanceService.saveKnownCopyJobs(this.copyJobHistory);
			}
		}
	}

	public UCopyJobSeries getCopyJobSeriesWithID (int seriesID) {
		UCopyJobSeries foundSeries = null;
		if (this.copyJobHistory !=null && !this.copyJobHistory.isEmpty()) {
			foundSeries=this.copyJobHistory.get(seriesID);
		}
		return foundSeries;
	}

	private void addCopyJobSeriesToHistory (UCopyJobSeries aCopyJobSeries) {
		this.copyJobHistory.put (aCopyJobSeries.getJobID(),aCopyJobSeries);
	}

	public Timestamp getCurrentTime (){
		return UCopyJobService.getCurrentTimeStatic();
	}

	public static String copyJobHistoryToString(HashMap<Integer,UCopyJobSeries> aCopyJobHistory) {
		String history = "\n\n******* CopyJobHistory: *******\n\n";
		Iterator looperCopyJobSeries = aCopyJobHistory.entrySet().iterator();
		while (looperCopyJobSeries.hasNext()) {
			Map.Entry pairs = (Map.Entry)looperCopyJobSeries.next();
			history=history.concat(((UCopyJobSeries)pairs.getValue()).toString());
		}
		history=history.concat("\n\n******* CopyJobHistory *******\n\n");
		return history;
	}

	public static int numberOfCopyJobsInHistory(HashMap<Integer,UCopyJobSeries> aCopyJobHistory) {
		int number = 0;
		Collection <UCopyJobSeries> seriesCollection = aCopyJobHistory.values();
		for (UCopyJobSeries seriesLooper : seriesCollection) {
			number = number + seriesLooper.getNumberOfCopyJobs();
		}
		return number;
	}

	public Boolean offerAssistanceAndAskForFeedback() throws Exception {
		// mmtd ** Ask for Feedback **
		String assistanceMessage = ASSITANCE_DEFAULT_MESSAGE;
		Boolean feedback = UInputService.getInstance().isAnswerYesOnPoUpWithMessag(assistanceMessage);
		return feedback;
	}

	private void printMap(Map mp) {
		System.out.println("\n\n***************** printMap: *****************");
		Iterator it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}
		System.out.println("***************** printMap: *****************\n\n");
	}

	public static Timestamp getCurrentTimeStatic() {
		return new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	}

	public Boolean deletePersistance () throws Exception {
		Boolean success = UPersistanceService.deleteDB();
		if (success) {
			this.copyJobHistory = UPersistanceService.loadKnownCopyJobs();
		}
		return success;
	}

}
