package com.dbInsertion;

import java.sql.Timestamp;

public class Review {

	private Timestamp resultTme;
	private double granuPeriod;
	private String objName;
	private double cellId;
	private double callAttempts;
	public Review(Timestamp resultTme, double granuPeriod, String objName, double cellId, double callAttempts) {
		super();
		this.resultTme = resultTme;
		this.granuPeriod = granuPeriod;
		this.objName = objName;
		this.cellId = cellId;
		this.callAttempts = callAttempts;
	}
	public Timestamp getResultTme() {
		return resultTme;
	}
	public void setResultTme(Timestamp resultTme) {
		this.resultTme = resultTme;
	}
	public double getGranuPeriod() {
		return granuPeriod;
	}
	public void setGranuPeriod(double granuPeriod) {
		this.granuPeriod = granuPeriod;
	}
	public String getObjName() {
		return objName;
	}
	public void setObjName(String objName) {
		this.objName = objName;
	}
	public double getCellId() {
		return cellId;
	}
	public void setCellId(double cellId) {
		this.cellId = cellId;
	}
	public double getCallAttempts() {
		return callAttempts;
	}
	public void setCallAttempts(double callAttempts) {
		this.callAttempts = callAttempts;
	}
	
	
}
