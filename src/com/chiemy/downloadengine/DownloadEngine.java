package com.chiemy.downloadengine;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiemy.downloadengine.db.DownloadInfoDAO;

final class DownloadEngine<T extends Downloadable> implements
		IDownloadEngine<T>, DownloadTaskListener{
	private DownloadEngineConfig mConfig;
	private DownloadInfoDAO infoDAO;
	private Map<String, DownloadTask> taskMap;

	public DownloadEngine(DownloadEngineConfig config) {
		mConfig = config;
		infoDAO = DownloadInfoDAO.getInstance(config.context);
		taskMap = new HashMap<String, DownloadTask>();
	}

	@Override
	public void start(T entity) {
		if (entity == null) {
			return;
		}
		DownloadInfo info = entity.getDownloadInfo();
		if (info == null) {
			info = infoDAO.queryDownloadTask(
					mConfig.uniqType == UniqType.UniqId ? entity.getId() : entity.getDownloadUrl(), mConfig.uniqType);
			if(info == null){
				info = new DownloadInfo(entity);
				info.setUniqType(mConfig.uniqType);
				entity.setDownloadInfo(info);
			}
		}
		DownloadTask tempTask = taskMap.get(info.getUniq());
		if(tempTask != null){
			tempTask.cancel(true);
		}
		try {
			DownloadTask task = new DownloadTask(info, mConfig.filePath);
			taskMap.put(info.getUniq(), task);
			// 开启下载任务
		} catch (MalformedURLException e) {
			e.printStackTrace();
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

	@SuppressWarnings("unchecked")
	@Override
	public void onStatusChange(DownloadInfo info) {
		if(mListner != null){
			mListner.onStatusChange(this, (T)info.getEntity());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onError(DownloadInfo info, Throwable erro) {
		if(mListner != null){
			mListner.onError(this, (T)info.getEntity(), erro);
		}
	}

	private DownloadEngineListener<T> mListner;
	@Override
	public void setDownloadEngineListener(DownloadEngineListener<T> listener) {
		mListner = listener;
	}

	@Override
	public DownloadEngineListener<T> getDownloadEngineListener() {
		return mListner;
	}

}
