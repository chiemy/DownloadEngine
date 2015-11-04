package com.chiemy.downloadengine;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.chiemy.downloadengine.db.DownloadInfoDAO;

final class DownloadEngine<T extends Downloadable> implements
		IDownloadEngine<T>, DownloadTaskListener {
	private static ExecutorService LIMITED_TASK_EXECUTOR;
	private static final int DEFAULT_TASK_NUM = 1;
	static {
		LIMITED_TASK_EXECUTOR = (ExecutorService) Executors
				.newFixedThreadPool(DEFAULT_TASK_NUM);
	}

	private DownloadEngineConfig mConfig;
	private DownloadInfoDAO infoDAO;
	private Map<String, DownloadTask> taskMap;
	private List<T> unfinishedList, finishedList;

	private static final int MSG_TASK = 1;
	private Handler handler = new Handler(Looper.getMainLooper()) {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MSG_TASK) {
				DownloadTask task = (DownloadTask) msg.obj;
				// 开启下载任务
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					forAPI11(task);
				} else {
					task.execute("");
				}
				if (mListener != null) {
					mListener.onStatusChange(DownloadEngine.this, (T) task
							.getDownloadInfo().getEntity());
				}
			}
		}
	};

	public DownloadEngine(DownloadEngineConfig config) {
		mConfig = config;
		infoDAO = DownloadInfoDAO.getInstance(config.context);
		taskMap = new HashMap<String, DownloadTask>();
		mUpdateInterval = mConfig.progressUploadInterval;
		unfinishedList = new ArrayList<T>();
		finishedList = new ArrayList<T>();
	}

	@Override
	public void start(T entity) {
		if (entity == null) {
			return;
		}
		DownloadInfo info = entity.getDownloadInfo();
		if (info == null) {
			info = infoDAO.queryDownloadTask(
					mConfig.uniqType == UniqType.UniqId ? entity.getId()
							: entity.getDownloadUrl(), mConfig.uniqType);
			if (info == null) {
				info = new DownloadInfo(entity);
				info.setUniqType(mConfig.uniqType);
				entity.setDownloadInfo(info);
			}
		}
		info.setStatus(DownloadStatus.STATUS_WAIT);
		start(info);
	}

	private void start(DownloadInfo info) {
		DownloadTask tempTask = taskMap.get(info.getUniq());
		if (tempTask != null) {
			tempTask.cancel(true);
		}
		try {
			final DownloadTask task = new DownloadTask(info, mConfig.filePath);
			taskMap.put(info.getUniq(), task);
			Message msg = handler.obtainMessage(MSG_TASK);
			msg.obj = task;
			handler.sendMessage(msg);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void forAPI11(DownloadTask task) {
		task.executeOnExecutor(LIMITED_TASK_EXECUTOR, "");
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

	private long preTime;
	private long mUpdateInterval;

	@SuppressWarnings("unchecked")
	@Override
	public void onStatusChange(DownloadInfo info) {
		int status = info.getStatus();
		if(status == DownloadStatus.STATUS_CANCEL){
			status = DownloadStatus.STATUS_STOPPED;
		}
		switch (status) {
		case DownloadStatus.STATUS_RUNNING:
			if (info.getStartTime() == 0) {
				info.setStartTime(System.currentTimeMillis());
			}
			if (mListener != null
					&& (System.currentTimeMillis() - preTime > mUpdateInterval || info
							.getTotalSize() == info.getDownloadSize())) {
				preTime = System.currentTimeMillis();
				mListener.onStatusChange(this, (T) info.getEntity());
			}
			break;
		case DownloadStatus.STATUS_WAIT:
		case DownloadStatus.STATUS_STOPPED:
			if (status == DownloadStatus.STATUS_STOPPED) {
				infoDAO.updateDownloadTask(info);
			}
			if (mListener != null) {
				mListener.onStatusChange(this, (T) info.getEntity());
			}
			break;
		case DownloadStatus.STATUS_FINISHED:
			info.setEndTime(System.currentTimeMillis());
			infoDAO.updateDownloadTask(info);
			taskMap.remove(info.getUniq());
			unfinishedList.remove(info.getEntity());
			finishedList.add((T) info.getEntity());
			if (mListener != null) {
				mListener.onStatusChange(this, (T) info.getEntity());
			}
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onError(DownloadInfo info, Throwable erro) {
		infoDAO.updateDownloadTask(info);
		if (mListener != null) {
			mListener.onError(this, (T) info.getEntity(), erro);
		}
	}

	private DownloadEngineListener<T> mListener;

	@Override
	public void setDownloadEngineListener(DownloadEngineListener<T> listener) {
		mListener = listener;
	}

	@Override
	public DownloadEngineListener<T> getDownloadEngineListener() {
		return mListener;
	}

}
