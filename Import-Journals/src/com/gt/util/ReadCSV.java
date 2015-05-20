/**
 * File:ReadCSV.java
 * @author Krishna Mohan
 * Date:03 Apr 2015
 * Company:Gigatude
 * 
 * */
package com.gt.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.gt.Config;
import com.gt.Constants;
import com.gt.CSVHeader;

import au.com.bytecode.opencsv.CSVReader;


/**
 * This class will read the CSV file from default location
 * returns GROUP ID and LEDGER ID
 * 
 * */
public class ReadCSV
{

	private static Logger logger = Logger.getLogger(ReadCSV.class);
	private static String groupId;
	private static String ledgerId;
	CSVReader reader;
	public ReadCSV(Config config,File cur_csvfile)
	{
		try 
		{
			String[] row;

			//Get Default CSV FilePath
			String filepath=config.getProperty(Config.DEFAULT_CSV_FILE_PATH);			
			
			//Get Default CSV FileName-->CurrentJournalsYYYYMMDD,MMDDYYYY,DDMMYYYY
			String pattern="";
			if (!"".equalsIgnoreCase(config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN))){
				pattern=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)).format(new Date());
			}
			
			String csvfileName=config.getProperty(Config.DEFAULT_CSV_FILE_NAME)+pattern+Constants.CSV_EXT;
			
			//Generating file object
			File csvFile = cur_csvfile;//new File(filepath+csvfileName);
			
			
		
			//Checking whether CSV file exists in default location or not
			if(csvFile.exists() && !csvFile.isDirectory())
			{
				
				reader= new CSVReader(new FileReader(csvFile),',', '\"',2);
				while((row=reader.readNext())!=null)
				{			
					groupId=row[CSVHeader.getGroupId()];
					ledgerId=row[CSVHeader.getLedgerId()];
					break;

				}
			}
			else
			{
				logger.info("Failed to Read CSV File,CSV File not found ");
			}
		}
		catch(FileNotFoundException fe)
		{
			logger.info("File not found found "+fe.getMessage());
			if (logger.isDebugEnabled()) 
			{
				fe.printStackTrace();

			}
		}
		catch(Exception e)
		{
			logger.info("Failed loading CSV file. System exit!");
			logger.error("Failed loading CSV file " + e.getMessage());
			if (logger.isDebugEnabled()) 
			{
				logger.info(e.getMessage());
			}

		}
		finally
		{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	//Getters and Setters of GROUPID and LedgerID
	public static String getGroupId()
	{
		return groupId;
	}
	public static void setGroupId(String groupId)
	{
		ReadCSV.groupId = groupId;
	}
	public static String getLedgerId() 
	{
		return ledgerId;
	}
	public static void setLedgerId(String ledgerId) 
	{
		ReadCSV.ledgerId = ledgerId;
	}




}