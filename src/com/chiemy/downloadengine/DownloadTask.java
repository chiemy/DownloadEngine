package com.chiemy.downloadengine;

import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.chiemy.downloadengine.error.DownloadException;
import com.chiemy.downloadengine.error.FileAlreadyExistException;
import com.chiemy.downloadengine.error.NoMemoryException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

final class DownloadTask implements Runnable {
	private URL URL;
	private String url;
	private File file, tempFile;
	private static final String TEMP_SUFFIX = ".download";
	
	private DownloadTaskListener listener;
	private DownloadInfo downloadInfo;
	private boolean isRunning;

	private static final int MSG_STATUS_CHANGED = 1;
	private static final int MSG_ERROR = 2;
	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_STATUS_CHANGED:
					if(listener != null){
						listener.onStatusChange(downloadInfo);
					}
					break;
				case MSG_ERROR:
					if(listener != null){
						listener.onError(downloadInfo, (Throwable) msg.obj);
					}
					break;
			}
		}
	};

	public DownloadTask(DownloadInfo info, String downloadPath)
			throws MalformedURLException {
		downloadInfo = info;
		url = info.getUrl();
		this.URL = new URL(url);
		String fileName = info.getFileName();
		if (TextUtils.isEmpty(fileName)) {
			try {
				fileName = URLDecoder.decode(URL.getFile(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		fileName = new File(fileName).getName();
		this.file = new File(downloadPath, fileName);
		this.tempFile = new File(downloadPath, fileName + TEMP_SUFFIX);
		downloadInfo.setFilePath(tempFile.getAbsolutePath());
	}
	
	public DownloadInfo getDownloadInfo() {
		return downloadInfo;
	}

	public void setListener(DownloadTaskListener listener) {
		this.listener = listener;
	}

	public boolean isRunning() {
		return isRunning;
	}

	private void onStatusChange(){
		Message msg = handler.obtainMessage(MSG_STATUS_CHANGED);
		handler.sendMessage(msg);
	}

	private void onError(Throwable t) {
		Message msg = handler.obtainMessage(MSG_ERROR);
		msg.obj = t;
		handler.sendMessage(msg);
	}

	@Override
	public void run() {
		isRunning = true;
		long result = doInBackground();
		isRunning = false;
		onPostExecute(result);
	}

	private Throwable error;
	protected Long doInBackground(String... params) {
		long result = 0;
		try {
			result = download();
		} catch (FileAlreadyExistException e) {
			error = e;
		} catch (NetworkErrorException e) {
			error = e;
		} catch (DownloadException e) {
			error = e;
		} catch (IOException e) {
			error = e;
		}
		return result;
	}

	protected void onPostExecute(Long result) {
		if(totalSize < 0){
			return;
		}
		boolean success = (totalSize == currentFileSize && totalSize > 0);
		downloadInfo.setTotalSize(totalSize);
		downloadInfo.setDownloadSize(currentFileSize);
		downloadInfo.setSpeed(0);
		downloadInfo.setStatus(DownloadStatus.STATUS_STOPPED);
		onStatusChange();
		if(success){
			tempFile.renameTo(file);
			String filePath = tempFile.getAbsolutePath();
			downloadInfo.setFilePath(filePath);
			downloadInfo.setStatus(DownloadStatus.STATUS_FINISHED);
			onStatusChange();
		}else{
			downloadInfo.setStatus(DownloadStatus.STATUS_FAILED);
			onError(error);
		}
	}
	
	private boolean interrupt = false;
	public void cancel() {
		interrupt = true;
		downloadInfo.setStatus(DownloadStatus.STATUS_CANCEL);
		downloadInfo.setSpeed(0);
		onStatusChange();
	}
	
	private long previousTime;
	protected void onProgressUpdate(Integer... values) {
		int downloadSize = values[0];
		int read = values[1];
		long currentTime = System.currentTimeMillis();
		long intervalMillis = currentTime - previousTime;
		float intervalSec = intervalMillis / 1000f;
		previousTime = currentTime;
		long speed = 0;
		if(intervalSec != 0){
			speed = (long)(read / intervalSec); // 字节/s
		}
		
		downloadInfo.setStatus(DownloadStatus.STATUS_RUNNING);
		downloadInfo.setTotalSize(totalSize);
		downloadInfo.setDownloadSize(downloadSize + previousFileSize);
		downloadInfo.setSpeed(speed);
		onStatusChange();
	}
	
	private HttpURLConnection connection;
	private long totalSize, currentFileSize, previousFileSize;
	private boolean finishIfFileExist;
	private Long download() throws IOException, NetworkErrorException, DownloadException {
		connection = createConnection();
		totalSize = connection.getContentLength();

		if (file.exists() && totalSize == file.length()) {
			if(finishIfFileExist){
				currentFileSize = totalSize;
				return currentFileSize;
			}else{
				throw new FileAlreadyExistException("文件已存在，跳过下载.");
			}
		} else if (tempFile.exists()) {
			previousFileSize = tempFile.length();
			currentFileSize = previousFileSize;
			if(currentFileSize == totalSize && totalSize > 0){
				return currentFileSize;
			}
			connection.disconnect();
			this.URL = new URL(url);
			connection = createConnection();
			connection.setRequestProperty("RANGE", "bytes=" + tempFile.length()+ "-");
		}
		
		checkUsableSpace();
		
		RandomAccessFile outputStream = new ProgressReportingRandomAccessFile(
				tempFile, "rw");
		if(totalSize > 0){
			onProgressUpdate(0, 0);
		}

		InputStream in = connection.getInputStream();
		long bytesCopied = copy(in, outputStream);

		currentFileSize = previousFileSize + bytesCopied;
		onProgressUpdate((int)currentFileSize, 0);
		
		if (currentFileSize != totalSize && totalSize != -1 && !interrupt) {
			throw new IOException("Download incomplete: " + bytesCopied + " != " + totalSize);
		}
		return currentFileSize;
	}

	private HttpURLConnection createConnection() throws IOException {
		HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
		connection.setConnectTimeout(10*1000);
		connection.setReadTimeout(10 * 1000);
		connection.setRequestProperty("User-Agent", "Mobile");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestMethod("GET");
		return connection;
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void checkUsableSpace() throws DownloadException{
		if(file.getParentFile().getUsableSpace() < (totalSize - currentFileSize)){
			throw new NoMemoryException("存储空间不足");
		}
	}
	
	public long copy(InputStream input, RandomAccessFile out) {
		if (input == null || out == null) {
			return -1;
		}
		byte[] buffer = new byte[1024];
		BufferedInputStream in = new BufferedInputStream(input, 1024);

		long count = 0;
		int n = 0;
		try {
			out.seek(out.length());
			while (!interrupt) {
				n = in.read(buffer, 0, 1024);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;
			}
		} catch (IOException e) {
			error = e;
		} finally{
			connection.disconnect();
			connection = null;
			try {
				out.close();
				in.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
		private int progress = 0;

		public ProgressReportingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {
			super(file, mode);
		}

		@Override
		public void write(byte[] buffer, int offset, int count)
				throws IOException {
			super.write(buffer, offset, count);
			progress += count;
			onProgressUpdate(progress, count);
		}
	}

}
