package com.chiemy.downloadengine;

public interface DownloadEngineListener<T extends Downloadable> {
	/**
	 * 状态改变时的回调
	 */
	void onStatusChange(IDownloadEngine<T> engine, T entity);

	/**
	 * 任务结束回调
	 * 
	 */
	void onError(IDownloadEngine<T> engine, T entity, Throwable erro);
}
