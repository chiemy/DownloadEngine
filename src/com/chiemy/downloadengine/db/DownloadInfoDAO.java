package com.chiemy.downloadengine.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chiemy.downloadengine.DownloadInfo;
import com.chiemy.downloadengine.DownloadStatus;
import com.chiemy.downloadengine.UniqType;

/**
 * 
 */
public class DownloadInfoDAO{
	public static final String TABLE_NAME = "downloadTask";
	private static DownloadInfoDAO instance;
	public static final String ID = "id";
	public static final String MIME_TYPE = "mimeType";
	public static final String NAME = "name";
	public static final String PATH = "path";
	public static final String SIZE = "size";
	public static final String START_TIME = "startTime";
	public static final String FINISH_TIME = "finishTime";
	public static final String STATUS = "status";
	public static final String TYEP = "type";
	public static final String URL = "url";
	public static final String DOWNLOADED_SIZE = "downloadedSize";
	public static final String ENGINE_TAG = "engine_tag";

	private DatabaseHelper databaseHelper = null;

	private Context mContext = null;

	private DownloadInfoDAO(Context context) {
		mContext = context;
	}

	public static DownloadInfoDAO getInstance(Context context) {
		if (instance == null || instance.mContext == null) {
			instance = new DownloadInfoDAO(context);
		}
		return instance;
	}

	/*
	 * You'll need this in your class to release the helper when done.
	 */
	public void release() {
		if (databaseHelper != null) {
			databaseHelper.close();
			databaseHelper = null;
		}
	}

	private DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(mContext);
		}
		return databaseHelper;
	}

	public void addDownloadTask(DownloadInfo info){
		if (info == null) {
			return;
		}
		SQLiteDatabase dao = getHelper().getWritableDatabase();
		DownloadInfo tempInfo = queryDownloadTask(info.getEngineId(), info.getUniq(), info.getUniqType());
		if (tempInfo == null) {
			ContentValues values = getContentValues(tempInfo);
			dao.insert(TABLE_NAME, null, values);
		} else {
			updateDownloadTask(info);
		}
	}

	public void updateDownloadTask(DownloadInfo info) {
		if (info == null) {
			return;
		}
		SQLiteDatabase dao = getHelper().getWritableDatabase();
		ContentValues values = getContentValues(info);
		String[] whereArgs = { String.valueOf(info.getUniq()) };
		String key = info.getUniqType() == UniqType.UniqUrl ? URL : ID;
		dao.update(TABLE_NAME, values, key + "=?", whereArgs);
	}
	
	/**
	 * 
	 * @param uniq 查询标识
	 * @param url 标识是否为url,如果不是，则按id处理
	 * @return
	 */
	public synchronized DownloadInfo queryDownloadTask(String engineId, String uniq, UniqType type){
		DownloadInfo ttask = null;
		SQLiteDatabase dao = getHelper().getReadableDatabase();
		String key = URL;
		if(type == UniqType.UniqId){
			key = ID;
		}
		String selection = key + "=? AND " + ENGINE_TAG + " = ?";
		String selectionArgs[] = {uniq, engineId};
		Cursor cursor = dao.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
		if (null != cursor) {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				ttask = getTask(cursor);
			}
			cursor.close();
		}
		return ttask;
	}

	public void deleteDownloadTask(DownloadInfo task){
		if(task == null){
			return;
		}
		deleteDownloadTask(task.getUniq(), task.getUniqType());
	}
	
	private synchronized void deleteDownloadTask(String uniq, UniqType type){
		SQLiteDatabase dao = getHelper().getWritableDatabase();
		String[] whereArgs = {uniq};
		String key = URL;
		if(type == UniqType.UniqId){
			key = ID;
		}
		dao.delete(TABLE_NAME, key + "=?", whereArgs);
	}
	

	public List<DownloadInfo> queryAllUnFinishTask(String engineId){
		List<DownloadInfo> tasks = null;
		try {
			tasks = queryByWhere(STATUS + "<>? AND " + ENGINE_TAG + "=?", String.valueOf(DownloadStatus.STATUS_FINISHED), engineId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tasks;
	}
	
	public List<DownloadInfo> queryAllFinishedTask(String engineId) { 
		List<DownloadInfo> tasks = null;
		try {
			tasks = queryByWhere(STATUS + "=? AND " + ENGINE_TAG + "=?", String.valueOf(DownloadStatus.STATUS_FINISHED), engineId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tasks;
	}
	
	public boolean isFinished(String uniq, UniqType type) {
		boolean exist = false;
		String key = URL;
		if(type == UniqType.UniqId){
			key = ID;
		}
		String selection = key + "=? AND " + STATUS + "=?";
		String selectionArgs[] = {uniq, String.valueOf(DownloadStatus.STATUS_FINISHED)};
		try {
			List<DownloadInfo> tasks = queryByWhere(selection, selectionArgs);
			exist = tasks.size() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exist;
	}
	
	private List<DownloadInfo> queryByWhere(String selection, String...selectionArgs) throws SQLException{
		List<DownloadInfo> tasks = new ArrayList<DownloadInfo>();
		SQLiteDatabase dao = getHelper().getReadableDatabase();
		Cursor cursor = dao.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
		if(cursor != null){
			while (cursor.moveToNext()) {
				DownloadInfo task = getTask(cursor);
				tasks.add(task);
			}
			cursor.close();
		}
		return tasks;
	}
	
	public void close(){
		if(databaseHelper != null){
			databaseHelper.close();
		}
	}
	

	private ContentValues getContentValues(DownloadInfo info) {
		ContentValues values = new ContentValues(11);
		values.put(ID, info.getId());
		values.put(NAME, info.getFileName());
		values.put(PATH, info.getFilePath());
		values.put(SIZE, info.getTotalSize());
		values.put(DOWNLOADED_SIZE, info.getDownloadSize());
		values.put(START_TIME, info.getStartTime());
		values.put(FINISH_TIME, info.getEndTime());
		values.put(STATUS, info.getStatus());
		values.put(TYEP, info.getType());
		values.put(URL, info.getUrl());
		values.put(ENGINE_TAG, info.getEngineId());
		return values;
	}

	private DownloadInfo getTask(Cursor cursor) {
		DownloadInfo task = new DownloadInfo();
		task.setId(cursor.getString(cursor.getColumnIndex(ID)));
		task.setFileName(cursor.getString(cursor.getColumnIndex(NAME)));
		task.setFilePath(cursor.getString(cursor.getColumnIndex(PATH)));
		task.setTotalSize(cursor.getLong(cursor.getColumnIndex(SIZE)));
		task.setStartTime(cursor.getLong(cursor.getColumnIndex(START_TIME)));
		task.setEndTime(cursor.getLong(cursor.getColumnIndex(FINISH_TIME)));
		task.setType(cursor.getInt(cursor.getColumnIndex(TYEP)));
		task.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
		task.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
		task.setDownloadSize(cursor.getLong(cursor
				.getColumnIndex(DOWNLOADED_SIZE)));
		task.setEngineId(cursor.getString(cursor.getColumnIndex(ENGINE_TAG)));
		return task;
	}

}
