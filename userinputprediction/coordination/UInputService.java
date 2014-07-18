package de.htw_berlin.userinputprediction.coordination;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.util.FileSet;
import com.mucommander.text.Translator;
import com.mucommander.ui.dialog.QuestionDialog;
import com.mucommander.ui.main.MainFrame;

import de.htw_berlin.userinputprediction.utils.UPersistanceService;

public class UInputService { // singleton

	private static final Boolean START_WITH_PRESENTATION_DATA = true;
	private static UInputService instance;
	private UCopyJobService copyJobManager;
	private MainFrame mainFrame;

	private UInputService() {
		try {
			// Check for presentation mode
			if (this.START_WITH_PRESENTATION_DATA) {
				this.copyJobManager = new UCopyJobService(UPersistanceService.FILENAME_GENERATED_TRAININGDATA_PRESENTATION);
			} else {
				this.copyJobManager = new UCopyJobService();
			}
		} catch (Exception e) {
			// mmtd: exception-handling
			e.printStackTrace();
		}
	}

	public static synchronized UInputService getInstance(){
		if(instance == null){
			instance = new UInputService();
			instance.setMainFrame(null);
		}
		return instance;
	}

	public void copyJobExecuted (FileSet sourceFiles, AbstractFile destFolder) {
		try {
			this.copyJobManager.addNewCopyJob(sourceFiles, destFolder);
		} catch (Exception e) {
			// mmtd: exception-handling
			e.printStackTrace();
		}
	}
	
	public Boolean isAnswerYesOnPoUpWithMessag(String message) {
		Boolean answer = null;
		if (this.mainFrame!=null) {
			QuestionDialog dialog = new QuestionDialog(mainFrame, "Backup Automatization",
					message, mainFrame, new String[] {
							Translator.get("Yes"), Translator.get("No") },
					new int[] { 1, 0 }, 0);
			int val = dialog.getActionValue();
		} else {
			throw new IllegalStateException("swing mainframe not initialized");
		}
		return answer;
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

}
