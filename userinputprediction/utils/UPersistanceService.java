package de.htw_berlin.userinputprediction.utils;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.TSerializable;
import com.db4o.ta.TransparentActivationSupport;

import de.htw_berlin.userinputprediction.copyjob.UCopyJobSeries;

public class UPersistanceService {


	public static final String FILENAME_GENERATED_TRAININGDATA_DEFAULT = "persistance/training_data.db4o";
	public static final String FILENAME_GENERATED_TRAININGDATA_TEST = "persistance/training_data_test.db4o";
	public static final String FILENAME_GENERATED_TRAININGDATA_TEST2 = "persistance/training_data_test2.db4o";
	public static final String FILENAME_GENERATED_TRAININGDATA_PRESENTATION = "persistance/training_data_presentation.db4o";
	
	private static final String FILENAME_DB_DEFAULT = "persistance/copyjob_history.db4o";

	public static HashMap<Integer,UCopyJobSeries> loadKnownCopyJobs() throws Exception {
		return UPersistanceService.loadCopyJobHistory(UPersistanceService.FILENAME_DB_DEFAULT);
	}

	public static HashMap<Integer,UCopyJobSeries> loadCopyJobHistory(String filename) throws Exception {
		HashMap<Integer,UCopyJobSeries> loadedJobs = new HashMap<Integer,UCopyJobSeries>();
		ObjectContainer db = UPersistanceService.connectDB(filename);
		if (db!=null) {
			try {
				ObjectSet<HashMap<Integer, UCopyJobSeries>> result = db.queryByExample(loadedJobs);
				// UPersistanceService.listResult(result);
				int resultCount = result.size();
				if (resultCount >0 ) {
					loadedJobs = result.get(0);
				}
				closeDB(db);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return loadedJobs;
	}

	public static Boolean saveKnownCopyJobs(HashMap<Integer,UCopyJobSeries> aCopyJobHashMap) throws Exception {
		return UPersistanceService.saveCopyJobsHistory(aCopyJobHashMap, UPersistanceService.FILENAME_DB_DEFAULT);
	}
	public static Boolean saveCopyJobsHistory(HashMap<Integer,UCopyJobSeries> aCopyJobHashMap,String filename) throws Exception {
		Boolean success = false;
		if (UPersistanceService.deleteDB(filename)) {
			ObjectContainer db = UPersistanceService.connectDB(filename);
			if (db!=null) {
				try {
					db.store(aCopyJobHashMap);
				} catch (Exception e) {
					throw e;
				} finally {
					success = closeDB(db);
				}
			}
		}
		return success;
	}

	public static Boolean deleteSavedCopyJobs () throws Exception {
		return UPersistanceService.deleteCopyJobHistory(UPersistanceService.FILENAME_DB_DEFAULT);
	}

	public static Boolean deleteCopyJobHistory (String filename) throws Exception {
		Boolean success = false;
		ObjectContainer db = UPersistanceService.connectDB(filename);
		if (db!=null) {
			try {
				HashMap<Integer,UCopyJobSeries> loadedJobs = null;
				ObjectSet result = db.queryByExample(loadedJobs);
				for (Object looper : result) {
					db.delete(looper);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				success = closeDB(db);
			}
		}
		return success;
	}


	public static void listResult(List<?> result){
		System.out.println("Loaded Results: "+result.size());
		for (Object o : result) {
			System.out.println(o);
		}
	}

	public static Boolean deleteDB() {
		return UPersistanceService.deleteDB(UPersistanceService.FILENAME_DB_DEFAULT);
	}
	public static Boolean deleteDB(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}

	private static ObjectContainer connectDB(String filename) throws Exception {
		ObjectContainer aDB = null;
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentActivationSupport());
		config.common().objectClass(Timestamp.class).translate(new TSerializable());
		try {
			aDB = Db4oEmbedded.openFile(
					config, 
					filename);
		} catch (Exception e) {
			throw e;
		}
		return aDB;
	}

	private static Boolean closeDB (ObjectContainer aDB) throws Exception {
		Boolean success = false;
		try {
			success=aDB.close();
		} catch (Exception e) {
			throw e;
		}
		return success;
	}

	// mmtd:
	// - Tippfehler bei DSF
	// - Schwankung ums Intervallgrenze

}
