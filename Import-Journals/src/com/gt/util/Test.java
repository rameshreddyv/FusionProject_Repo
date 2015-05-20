package com.gt.util;

import static com.gt.Constants.ZIP_EXT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gt.Config;
import com.gt.Constants;
import com.gt.service.FusionServiceStub;


public class Test {
	
	private static Logger logger = Logger.getLogger(Test.class);

	public static void main(String ar[])
	{
		
		PropertyConfigurator.configure("log4j.properties");
		
	//	String find="Waiting on Child Process with request ID:";
		//String filename="C:/Users/gt-dell-03/Desktop/test_logs.txt";
		try {
			
			Config 	config = new Config("importjournals.config");
			/*	ImportManager im=new ImportManager(config);
			im.ThreadsManger();
			*/
		/*	String folderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER);
			String[]entries = new File(folderName).list();
			for(String s: entries)
			{	
				File currentFile = new File(new File(folderName).getPath(),s);
				currentFile.delete();
			}
			*/
/*
			String foldername=(config.getProperty(Config.DEFAULT_CSV_FILE_PATH));
			List<String> files_list=new GetFilesList(config).GetFileNames(foldername);
			
			for (int i = 0; i < files_list.size(); i++) 
			{
			    String value = (String) files_list.get(i);
			    System.out.println("Element: " + value);
			    
			    File oldfile=new File(value);
			    File newfile=new File(config.getProperty(Config.DEFAULT_TEMP_FOLDER)+"/inprocess/"+oldfile.getName());
			    if(oldfile.renameTo(newfile))
			    {
			    	System.out.println("file moved--"+newfile);
			    }
			    
			}
			
			File folder = new File(config.getProperty(Config.DEFAULT_TEMP_FOLDER)+"/inprocess");
			if(!folder.exists())
			{
				folder.mkdir();
			}
			else
			{/*
				List<String> files_names=new GetFilesList(config).GetFileNames(folder.toString());
				for (int i = 0; i < files_names.size(); i++) 
				{
				    String value = (String) files_names.get(i);
				    System.out.println("Element: " + value);
				    
				}
				*/
			//}
			
		/*	
			File f=new ConstructZipFile(config).ConstructZip();
			String oldfolderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER);
			String archivesfolderName = config.getProperty(Config.DEFAULT_ARCHIVES_FOLDER);
			
			//Default filename CurrentJournals.zip
			
			String pattern="";
			if (!"".equalsIgnoreCase(config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN))){
				pattern=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)).format(new Date());
			}
			String oldzipfileName = config.getProperty(Config.DEFAULT_ZIP_FILE_NAME) + ZIP_EXT;
			String newzipfileName = config.getProperty(Config.DEFAULT_ZIP_FILE_NAME) +pattern+ ZIP_EXT;
			
			   File oldfile =new File(oldfolderName+oldzipfileName);
			   File archivesfolder=new File(archivesfolderName);
			   
			   
			   
			   if(config.getProperty(Config.DEFAULT_GEN_FILE_CLEANUP).equals("true"))
			   {
				   
			   
			   if(!archivesfolder.exists())
			   { 
				   if(archivesfolder.mkdir())
				   {
					   logger.info(archivesfolderName+" folder created");
				   }
		    	   
			   }
			  
			   	
				   if(oldfile.renameTo(new File(archivesfolderName+newzipfileName))){
			    		System.out.println("File moved successfully!");
			    	   }else{
			    		   if(new File(archivesfolderName+newzipfileName).exists())
			    		   {
			    				System.out.println("File already exists");
			    		   }
			    		System.out.println("File is failed to move!");
			    	   }
			   }
			   else
			   {
				   logger.info(archivesfolderName+" folder created");
			   }
			*/
		//	new DownloadMainLogFile(config);
			
			
			//new FusionServiceStub(config).invokeGetJobExecutionDetails((long) 42574,"SUCCEEDED",1);//41599
			
		
			
			try {
				new WorkerThread("COMTH_RV150429.csv",config,1).ProcessFile("COMTH_RV150429.csv",config,1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*String process[]=new ReadTextFile(filename).getProcessIDArray(find);
			for(int i=0;i<process.length;i++)
			{
				System.out.println(process[i]);
			}*/
			
			//Date date = new Date();
		 /*   SimpleDateFormat dateformat =new SimpleDateFormat ("yyyyMMdd");
		    System.out.println("\nCurrent Date: " + dateformat.format(date));
			ReadCSV jc=new ReadCSV(config);
			
			String pattern="";
			if (!"".equalsIgnoreCase(config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN))){
				pattern=new SimpleDateFormat (config.getProperty(Config.DEFAULT_CSV_FILE_NAME_PATTERN)).format(new Date());
			}
			
			String csvfileName=config.getProperty(Config.DEFAULT_CSV_FILE_NAME)+pattern+Constants.CSV_EXT;
			
			
			System.out.println(csvfileName);
			String zipfileName=config.getProperty(config.DEFAULT_CSV_FILE_NAME)+Constants.ZIP_EXT;
			System.out.println(zipfileName);
			String filePath=config.getProperty(config.DEFAULT_CSV_FILE_PATH);
			System.out.println(filePath+zipfileName);

			String fileName=config.getProperty(config.DEFAULT_CSV_FILE_NAME)+Constants.ZIP_EXT;
			System.out.print(filePath+fileName);
			
					if(config.getProperty(config.JOURNAL_IMP_ESS_JOB_PARAM4_CONFIG).equals("N"))
					{
						System.out.print("\nledger id "+new ReadCSV(config).getGroupId());
						//System.out.print("\nledger id "+ReadCSV.getLedgerId());
					}
					else
					{
						System.out.print(config.getProperty(config.JOURNAL_IMP_ESS_JOB_PARAM4));
					}
			*/		
					/*RatesHandler rn=new RatesHandler(config);
					File csv=new File(filePath+csvfileName);
					File zp=rn.constructZip(csv);
					*/	
				/*	String logFileName = config.getProperty(Config.DEFAULT_LOG_FILENAME);
					String errorLogEmailIds = config.getProperty(Config.ERROR_LOG_EMAIL_TO);
					String errorLogEmailSubject = config.getProperty(Config.ERROR_LOG_EMAIL_SUBJECT);
					String errorLogEmailMsg = config.getProperty(Config.ERROR_LOG_EMAIL_MESSAGE);
*/
					//File logFile = new File(logFileName);
	
		
			/*
			MailSender mailSender = new MailSender(config);
					//mailSender.sendEmail(errorLogEmailIds, errorLogEmailSubject, errorLogEmailMsg, logFile.getPath());
			String krishna_file="file:///C:/Users/gt-dell-03/Desktop/Manage%20Currency%20Rates%20%20%20Period%20Close%20%20%20Oracle%20Applications.png";
					mailSender.sendEmail("adinaryana.gondi@gigatude.com", "TEST This Data","Testing");
		
				*/	
					
					
				//	if (Constants.DEFAULT_GEN_FILE_CLEANUP_FLAG.equalsIgnoreCase(config.getProperty(Config.DEFAULT_GEN_FILE_CLEANUP)))
					//f.deleteOnExit();
					
		}
		catch(FileNotFoundException fe)
		{
			logger.info("File not found found "+fe.getMessage());
			if (logger.isDebugEnabled()) {
				fe.printStackTrace();
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
		
		
		
	}
/*	public void test(Config config,File cur_csvfile)
	{
		
		System.out.print("t");
	new ReadCSV(config,cur_csvfile);		
	System.out.print("groupid "+ReadCSV.getGroupId());			
	System.out.print("\nledger id "+ReadCSV.getLedgerId());
	}*/
}
