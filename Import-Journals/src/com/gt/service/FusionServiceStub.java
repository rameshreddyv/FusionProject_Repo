/**
 * Copyright (c) 2014-15 GT. All rights reserved.
 */
package com.gt.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import com.gt.Config;
import com.gt.Constants;
import com.gt.core.AppException;
import com.gt.util.DownloadMainLogFile;
import com.gt.util.MailSender;
import com.gt.util.ReadCSV;
import com.gt.util.UtilEncodeBase;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.DocumentDetails;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.FinancialUtilService;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.FinancialUtilService_Service;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.ObjectFactory;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.ServiceException;

/**
 * Service client class for Oracle Fusion web services to upload currency zip file and check the job status
 * 
 * @author muraliK
 * 
 */
public class FusionServiceStub {

	private static Logger logger = Logger.getLogger(FusionServiceStub.class);

	private static int EMAIL_FOR_JOURNAL_LAUNCHER_JOB=1;
	private static int EMAIL_FOR_INTERFACE_LOADER_JOB=2;
	

	private Config config;
	private FinancialUtilService financialUtilService;

	public FusionServiceStub(Config config) {
		this.config = config;
		setFinancialUtilService();
	}

	/**
	 * Method to initiate the currency zip file upload and get the job details
	 * 
	 * @param file zip file
	 * @param cur_csvfile current csv file
	 * @return boolean
	 */
	
	
	public boolean process(File file,File cur_csvfile) throws Exception {

		/** Upload the currency zip file and initiate job */
		String uploadResponse = invokeUcmUpload(file);

		/** Submit job request to process InterfaceLoader */
		String interfaceLoadStatus = invokeEssJobForInterfaceLoader(uploadResponse);
		
		/** Submit job request to JournalImports only if the interface loader process was successfully completed. */
		if ( config.getProperty(Config.ESS_JOB_STATUS_SUCCEEDED).trim().equalsIgnoreCase(interfaceLoadStatus.trim()))
			
			processJournalImports(cur_csvfile);
		

		return true;
	}

	/**
	 * @param financialUtilService
	 * @param fileName
	 * @param docTitle
	 * @param contentType
	 * @param account
	 * @param securityGroup
	 * @param fileAsBytes
	 * @return String
	 */
	private String invokeUcmUpload(File file) {

		logger.info("in invokeUcmUpload() ");
		String fileName = config.getProperty(Config.UPLOAD_FILE_NAME);
		String docTitle = config.getProperty(Config.UPLOAD_FILE_DOC_TITLE);
		String contentType = config.getProperty(Config.UPLOAD_FILE_TYPE);
		String account = config.getProperty(Config.UPLOAD_FILE_ACCOUNT);
		String securityGroup = config.getProperty(Config.UPLOAD_FILE_SEC_GROUP); 
		String docAuthor = config.getProperty(Config.UPLOAD_FILE_DOC_AUTHOR); 

		byte[] fileAsBytes = UtilEncodeBase.getByteArray(file.getAbsolutePath());

		DocumentDetails document = new DocumentDetails();
		ObjectFactory factory = new ObjectFactory();

		document.setFileName(factory.createDocumentDetailsFileName(fileName + "." + contentType));
		document.setDocumentTitle(factory.createDocumentDetailsDocumentTitle(docTitle));
		document.setContent(factory.createDocumentDetailsContent(fileAsBytes));
		document.setContentType(factory.createDocumentDetailsContentType(contentType));
		document.setDocumentAccount(factory.createDocumentDetailsDocumentAccount(account));
		document.setDocumentSecurityGroup(factory.createDocumentDetailsDocumentSecurityGroup(securityGroup));
		document.setDocumentAuthor(factory.createDocumentDetailsDocumentAuthor(docAuthor));

		String response = "";
		try {
			logger.info("call to financialUtilService.uploadFileToUcm(document) ");
			response = financialUtilService.uploadFileToUcm(document);
		} catch (ServiceException e) {
			logger.info("Failed uploading file " + e.getMessage());
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			throw new AppException("Failed uploading file to fusion server: " + e.getMessage(), e);

		}
		return response;
	}

