package com.chiemy.downloadengine;

import android.text.TextUtils;

public class DownloadInfo {
	// id
	private String id;
	// 资源大小
	private long totalSize;
	// 已下载资源大小
	private long downloadSize;
	// 下载速度
	private long speed;
	// 下载地址
	private String url;
	// 保存路径
	private String filePath;
	// 保存文件名称
	private String fileName;
	// 开始下载时间
	private long startTime;
	// 结束下载时间
	private long endTime;
	// 下载状态
	private int status;
	// 资源类型
	private int type;
	private Downloadable entity;
	
	DownloadInfo(Downloadable entity){
		setEntity(entity);
	}
	
	public DownloadInfo(){
	}
	
	void setEntity(Downloadable entity) {
		this.entity = entity;
	}
	
	Downloadable getEntity() {
		return entity;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	public long getDownloadSize() {
		return downloadSize;
	}
	public void setDownloadSize(long downloadSize) {
		this.downloadSize = downloadSize;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isValidate() {
		return !TextUtils.isEmpty(url);
	}
	
	private UniqType uniqType = UniqType.UniqUrl;
	public String getUniq() {
    	String uniq = url;
    	if(uniqType == UniqType.UniqId){
    		uniq= id;
    	}
    	return uniq;
	}
	
    public void setUniqType(UniqType uniqType) {
		this.uniqType = uniqType;
	}
    
    public UniqType getUniqType() {
		return uniqType;
	}
}
