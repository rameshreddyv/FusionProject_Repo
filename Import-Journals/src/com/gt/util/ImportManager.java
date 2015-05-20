package com.gt.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.gt.Config;

public class ImportManager {
	
	private static Logger logger = Logger.getLogger(ImportManager.class);
	String message;
	static List<String> files_list;
	
	static int counter=0;
	Config config;
	public ImportManager(Config config)
	{
		this.config=config;
	}
	public void ThreadsManger()
	{
		int threads_count=Integer.parseInt(config.getProperty(Config.THREADS_COUNT));
		files_list=new GetFilesList(config).GetFileNames(config.getProperty(Config.DEFAULT_CSV_FILE_PATH));
		ExecutorService executor=Executors.newFixedThreadPool(threads_count);
		for(int i=0;i<files_list.size();i++)
		{
			   	String value = (String) files_list.get(i);
				Runnable worker=new WorkerThread(value,config,counter);
				executor.execute(worker);
				counter++;
		}		
		executor.shutdown();
		while (!executor.isTerminated()) {   }  
		logger.info("Finished all threads");  
	

	}
}