package com.chiemy.downloadengine;

import java.util.HashMap;
import java.util.Map;


public final class DownloadEngineFactory {
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "downloadengine configuration can not be initialized with null";
	private DownloadEngineConfig mConfig;
	private Map<String, IDownloadEngine> enginesMap;
	private static DownloadEngineFactory instance;
	
	private DownloadEngineFactory(){
		enginesMap = new HashMap<String, IDownloadEngine>();
	}
	
	public static synchronized DownloadEngineFactory getInstance(){
		if(instance == null){
			instance = new DownloadEngineFactory();
		}
		return instance;
	}
	/**
	 * 默认的下载引擎配置
	 * @param config
	 */
	public void setDefaultConfig(DownloadEngineConfig config) {
		if (config == null) {
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
		}
		this.mConfig = config;
	}
	/**
	 * 根据tag获取下载引擎，如果没有则会创建一个新的
	 * @param tag
	 * @return
	 */
	public synchronized IDownloadEngine getDownloadEngine(String tag){
		IDownloadEngine engine = enginesMap.get(tag);
		if(engine == null){
			if (mConfig == null) {
				throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
			}
			engine = new DownloadEngine(mConfig);
		}
		return engine;
	}
}
