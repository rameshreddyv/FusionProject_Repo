package com.gt.util;

import static com.gt.Constants.ZIP_EXT;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.gt.Config;
import com.gt.Constants;
import com.gt.service.FusionServiceStub;

public class WorkerThread implements Runnable {

	private static  Logger logger = Logger.getLogger(WorkerThread.class);
	String filename;
	List<String> files_list;
	Config config;
	int suffix=0;
	static int check=0;

	public WorkerThread(String name,Config config,int counter)
	{
		this.filename=name;		
		this.config=config;
		this.suffix=counter;
	}


	@Override
	public void run() 
	{

		processmessage();
		File f=new File(filename);	 
		//File zip=  new ConstructZipFile(config).ConstructZip(f.toString(),suffix);
		try 
		{
			ProcessFile(filename,config,suffix);

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}






	}

	private void processmessage()
	{

		try 
		{
			Thread.sleep(Integer.parseInt(config.getProperty(Config.SLEEP_TIME)));
		} 
		catch (InterruptedException e) 
		{


		}

	}
	public static void ProcessFile(String filename,Config config,int i) throws Exception
	{

		File oldfile=new File(filename);
		File newfile=new File(config.getProperty(Config.DEFAULT_TEMP_FOLDER)+File.separator+"inprocess"+File.separator+oldfile.getName());
		if(oldfile.renameTo(newfile))
		{

			File zipFile=new ConstructZipFile(config).ConstructZip(newfile.toString(),i);

			/** constructing zip file from csv file */


			/** Instantiate fusion services stub to process the currency file */	

			File proceesingCSV=newfile;
			FusionServiceStub serviceStub = new FusionServiceStub(config);	
			boolean status = serviceStub.process(zipFile,proceesingCSV);


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

					//pattern=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)).format(new Date());
					pattern=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)+"hhmmssS"+i).format(new Date());
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
				if(zipFile.renameTo(new File(archivesfolderName+newzipfileName)))//new File(archivesfolderName+newzipfileName)
				{
					logger.info("File moved to archives successfully"+archivesfolderName+newzipfileName);

					logger.info("zip file"+zipFile);
					zipFile.delete();

				}
				else
				{
					if(new File(archivesfolderName+newzipfileName).exists())
					{
						logger.info("File already exists"+archivesfolderName+newzipfileName);

					}

					logger.info("File failed to move");

				}

			}


		}
		if (Constants.DEFAULT_INTERIM_FILE_CLEANUP_FLAG.equalsIgnoreCase(config.getProperty(Config.DEFAULT_INTERIM_FILE_CLEANUP)))
		{

	
			try
			{
				if(newfile.exists() && newfile.delete())
				{
					logger.info("Inprocess cleaned" );
				}
				else
				{
					logger.info("Inprocess not cleaned" +newfile );
				}

			} catch (SecurityException x)
			{
				logger.info("sec exception" +x.getMessage() );
			
			}




		}


	}



}
