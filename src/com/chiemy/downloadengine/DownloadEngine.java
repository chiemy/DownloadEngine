package com.chiemy.downloadengine;

import java.util.List;

final class DownloadEngine implements IDownloadEngine {
	
	private DownloadEngineConfig mConfig;
	
	public DownloadEngine(DownloadEngineConfig config){
		mConfig = config;
	}
	
	@Override
	public void start(Downloadable entity) {
	}

	@Override
	public void pause(Downloadable entity) {
	}

	@Override
	public void pauseAll() {
	}

	@Override
	public void delete(Downloadable entity) {
	}

	@Override
	public DownloadInfo getDownloadInfo(String uniq) {

		return null;
	}

	@Override
	public List<Downloadable> getAllFinished() {

		return null;
	}

	@Override
	public List<Downloadable> getAllUnFinished() {

		return null;
	}

	@Override
	public List<Downloadable> getAll() {

		return null;
	}

	@Override
	public void destroy() {
	}

}