	private String invokeEssJobForInterfaceLoader(String fileSent) {

		logger.info("Invoking EssJobForInterfaceLoader : Webservice call");
		
		// JobPackageName must be"/oracle/apps/ess/financials/commonModules/shared/common/interfaceLoader"
		String jobPackageName = config.getProperty(Config.ESS_JOB_PACKAGENAME);
		
		// JobDefinitionName Must be "InterfaceLoaderController"
		String jobDefinitionName = config.getProperty(Config.ESS_JOB_DEFINITIONNAME);
		List<String> paramList = new ArrayList<String>();
		paramList.add(config.getProperty(Config.ESS_JOB_PARAM1));
		paramList.add(fileSent);
		paramList.add(config.getProperty(Config.ESS_JOB_PARAM3));
		paramList.add(config.getProperty(Config.ESS_JOB_PARAM4));

		Long ESSLoad = Long.getLong("0");
		try {
			ESSLoad = financialUtilService.submitESSJobRequest(jobPackageName, jobDefinitionName, paramList);

		} catch (ServiceException e) {
			logger.info("Failed submitting EssJobForInterfaceLoader service: " + e.getMessage());

			throw new AppException("Failed submitting job: ", e);
		}

		logger.info(" EssJobForInterfaceLoader job submitted >> ESS Job Code: " + ESSLoad);

		/** check for job status repeatedly */
		String essStatusReturn = "";
		boolean active = true;
		logger.info("Checking for Status of ESS Job Code: " + ESSLoad);

		do {
			try {
				essStatusReturn = financialUtilService.getESSJobStatus(ESSLoad);
				logger.info("Received  ESS Job Status: " + essStatusReturn);
			} catch (ServiceException e) {

				throw new AppException("Failed getting job status " + e.getMessage());
			}
			
			if ( config.getProperty(Config.ESS_JOB_STATUS_SUCCEEDED).trim().equalsIgnoreCase(essStatusReturn.trim())
					    ||config.getProperty(Config.ESS_JOB_STATUS_ERROR).trim().equalsIgnoreCase(essStatusReturn.trim())
					       ||config.getProperty(Config.ESS_JOB_STATUS_WARNING).trim().equalsIgnoreCase(essStatusReturn.trim()) ){
					
					logger.info(" Checking ESSJobStatus is any of Succeeded or Error or Warning : Returned status = " + essStatusReturn);
					active=false;
					break;
			} // If the job is not running or wait 

			try {
				long waittime = 300000;
				waittime = Long.parseLong(config.getProperty(Config.ESS_JOB_STATUS_CHECK_TIMER));
				logger.info("Waiting before next check of ESS Job Status >> Job Code: " + ESSLoad + " Wait time = " + waittime);
				Thread.sleep(waittime);
			} catch (InterruptedException ie) {
				throw new AppException("Failed at waiting for job status " + ie.getMessage(), ie);
			}
			
		} while (active == true);

		/** Send job status details to team via email */
		invokeGetJobExecutionDetails(ESSLoad,essStatusReturn,EMAIL_FOR_INTERFACE_LOADER_JOB);
		return essStatusReturn;
	}

	
	private void processJournalImports(File cur_csvfile ) {
		invokeEssJobForImportJournals(cur_csvfile);
	}


	
	private void invokeEssJobForImportJournals(File cur_csvfile) {

		logger.info("Invoking invokeEssJobForImportJournals : Webservice call");
		
		// JobPackageName must be"/oracle/apps/ess/financials/generalLedger/programs/common"
		String jobPackageName = config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PACKAGENAME);
		
		// JobDefinitionName Must be "JournalImportLauncher"
		String jobDefinitionName = config.getProperty(Config.JOURNAL_IMP_ESS_JOB_DEFINITIONNAME);
		//String jobDefinitionName = "JournalImportLauncher";
		
