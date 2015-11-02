package com.chiemy.downloadengine;


interface DownloadTaskListener {
	/**
	 * 状态改变时的回调
	 */
	void onStatusChange(DownloadInfo info);

	/**
	 * 任务结束回调
	 * 
	 */
	void onError(DownloadInfo info, Throwable erro);
}
