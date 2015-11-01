package com.chiemy.downloadengine;

import java.util.List;

final class DownloadEngine<T extends Downloadable> implements IDownloadEngine<T>{
	
	private DownloadEngineConfig mConfig;
	
	public DownloadEngine(DownloadEngineConfig config){
		mConfig = config;
	}
	
	@Override
	public void start(T entity) {
		DownloadInfo info = entity.getDownloadInfo();
		if (info == null) {
			entity.setDownloadInfo(new DownloadInfo(entity));
		}
	}

	@Override
	public void pause(T entity) {
	}

	@Override
	public void pauseAll() {
	}

	@Override
	public void delete(T entity) {
	}

	@Override
	public T getDownloadInfo(String uniq) {

		return null;
	}

	@Override
	public List<T> getAllFinished() {

		return null;
	}

	@Override
	public List<T> getAllUnFinished() {

		return null;
	}

	@Override
	public List<T> getAll() {

		return null;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void setConfig(DownloadEngineConfig config) {
		this.mConfig = config;
	}

}
