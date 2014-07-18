package de.htw_berlin.userinputprediction.copyjob;

import java.sql.Timestamp;


public class UCopyJob {
	private UCopyJobObjects objects;
	private UCopyJobDestination destination;
	private Timestamp eventTime;
	
	public UCopyJob(UCopyJobObjects newObjects, UCopyJobDestination newDestination, Timestamp aTimestamp) {
		this.objects = newObjects;
		this.destination = newDestination;
		if (aTimestamp==null) {
		} else {
			this.eventTime = aTimestamp;
		}
	}
	
	public int getJobID() {
		return this.objects.getObjectsID();
	}

	public UCopyJobObjects getObjects() {
		return objects;
	}

	public UCopyJobDestination getDestination() {
		return destination;
	}

	public Timestamp getEventTime() {
		return eventTime;
	}
	
	public String toString() {
		String cjAsString = "\n\nUCopyJob (ID: "+this.getJobID()+")\n";
		cjAsString = cjAsString + "  Event Time:\n";
		cjAsString = cjAsString + this.eventTime.toString()+"\n";
		cjAsString = cjAsString + "  Sources:\n";
		cjAsString = cjAsString + this.objects.toString()+"\n";
		cjAsString = cjAsString + "  Destination:\n";
		cjAsString = cjAsString + "  " + this.destination.toString()+"\n";
		return cjAsString;
	}

}
