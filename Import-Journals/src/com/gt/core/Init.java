/**
 * Copyright (c) 2014-15 GT. All rights reserved.
 */
package com.gt.core;

import static com.gt.Constants.ZIP_EXT;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gt.Config;
import com.gt.Constants;
import com.gt.service.FusionServiceStub;
import com.gt.util.ConstructZipFile;
import com.gt.util.MailSender;

/**
 * Class to initiate the import Journals
 * 
 * @author Krishna Mohan
 */
public class Init {

	private static Logger logger = Logger.getLogger(Init.class);

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

		
			/** constructing zip file from csv file */
			File zipFile=new ConstructZipFile(config).ConstructZip();
		
			/** Instantiate fusion services stub to process the currency file */			
		/*	FusionServiceStub serviceStub = new FusionServiceStub(config);
			boolean status = serviceStub.process(zipFile);
			*/	
			/** Move files to archives **/
			
			
		
			
			if (Constants.DEFAULT_GEN_FILE_CLEANUP_FLAG.equalsIgnoreCase(config.getProperty(Config.DEFAULT_GEN_FILE_CLEANUP)))
			{
				//delete file on JVM exit: Clean up created zip file
				zipFile.deleteOnExit();
			}
			else
			{
				//getting archives folder name-->default archives folder name:archives
				String archivesfolderName = config.getProperty(Config.DEFAULT_ARCHIVES_FOLDER);
				
				//getting pattern for suffix of file name-->default pattern YYMMdd
				String pattern="";
				if (!"".equalsIgnoreCase(config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)))
				{
					pattern=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)).format(new Date());
				}
				
				//generating new zip file name-->default:CurrentJournalsYYMMdd.zip
				String newzipfileName = config.getProperty(Config.DEFAULT_ZIP_FILE_NAME) +pattern+ ZIP_EXT;
				File archivesfolder=new File(archivesfolderName);
				
				//If archives folder not exists creating archives folder
				if(!archivesfolder.exists())
				{ 
					if(archivesfolder.mkdir())
					{
						logger.info(archivesfolderName+" folder created");
					}

				}
				
				//moving new zipfile to archives folder
				if(zipFile.renameTo(new File(archivesfolderName+newzipfileName)))
				{
					logger.info("File moved to archives successfully");

				}
				else
				{
					if(new File(archivesfolderName+newzipfileName).exists())
					{
						logger.info("File already exists");
					}

					logger.info("File failed to move");
				}



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
