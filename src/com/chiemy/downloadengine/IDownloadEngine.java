package com.chiemy.downloadengine;

import java.util.List;

/**
 * 下载引擎
 * @author chiemy
 *
 */
public interface IDownloadEngine<T extends Downloadable> {
	
	void setConfig(DownloadEngineConfig config);
	/**
	 * 开始下载
	 * @param entity
	 */
	void start(T entity);
	/**
	 * 暂停下载
	 * @param entity
	 */
	void pause(T entity);
	/**
	 * 暂停所有下载任务
	 */
	void pauseAll();
	/**
	 * 删除下载任务
	 * @param entity
	 */
	void delete(T entity);
	
	/**
	 * 根据下载任务的唯一标识获取下载信息
	 * @param uniq 下载任务的唯一标识
	 * @return
	 */
	T getDownloadInfo(String uniq);
	/**
	 * 获取已完成的任务
	 * @return
	 */
	List<T> getAllFinished();
	/**
	 * 获取未下载完成任务
	 * @return
	 */
	List<T> getAllUnFinished();
	/**
	 * 获取所有下载
	 * @return
	 */
	List<T> getAll();
	
	void setDownloadEngineListener(DownloadEngineListener<T> listner);
	
	DownloadEngineListener<T> getDownloadEngineListener();
	
	void destroy();
}
