package com.gt.util;

import java.io.Serializable;

public class FileStatusPojo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String filename;
	private String UCMupload;
	private Long ESSLoad;
	private Boolean DownloadDetails;
	private int total_status;
	
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUCMupload() {
		return UCMupload;
	}
	public void setUCMupload(String uCMupload) {
		UCMupload = uCMupload;
	}
	public Long getESSLoad() {
		return ESSLoad;
	}
	public void setESSLoad(Long eSSLoad) {
		ESSLoad = eSSLoad;
	}
	public Boolean getDownloadDetails() {
		return DownloadDetails;
	}
	public void setDownloadDetails(Boolean downloadDetails) {
		DownloadDetails = downloadDetails;
	}
	public int getTotal_status() {
		return total_status;
	}
	public void setTotal_status(int total_status) {
		this.total_status = total_status;
	}


}