		List<String> paramList = new ArrayList<String>();
		// Data access set identifier : This is tobedrivenfrom config
		paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM1));
		
		
		//Source 
		paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM2));
		
		//Ledger identifier
		//paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM3));
		// ReadCSV.getLedgerID();
		
		ReadCSV rcv=new ReadCSV(config,cur_csvfile);
		
		if(config.getProperty(config.JOURNAL_IMP_ESS_JOB_PARAM3_CONFIG).equals("N"))
		{
			paramList.add(rcv.getLedgerId());
		}
		else
		{
			paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM3));		
		}
		
		//Group identifier
		//paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM4));
		
		if(config.getProperty(config.JOURNAL_IMP_ESS_JOB_PARAM4_CONFIG).equals("N"))
		{
			paramList.add(rcv.getGroupId());
		}
		else
		{
			paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM4));		
		}
		
		
		
		//Post account errors to suspense account
		paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM5));
		
		//Create summary journals
		paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM6));
		
		//Import descriptive flexfields		
		paramList.add(config.getProperty(Config.JOURNAL_IMP_ESS_JOB_PARAM7));
		
		Long ESSLoad = Long.getLong("0");
		try {
			ESSLoad = financialUtilService.submitESSJobRequest(jobPackageName, jobDefinitionName, paramList);

		} catch (ServiceException e) {
			logger.info("Failed submitting invokeEssJobForImportJournals service: " + e.getMessage());

			throw new AppException("Failed submitting job: ", e);
		}

		logger.info(" invokeEssJobForImportJournals job submitted >> ESS Job Code: " + ESSLoad);

		/** check for job status repeatedly */
		String essStatusReturn = "";
		boolean active = true;
		logger.info("Checking for Status of ESS Job Code: " + ESSLoad);

		do {
			try {
				essStatusReturn = financialUtilService.getESSJobStatus(ESSLoad);
				logger.info("Received  ESS Job Status: " + essStatusReturn);
			} catch (ServiceException e) {

				throw new AppException("Failed getting job status " + e.getMessage());
			}
			
			if ( config.getProperty(Config.ESS_JOB_STATUS_SUCCEEDED).trim().equalsIgnoreCase(essStatusReturn.trim())
					    ||config.getProperty(Config.ESS_JOB_STATUS_ERROR).trim().equalsIgnoreCase(essStatusReturn.trim())
					       ||config.getProperty(Config.ESS_JOB_STATUS_WARNING).trim().equalsIgnoreCase(essStatusReturn.trim()) ){
					
					logger.info(" Checking ESSJobStatus is any of Succeeded or Error or Warning : Returned status = " + essStatusReturn);
					active=false;
					break;
			} // If the job is not running or wait 

			try {
				long waittime = 300000;
				waittime = Long.parseLong(config.getProperty(Config.ESS_JOB_STATUS_CHECK_TIMER));
				logger.info("Waiting before next check of ESS Job Status >> Job Code: " + ESSLoad + " Wait time = " + waittime);
				Thread.sleep(waittime);
			} catch (InterruptedException ie) {
				throw new AppException("Failed at waiting for job status " + ie.getMessage(), ie);
			}
			
		} while (active == true);

		/** Send job status details to team via email */
	
		invokeGetJobExecutionDetails(ESSLoad,essStatusReturn,EMAIL_FOR_JOURNAL_LAUNCHER_JOB);
	}
	
	
	/**
	 * Method to get the Job execution details and download the zip file and send the log fils by email
	 * 
	 * @param ESSLoad
	 */
	

	File currentfile=null;//file that is at run
	File mynewfile=null;	//This is child process log file (eg:40142.zip)
	String childProcessId;	//This is child process id
	String childProcessStatus;//This is child process status
	ZipFile zipfile;
	InputStream theFile;
	ZipInputStream stream;
	InputStream inputstream;
	public File invokeGetJobExecutionDetails(Long ESSLoad,String essStatus,int jobType) {
		
		try {
			
			logger.info("Downloading JobExecutionDetails : WebService call ");
			List<DocumentDetails> list = financialUtilService.downloadESSJobExecutionDetails(String.valueOf(ESSLoad.longValue()),"");
			logger.info("Job code : " + String.valueOf(ESSLoad.longValue()) + " List = " + list);	
			String filename=null;		
			Iterator<DocumentDetails> iterator = null;
			
			
			if (null != list && !list.isEmpty())
				iterator = list.iterator();
			for (; null != iterator && iterator.hasNext();) 
			{
				DocumentDetails document = iterator.next();
				filename=document.getDocumentName().getValue();
				logger.info("\nfile name:"+filename+"\ncontent type"+document.getContentType().getValue());
				
				/** if jobtype is 2 i.e (EMAIL_FOR_INTERFACE_LOADER_JOB) email will be sent without further processing of zip
				 * 
				 * This sends import journals part-1 email.
				 * 
				 * */
				if(jobType==2)
				{
					attachToEmail(document,String.valueOf(ESSLoad.longValue()),essStatus,jobType);
				}
				/** if jobtype is 1 i.e EMAIL_FOR_JOURNAL_LAUNCHER_JOB email will be sent without further processing of zip
				 * 
				 * This sends import journals part-2 email.
				 * 
				 * */
				else if (jobType==1)
				{
					
					new UtilEncodeBase();
					currentfile=UtilEncodeBase.reConstructDownloadFile(filename, config, document.getContent().getValue());
					String parentProcessId,parentProcessStatus;		
					Long essload;
					ZipEntry entry;
					final File oldfile=currentfile;//Assigning primary job zip file to old file for persistence 
					parentProcessId=String.valueOf(ESSLoad);//Assigning primary job process id 
					parentProcessStatus=String.valueOf(essStatus);//Assigning primary job status
					
					oldfile.deleteOnExit();
					currentfile.deleteOnExit();
				try 
				{
					theFile = new FileInputStream(currentfile);
					stream = new ZipInputStream(theFile);
					zipfile = new ZipFile(currentfile);	
					
					while((entry = stream.getNextEntry())!=null )
					{
						inputstream = zipfile.getInputStream(entry);	
						Scanner sc=new Scanner(inputstream);
						
						//checking if zip file contains log file
						//This is log file of primary job
						if(entry.getName().trim().equals(ESSLoad+".log"))
						{						
							do
							{
								String line=sc.nextLine();
								
								/**
								 * Getting "child process id" only when primary job succeeded
								 * 
								 * if primary job is not succeeded then index will be <0
								 * */
								if(line.indexOf("Journal Import concurrent request")==0)
								{
									String es=line.substring(34, 39);
									essload=Long.parseLong(es);		
									
									/***closing all streams if not file wont delete on exit**/
									
									stream.close();
									theFile.close();
									inputstream.close();
									stream.close();
									zipfile.close();	
									
									/** get child process log files based on  child process-id */
									mynewfile=invokeGetJobExecutionDetails(essload,essStatus,jobType);	
									//Assigning child process id
									childProcessId=String.valueOf(essload);
									//Assigning child process status
									childProcessStatus=essStatus;
									
									
								}	
							
						
							}while (sc.hasNextLine());				
							sc.close();
						
						}
						
					
						
					}
					
				
					
					
					/**
					 * if part 1 zip file exists(with child process id in log) and part 2 zip exists 
					 * then attaching them to email
					 * */
					if(oldfile!=null && mynewfile!=null)
					{	
						/***closing all streams if not file wont delete on exit**/
						
						theFile.close();
						inputstream.close();
						stream.close();
						zipfile.close();
						
						//sending both primary and child job logs to email with their id and status.						
						attachDocsToEmail(document,parentProcessId,childProcessId,String.valueOf(oldfile),String.valueOf(mynewfile));						
						
						//Deletes log files from "temp" folder when JVM exits 
						oldfile.deleteOnExit();
						mynewfile.deleteOnExit();
					
					}
					else
						
						/**
						 * if part 1 zip file exists and there is no child process id in its (log file),
						 * then deleting file on JVM exit
						 * */
						if(mynewfile==null && oldfile!=null)
						{
							theFile.close();
							inputstream.close();
							stream.close();
							zipfile.close();

								oldfile.deleteOnExit();
							
						}
					

				}
				catch(FileNotFoundException fe)
				{
					logger.info("Log files Not Found:"+fe.getMessage());
				}
				catch (IOException e) {
					
					logger.info("IO Exception while reading JI log files:"+e.getMessage());
				}
		

				
				}
			
				
			}

			
		} catch (ServiceException e) {
			throw new AppException("Failed in getting document details " + e.getMessage());
		}
		return currentfile;
		
	}
	
	/************************************************************************/
	/**
	 * Send email with log file attachment for part 2 of import journals 
	 * 
	 * @param document
	 */
	/************************************************************************/
	private void attachDocsToEmail(DocumentDetails document,String ParentProcessId,String ChildProcessId,String OldFileName,String NewFileName)
	{
		logger.info("Attaching To Email >> Downloaded Document details");
		

		String contentType = document.getContentType().getValue();
		String documentName = document.getDocumentName().getValue();
		String fileName = document.getFileName().getValue();
		String title = document.getDocumentTitle().getValue();
		
		String emailIds = config.getProperty(Config.JOB_STATUS_EMAIL_TO);
		MailSender mailSender = new MailSender(config);
		String[] emailReceipents = emailIds.trim().split(",");
		

		
			String subject = config.getProperty(Config.JI_JOB_STATUS_EMAIL_SUBJECT);
			logger.debug("Replaced Subject = " + subject);
			try {
			//Replace $requestId in the subject  
			subject=subject.replaceAll("\\$parentId", ParentProcessId);
		
				subject=subject.replaceAll("\\$parentStatus", financialUtilService.getESSJobStatus(Long.parseLong(ParentProcessId)));
			
			subject=subject.replaceAll("\\$childId", ChildProcessId);
			subject=subject.replaceAll("\\$childStatus", financialUtilService.getESSJobStatus(Long.parseLong(ChildProcessId)));
			logger.debug("Replaced Subject = " + subject);
			
			String message = config.getProperty(Config.JI_JOB_STATUS_EMAIL_MESSAGE);
			logger.debug("Raw Message = " + message);
			
			//Replace variables in the content  
			//$requestId,$contentType,$documentName,$title
			Map<String, String> values = new HashMap<String, String>();
			values.put("\\$parentId", ParentProcessId);
			values.put("\\$parentStatus", financialUtilService.getESSJobStatus(Long.parseLong(ParentProcessId)));
			values.put("\\$childId", ChildProcessId);
			values.put("\\$childStatus", financialUtilService.getESSJobStatus(Long.parseLong(ChildProcessId)));
			values.put("\\$contentType", contentType);
			values.put("\\$documentName", documentName);
			values.put("\\$title", title);
	
			for (Map.Entry<String, String> e : values.entrySet()) {
				message = message.replaceAll(  e.getKey() , e.getValue());
			}
			
			logger.info("Replaced Message = " + message);
			for (String emailReceipent : emailReceipents) 
			{
				mailSender.sendEmail(emailReceipent, subject, message, OldFileName,NewFileName);
				
				
			}
			
			
		
			} catch (NumberFormatException e1) {
				
				logger.info("NumberFormatException for process id"+e1.getMessage());
			} catch (ServiceException e1) {
				
				logger.info("ServiceException"+e1.getMessage());
			}

		
			logger.info("old file name"+OldFileName+"\n"+"new file name\n"+NewFileName);
			try {
				
				theFile.close();
				inputstream.close();
				stream.close();
				zipfile.close();
				new File(OldFileName).delete();
				new File(NewFileName).delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	


	
	/************************************************************************/
	/**
	 * Send email with log file attachment for part 1 of import journals 
	 * 
	 * @param document
	 */
	/************************************************************************/
	private void attachToEmail(DocumentDetails document,String requestId,String essStatus,int jobType) {
		logger.info("Attaching To Email >> Downloaded Document details");
		String contentType = document.getContentType().getValue();
		String documentName = document.getDocumentName().getValue();
		String fileName = document.getFileName().getValue();
		String title = document.getDocumentTitle().getValue();

		logger.debug("\n" + "contentType = " + contentType + "\n" + "documentName =" + documentName + "\n"
				+ "fileName =" + fileName + "\n" + "title =" + title + "\n");

		// code for downloading the zip file and
		File downloadedFile = UtilEncodeBase.reConstructDownloadFile(documentName, config, document
																								   .getContent()
																								   .getValue());
		
		//delete file on JVM exit: Clean up downloaded file
		if (Constants.DEFAULT_GEN_FILE_CLEANUP_FLAG.equalsIgnoreCase(config.getProperty(Config.DEFAULT_GEN_FILE_CLEANUP)))
		    downloadedFile.deleteOnExit();

		// attach it mail. There will be multiple documents we have to attach all of this and send them.
		// get log and attach to the mail
		
		
		String emailIds = config.getProperty(Config.JOB_STATUS_EMAIL_TO);
		MailSender mailSender = new MailSender(config);
		String[] emailReceipents = emailIds.trim().split(",");
		if(jobType==2)
		{
			
			String subject = config.getProperty(Config.JOB_STATUS_EMAIL_SUBJECT);
			logger.debug("Replaced Subject = " + subject);
			
			//Replace $requestId in the subject  
			subject=subject.replaceAll("\\$requestId", requestId);
			subject=subject.replaceAll("\\$essStatus", essStatus);
			logger.debug("Replaced Subject = " + subject);
			
			String message = config.getProperty(Config.JOB_STATUS_EMAIL_MESSAGE);
			logger.debug("Raw Message = " + message);
			
			//Replace variables in the content  
			//$requestId,$contentType,$documentName,$title
			Map<String, String> values = new HashMap<String, String>();
			values.put("\\$requestId", requestId);
			values.put("\\$essStatus", essStatus);
			values.put("\\$contentType", contentType);
			values.put("\\$documentName", documentName);
			values.put("\\$title", title);
		
			
			for (Map.Entry<String, String> e : values.entrySet()) {
				message = message.replaceAll(  e.getKey() , e.getValue());
			}
			
			logger.info("Replaced Message = " + message);
			for (String emailReceipent : emailReceipents) {
				mailSender.sendEmail(emailReceipent, subject, message, downloadedFile.getAbsolutePath());
			}
			downloadedFile.delete();
		}
		
	
	
	
	}
	


	/**
	 * instantiate fusion service stub instance
	 */
	private void setFinancialUtilService() {
		logger.info("in setFinancialUtilService()");
		QName qname = new QName("http://xmlns.oracle.com/apps/financials/commonModules/shared/financialUtilService/",
			"FinancialUtilService");

		URL wsdlDoc = null;
		try {
			wsdlDoc = new URL(config.getProperty(Config.WS_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		this.financialUtilService = new FinancialUtilService_Service(wsdlDoc, qname).getFinancialUtilServiceSoapHttpPort();
	

		Map<String, Object> requestContext = ((BindingProvider) financialUtilService).getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getProperty(Config.WS_URL));
		requestContext.put(BindingProvider.USERNAME_PROPERTY, config.getProperty(Config.WS_URL_USERNAME));
		requestContext.put(BindingProvider.PASSWORD_PROPERTY, config.getProperty(Config.WS_URL_PASSWORD));
	}
}
