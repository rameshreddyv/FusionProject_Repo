/**
 * Copyright (c) 2014-15 GT. All rights reserved.
 */
package com.gt.core;

import static com.gt.Constants.CSV_EXT;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gt.Config;
import com.gt.Constants;
import com.gt.util.ImportManager;
import com.gt.util.MailSender;

/**
 * Class to initiate the import Journals
 * 
 * @author Krishna Mohan
 */
public class MultipleInit {

	private static Logger logger = Logger.getLogger(MultipleInit.class);

	/** all configurations are stored in this file */
	private static final String CONFIG_FILE = "importjournals.config";
	

	public static void main(String[] args) {

		/** Load LOG4J properties files */
		boolean exception = false;
		Config config = null;
		PropertyConfigurator.configure("log4j.properties");
		try {
			/** Load application default properties files */
			
			config = new Config(CONFIG_FILE);		
			
			String tempfolderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER);
			File tempfolder=new File(tempfolderName);
			if(!tempfolder.exists())
			{
				if(tempfolder.mkdir())
				{
					logger.info(tempfolderName+" folder created");
				}
				
			}
			File folder = new File(config.getProperty(Config.DEFAULT_TEMP_FOLDER)+"/inprocess");
			if(!folder.exists())
			{
				folder.mkdir();
			}
	
		    try
		    {
		      //  ProcessFile(value,config,i);
		    	ImportManager im=new ImportManager(config);
		    	im.ThreadsManger();
		    	
		    }
		  
		    catch(Exception e)
		    {
		    	logger.info(e.getMessage().toString());
		    }
			
			   
			
	    	if (Constants.DEFAULT_INTERIM_FILE_CLEANUP_FLAG.equalsIgnoreCase(config.getProperty(Config.DEFAULT_INTERIM_FILE_CLEANUP)))
			{		
					String folderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER);
					String inprocessfolderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER)+"/inprocess";
			}

			
			
		} catch (IOException e) {
			logger.info("Failed loading config file. System exit!");
			logger.error("Failed loading config file " + e.getMessage());
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			exception = true;
		} catch (AppException e) {
			logger.info("Failed loading config file. System exit!");
			logger.error("Failed loading config file " + e.getMessage());
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			exception = true;
		} catch (Exception e) {
			logger.info("Failed loading config file. System exit!");
			logger.error("Failed loading config file " + e.getMessage());
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			exception = true;
		}

		if (exception) {
			String logFileName = config.getProperty(Config.DEFAULT_LOG_FILENAME);
			String errorLogEmailIds = config.getProperty(Config.ERROR_LOG_EMAIL_TO);
			String errorLogEmailSubject = config.getProperty(Config.ERROR_LOG_EMAIL_SUBJECT);
			String errorLogEmailMsg = config.getProperty(Config.ERROR_LOG_EMAIL_MESSAGE);

			File logFile = new File(logFileName);
			MailSender mailSender = new MailSender(config);
			mailSender.sendEmail(errorLogEmailIds, errorLogEmailSubject, errorLogEmailMsg, logFile.getPath());
		}
			
		
			
	}
	
	
	
}
