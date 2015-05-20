package com.gt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import com.gt.Config;
import com.gt.core.AppException;
import com.gt.service.FusionServiceStub;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.DocumentDetails;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.FinancialUtilService;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.FinancialUtilService_Service;
import com.oracle.xmlns.apps.financials.commonmodules.shared.financialutilservice.ServiceException;

public class DownloadMainLogFile {


	private static Logger logger = Logger.getLogger(FusionServiceStub.class);

	Config config;
	private FinancialUtilService financialUtilService;
	Long ESSLoadv=Long.parseLong("40880");
	String status="SUCCEEDED";
	ZipInputStream stream;
	public DownloadMainLogFile(Config config) 
	{
		this.config=config;
		setFinancialUtilService();
		invokeGetJobExecutionDetails(ESSLoadv,status);
	}




	public void invokeGetJobExecutionDetails(Long ESSLoad,String essStatus) {
		try {
			logger.info("Downloading JobExecutionDetails : WebService call ");
			List<DocumentDetails> list = financialUtilService.downloadESSJobExecutionDetails(String.valueOf(ESSLoad.longValue()),"");			
			logger.info("Job code : " + String.valueOf(ESSLoad.longValue()) + " List = " + list);

			MailSender mailSender = new MailSender(config);
			String filename;

			Iterator<DocumentDetails> iterator = null;
			if (null != list && !list.isEmpty())
				iterator = list.iterator();

			for (; null != iterator && iterator.hasNext();) 
			{
				DocumentDetails document = iterator.next();
				filename=document.getDocumentName().getValue();
				logger.info("\nfile name:"+filename+"\ncontent type"+document.getContentType().getValue());

				try {
					File f=new UtilEncodeBase().reConstructDownloadFile(filename, config, document.getContent().getValue());
					InputStream theFile = new FileInputStream(f);
					stream = new ZipInputStream(theFile);

					ZipEntry entry;
					ZipFile zipfile = new ZipFile(f);//f
				
					
				System.out.println("my test case "+financialUtilService.getESSJobStatus(ESSLoad));
				
				
					Long essload;
					while((entry = stream.getNextEntry())!=null )
					{
						InputStream inputstream = zipfile.getInputStream(entry);	
						Scanner sc=new Scanner(inputstream);	
						if(entry.getName().trim().equals(ESSLoad+".log"))
						{
							
						
						String s = String.format("Entry: %s len %d added %TD",entry.getName(), entry.getSize(),new Date(entry.getTime()));
						do
						{

							String line=sc.nextLine();
							if(line.indexOf("Journal Import concurrent request")==0)
							{
								String es=line.substring(34, 39);
								essload=Long.parseLong(es);
								System.out.println(essload);
								invokeGetJobExecutionDetails(essload,status);
								
							}
							
						}while (sc.hasNextLine());
						sc.close();
						}
			


					}
					System.out.println(" path"+f.getAbsolutePath());
					
					mailSender.sendEmail("mymail0405@gmail.com", "TEST SUBJECT", "TEST EMAIL", f.getAbsolutePath());

				}
				catch(FileNotFoundException fe)
				{
					fe.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				




				//byte[] bytesarr=new UtilEncodeBase().getByteArray(filename);
				//File f=new UtilEncodeBase().reConstructDownloadFile(filename, config, document.getContent().getValue());

				//logger.info("file stored in:"+f);


				// TODO : attachments to mail.
				// There will be multiple documents we have to attach all of this and
				// send them.

				//attachToEmail(document,String.valueOf(ESSLoad.longValue()),essStatus);
				
				
				/*****try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*****/
			}
	

		}

		catch (ServiceException e) {
			throw new AppException("Failed in getting document details " + e.getMessage());
		}
		
		
	}

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

		this.financialUtilService = new FinancialUtilService_Service(wsdlDoc, qname)
		.getFinancialUtilServiceSoapHttpPort();
		// financialUtilService =
		// financialUtilService_Service.getFinancialUtilServiceSoapHttpPort();//(securityFeature);

		Map<String, Object> requestContext = ((BindingProvider) financialUtilService).getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getProperty(Config.WS_URL));
		requestContext.put(BindingProvider.USERNAME_PROPERTY, config.getProperty(Config.WS_URL_USERNAME));
		requestContext.put(BindingProvider.PASSWORD_PROPERTY, config.getProperty(Config.WS_URL_PASSWORD));
	}

}
