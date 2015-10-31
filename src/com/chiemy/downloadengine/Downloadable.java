package com.chiemy.downloadengine;


public interface Downloadable {
	String getId();

	String getDownloadUrl();
	
	void setDownloadInfo(DownloadInfo download);
	
	DownloadInfo getDownloadInfo();
}
