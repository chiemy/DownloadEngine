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

import java.sql.SQLException;
import java.util.List;

import com.chiemy.downloadengine.DownloadInfo;

/**
 * 
 */
public interface ISql {
    public void addDownloadTask(DownloadInfo task) throws SQLException;

    public void updateDownloadTask(DownloadInfo task) throws SQLException;

    public DownloadInfo queryDownloadTask(String id) throws SQLException;

    public void deleteDownloadTask(DownloadInfo task) throws SQLException;
    
    public List<DownloadInfo> queryAllUnFinishTask() throws SQLException;
    
    List<DownloadInfo> queryAllFinishedTask() throws SQLException;
}
