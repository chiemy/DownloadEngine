package com.chiemy.downloadengine;

import android.content.Context;
import android.text.TextUtils;

import com.chiemy.downloadengine.utils.StorageUtils;

/**
 * 下载配置
 * @author chiemy
 *
 */
public final class DownloadEngineConfig {
	final int progressUploadInterval;
	final int threadPoolSize;
	final String filePath;
	final Context context;
	final UniqType uniqType;
	
	private DownloadEngineConfig(final Builder builder){
		threadPoolSize = builder.threadPoolSize;
		filePath = builder.filePath;
		progressUploadInterval = builder.progressUploadInterval;
		context = builder.context;
		uniqType = builder.uniqType;
	}
	
	public static class Builder{
		private Context context;
		public static final int DEFAULT_THREAD_POOL_SIZE = 1;
		public static final int DEFAULT_PROGRESS_UPLOAD_INTERVAL = 500;
		private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
		private int progressUploadInterval = DEFAULT_PROGRESS_UPLOAD_INTERVAL;
		private String filePath;
		private UniqType uniqType = UniqType.UniqUrl;
		
		public Builder(Context context){
			this.context = context.getApplicationContext();
		}
		
		/**
		 * 下载同时进行的任务个数<br>
		 * 默认为{@value #DEFAULT_THREAD_POOL_SIZE}
		 * @param threadPoolSize
		 */
		public Builder setThreadPoolSize(int threadPoolSize) {
			this.threadPoolSize = threadPoolSize;
			return this;
		}
		/**
		 * 设置进度条更新的最小时间间隔，单位ms。默认{@value #DEFAULT_PROGRESS_UPLOAD_INTERVAL}ms
		 * @param progressUploadInterval
		 */
		public Builder setProgressUploadInterval(int progressUploadInterval) {
			this.progressUploadInterval = progressUploadInterval;
			return this;
		}
		
		/**
		 * 设置下载文件保存路径
		 * @param path
		 * @return
		 */
		public Builder setFilePath(String path){
			filePath = path;
			return this;
		}
		
		/**
		 * 设置下载文件保存路径
		 * @param path
		 * @return
		 */
		public Builder setUniqType(UniqType type){
			this.uniqType = type;
			return this;
		}
		
		public DownloadEngineConfig build(){
			initEmptyFieldsWithDefaultValues();
			return new DownloadEngineConfig(this);
		}
		
		private void initEmptyFieldsWithDefaultValues(){
			if(TextUtils.isEmpty(filePath)){
				filePath = StorageUtils.getCachePath(context);
			}
		}
	}
}
