/**
 * @Author:Krishna Mohan
 * File:ConstructZip.java
 * Date:03 Apr 2015
 * Company:Gigatude
 * */
package com.gt.util;

import static com.gt.Constants.ZIP_EXT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.gt.Config;
import com.gt.Constants;
import com.gt.core.AppException;


/** This class is for constructing zip file from default csv file */

public class ConstructZipFile {

	private Config config;
	File zipFile=null;
	private static Logger logger = Logger.getLogger(ConstructZipFile.class);
	
	
	public ConstructZipFile(Config config) 
	{
		this.config = config;
		
	}
	public File ConstructZip() 
	{
		try {
			logger.info("Start constructing zip file ");
			byte[] buffer = new byte[1024];
			// TODO: get file patterns from properties file
			
			//Default temp folder D:/importjournals/temp/
			String folderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER);
			File tempfolder=new File(folderName);
			if(!tempfolder.exists())
			{
				if(tempfolder.mkdir())
				{
					logger.info(folderName+" folder created");
				}
				
			}
			
			//Default filename CurrentJournals.zip
			String fileName = config.getProperty(Config.DEFAULT_ZIP_FILE_NAME) + ZIP_EXT;
			
			//Reading Default csv filepath D:/importjournals/			
			String filepath=config.getProperty(Config.DEFAULT_CSV_FILE_PATH);
			
			//Readding Default csv fileName--CurrentJournalsYYYYMMDD 
			//String csvfileName=config.getProperty(Config.DEFAULT_CSV_FILE_NAME)+Config.CSV_PREFIX+Constants.CSV_EXT;

			String pattern="";
			if (!"".equalsIgnoreCase(config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN))){
				pattern=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)).format(new Date());
			}
			
			String csvfileName=config.getProperty(Config.DEFAULT_CSV_FILE_NAME)+pattern+Constants.CSV_EXT;
			logger.info("CSV FILE NAME: " + csvfileName);
			
			//Readding Default csv fileName--CurrentJournalsYYYYMMDD 
			File csvFile=new File(filepath+csvfileName);
			
			//checking if valid csv file exits in the importjournals folder			
			if(csvFile.exists() && !csvFile.isDirectory())
			{
				FileOutputStream fos = new FileOutputStream(folderName + fileName);
				ZipOutputStream zos = new ZipOutputStream(fos);
				ZipEntry ze = new ZipEntry(csvfileName);
				zos.putNextEntry(ze);		
				FileInputStream in = new FileInputStream(csvFile);

				int len;
				while ((len = in.read(buffer)) > 0)
				{
					zos.write(buffer, 0, len);
				}

				in.close();
				zos.closeEntry();
				zos.close();
				
				//checking created zip exists or not		
				zipFile= new File(folderName + fileName);
				if (!zipFile.exists()) 
				{
					throw new AppException("Failed constructing zip file");
				}
				
			
				logger.info("Completed constructing zip file ");
			}
			else
			{
				logger.info("Failed constructing zip file,CSV File not found ");			
				
			}
			
			
			
		}
		catch(FileNotFoundException fe)
		{
			logger.info("File not found found "+fe.getMessage());
			if (logger.isDebugEnabled()) {
				fe.printStackTrace();
			
			}
		}
		catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppException("Failed constructing zip file " + e.getMessage(), e);
		}
		return zipFile;
	}
	
	static int counter=0;
	public File ConstructZip(String filename,int i) 
	{
		
		
		
		try {
			logger.info("Start constructing zip file ");
			byte[] buffer = new byte[1024];
			// TODO: get file patterns from properties file
			
			//Default temp folder D:/importjournals/temp/
			String folderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER);
			File tempfolder=new File(folderName);
			if(!tempfolder.exists())
			{
				if(tempfolder.mkdir())
				{
					logger.info(folderName+" folder created");
				}
				
			}
			
			String patter=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)+"hhmmsS"+i).format(new Date());
			//Default filename CurrentJournals.zip
			//String fileName = config.getProperty(Config.DEFAULT_ZIP_FILE_NAME) + ZIP_EXT;
			String fileName = config.getProperty(Config.DEFAULT_ZIP_FILE_NAME) +patter+ ZIP_EXT;

			
			//	String csvfileName=config.getProperty(Config.DEFAULT_CSV_FILE_NAME)+pattern+Constants.CSV_EXT;
			logger.info("CSV FILE NAME: " + filename);
			
			//Readding Default csv fileName--CurrentJournalsYYYYMMDD 
			File csvFile=new File(filename);////new File(filepath+csvfileName);
			
			//checking if valid csv file exits in the importjournals folder			
			if(csvFile.exists() && !csvFile.isDirectory())
			{
				FileOutputStream fos = new FileOutputStream(folderName + fileName);
				ZipOutputStream zos = new ZipOutputStream(fos);
				ZipEntry ze = new ZipEntry(csvFile.getName());
			
				zos.putNextEntry(ze);		
				FileInputStream in = new FileInputStream(filename);//csvFile

				int len;
				while ((len = in.read(buffer)) > 0)
				{
					zos.write(buffer, 0, len);
				}

				in.close();
				zos.closeEntry();
				zos.close();
				
				
				
				//checking created zip exists or not		
				zipFile= new File(folderName + fileName);//
				if (!zipFile.exists()) 
				{
					throw new AppException("Failed constructing zip file");
				}
				
			
				logger.info("Completed constructing zip file ");
			}
			else
			{
				logger.info("Failed constructing zip file,CSV File not found ");			
				
			}
			
			
			
		}
		catch(FileNotFoundException fe)
		{
			logger.info("File not found found "+fe.getMessage());
			if (logger.isDebugEnabled()) {
				fe.printStackTrace();
			
			}
		}
		catch (AppException e) {
			throw e;
		} catch (Exception e) {
			throw new AppException("Failed constructing zip file " + e.getMessage(), e);
		}
		
		counter++;
		return zipFile;
		
		
	}
	
	

}
