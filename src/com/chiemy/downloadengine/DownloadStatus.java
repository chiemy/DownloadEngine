package com.chiemy.downloadengine;

public class DownloadStatus {
    /**
     * The DownloadTask is currently running.
     */
    public static final int STATUS_RUNNING = 1;

    /**
     * The DownloadTask is stopped.
     */
    public static final int STATUS_STOPPED = 2;
    /**
     * The DownloadTask is waiting.
     */
    public static final int STATUS_WAIT = 3;
    
    public static final int STATUS_FINISHED = 4;
    
    public static final int STATUS_FAILED = 5;
    
    public static final int STATUS_CANCEL = 6;

}
