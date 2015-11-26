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
		this.id = entity.getId();
		this.url = entity.getDownloadUrl();
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
	
	private String engineId; // 下载引擎的标识
	public void setEngineId(String id) {
		engineId = id;
	}
	
	public String getEngineId(){
		return engineId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadInfo other = (DownloadInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
    
    
}
