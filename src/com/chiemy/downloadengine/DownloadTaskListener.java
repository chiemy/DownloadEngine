package com.chiemy.downloadengine;


public interface DownloadTaskListener<T extends Downloadable> {
	/**
	 * 状态改变时的回调
	 * 
	 * @param status
	 *            {@link DownloadStatus#STATUS_WAIT}、
	 *            {@link DownloadStatus#STATUS_RUNNING}、
	 *            {@link DownloadStatus#STATUS_STOPPED}、
	 *            {@link DownloadStatus#STATUS_FINISHED}四种状态
	 * @param filePath 保存下载文件的地址
	 * @param uniq 下载任务的唯一标识
	 * @param total 下载文件的总大小
	 * @param downloadSize 已下载的文件的大小
	 * @param speed 下载速度
	 */
	void onStatusChange(T t);

	/**
	 * 任务结束回调
	 * 
	 * @param uniq 下载任务的唯一标识
	 * @param filePath 保存下载文件的地址
	 * @param success
	 *            是否下载成功
	 * @param erro
	 *            下载失败的信息
	 */
	void onError(T t, Throwable erro);
}
