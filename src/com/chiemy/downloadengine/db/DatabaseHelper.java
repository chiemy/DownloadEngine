/*
 * Copyright (C) 2013 Snowdream Mobile <yanghui1986527@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiemy.downloadengine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "downloader.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void close() {
        super.close();
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + DownloadInfoDAO.TABLE_NAME + "("
				+ "_id" + " integer PRIMARY KEY AUTOINCREMENT,"
				+ DownloadInfoDAO.ID + " text,"
				+ DownloadInfoDAO.MIME_TYPE + " text,"
				+ DownloadInfoDAO.NAME + " text,"
				+ DownloadInfoDAO.PATH + " text,"
				+ DownloadInfoDAO.SIZE + " text,"
				+ DownloadInfoDAO.DOWNLOADED_SIZE + " text,"
				+ DownloadInfoDAO.START_TIME + " text,"
				+ DownloadInfoDAO.FINISH_TIME + " text,"
				+ DownloadInfoDAO.STATUS + " integer,"
				+ DownloadInfoDAO.URL + " text,"
				+ DownloadInfoDAO.TYEP+ " text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
