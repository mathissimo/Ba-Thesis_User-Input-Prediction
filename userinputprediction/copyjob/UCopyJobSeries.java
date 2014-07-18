package de.htw_berlin.userinputprediction.copyjob;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.htw_berlin.userinputprediction.properties.UCopyJobSeriesProperties;

public class UCopyJobSeries {
	private HashMap<Integer,UCopyJob> copyJobSeries;
	public Boolean isLabel = null; // default for unlabeld Copyjob Series

	public UCopyJobSeries(UCopyJob aCopyJob) {
		this.copyJobSeries = new HashMap<Integer,UCopyJob>();
		this.addCopyJob(aCopyJob);
	}
	
	public Timestamp[] getCopyJobsTimeEvent() {
		Timestamp [] eventTimeList = new Timestamp [this.copyJobSeries.size()];
		int i = 0;
		for (UCopyJob looper : this.copyJobSeries.values()) {
			eventTimeList[i]= looper.getEventTime();
			i++;
		}
		return eventTimeList;
	}
	
	public List<UCopyJobDestination> getCopyJobsDestinations() {
		List<UCopyJobDestination> destinations = new ArrayList<UCopyJobDestination>();
		for (UCopyJob looper : this.copyJobSeries.values()) {
			destinations.add(looper.getDestination());
		}
		return destinations;
	}
	
	public UCopyJobSeriesProperties getProperties() {
		UCopyJobSeriesProperties currentProps = new UCopyJobSeriesProperties(this);
		return currentProps;
	}
	
	public String toString() {
		String labelString = "[no label]";
		if (this.isLabel!=null) {
			labelString = this.isLabel.toString();
		}
		String cjsAsString = "\n\nUCopyJobSeries\nlabel: "+labelString+"\n";
		for (UCopyJob looper : this.copyJobSeries.values()) {
			cjsAsString = cjsAsString + "  " + looper.toString() + "\n";
		}
		return cjsAsString;
	}
	
	public int getPositionForCopyJob (UCopyJob aCopyJob) {
		int pos = 0;
		for (int i = 0; i < this.getNumberOfCopyJobs(); i++) {
			long looperTimeEvent = this.copyJobSeries.get(i).getEventTime().getTime();
			if (looperTimeEvent>aCopyJob.getEventTime().getTime()) {
				pos = i;
				break;
			} else {
				pos = i+1;
			}
		}
		return pos;
	}
	
	public void shiftCopyJobsInSeriesFromPosition(int position) {
		// iterate from the end
		for (int i = this.getNumberOfCopyJobs()-1; i >= position; i--) {
			this.copyJobSeries.put(i+1, this.copyJobSeries.remove(i));
		}
	}

	public UCopyJobObjects getObjects() {
		return this.copyJobSeries.get(0).getObjects();
	}
	
	public int getJobID() {
		return this.getObjects().getObjectsID();
	}
	
	public void addCopyJob (UCopyJob aCopyJob) {
		int newPos = this.getPositionForCopyJob(aCopyJob);
		this.shiftCopyJobsInSeriesFromPosition(newPos);
		this.copyJobSeries.put(newPos, aCopyJob);
	}
	
	public int getNumberOfCopyJobs() {
		return this.copyJobSeries.size();
	}

	public HashMap<Integer,UCopyJob> getCopyJobSeries() {
		return copyJobSeries;
	}
	
}
