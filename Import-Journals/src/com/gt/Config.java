/**
 * Copyright (c) 2014-15 GT. All rights reserved.
 */
package com.gt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.gt.core.AppException;

/**
 * Class class to load the configuration from properties file
 * 
 * @author Krishna Mohan
 */
public class Config {

	

	public static final String DEFAULT_TEMP_FOLDER = "default.temp.folder";
	public static final String DEFAULT_CSV_FILE_NAME = "default.csv.filename";
	public static final String DEFAULT_CSV_FILE_NAME_PATTERN = "default.csv.filename.pattern";
	public static final String DEFAULT_ZIP_FILE_NAME = "default.zip.filename.pattern";
	public static final String DEFAULT_ARCHIVES_FOLDER="default.archives.folder";

	

	public static final String DEFAULT_CSV_FILE_PATH="default.csv.filepath";
	public static final String DEFAULT_GEN_FILE_CLEANUP="default.generated.files.cleanup";
	public static final String DEFAULT_INTERIM_FILE_CLEANUP="default.interim.files.cleanup";


	public static final String SMTP_HOST = "mail.smtp.host";
	public static final String SMTP_PORT = "mail.smtp.port";
	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String SMTP_TLS_ENABLED = "mail.smtp.tlsenabled";
	public static final String SMTP_USERNAME = "mail.smtp.user";
	public static final String SMTP_PASSWORD = "mail.smtp.password";

	// Fusion URL constants
	public static final String WS_URL = "fusion.service.ws.url";
	public static final String WS_URL_USERNAME = "fusion.service.ws.username";
	public static final String WS_URL_PASSWORD = "fusion.service.ws.password";

	// ESS Job Submit param constants
	public static final String ESS_JOB_PASSWORD = "fusion.service.ws.password";
	public static final String ESS_JOB_DEFINITIONNAME = "ess.job.definitionname";
	public static final String ESS_JOB_PACKAGENAME = "ess.job.jobPackageName";
	public static final String ESS_JOB_PARAM1 = "ess.job.param1";
	public static final String ESS_JOB_PARAM2 = "ess.job.param2";
	public static final String ESS_JOB_PARAM3 = "ess.job.param3";
	public static final String ESS_JOB_PARAM4 = "ess.job.param4";

	// ESS Job Status param constants
	public static final String ESS_JOB_STATUS_RUNNING = "ess.job.status.running";
	public static final String ESS_JOB_STATUS_SUCCEEDED = "ess.job.status.succeeded";
	public static final String ESS_JOB_STATUS_ERROR = "ess.job.status.error";
	public static final String ESS_JOB_STATUS_WARNING = "ess.job.status.warning";
	public static final String ESS_JOB_STATUS_CHECK_TIMER = "ess.job.status.check.time";

	// UploadFileToUCM param constants
	public static final String UPLOAD_FILE_NAME = "upload.file.name";
	public static final String UPLOAD_FILE_DOC_TITLE = "upload.file.doc.title";
	public static final String UPLOAD_FILE_TYPE = "upload.file.contenttype";
	public static final String UPLOAD_FILE_ACCOUNT = "upload.file.account";
	public static final String UPLOAD_FILE_SEC_GROUP = "upload.file.sec.group";
	public static final String UPLOAD_FILE_DOC_AUTHOR = "upload.file.doc.author";

	public static final String JOB_STATUS_EMAIL_TO = "job.status.emails.to";
	public static final String JOB_STATUS_EMAIL_FROM = "job.status.emails.from";
	public static final String JOB_STATUS_EMAIL_SUBJECT = "job.status.emails.subject";
	public static final String JI_JOB_STATUS_EMAIL_SUBJECT = "ji.job.status.emails.subject";
	public static final String JOB_STATUS_EMAIL_MESSAGE = "job.status.emails.message";
	public static final String JI_JOB_STATUS_EMAIL_MESSAGE = "ji.job.status.emails.message";
	

	public static final String ERROR_LOG_EMAIL_TO = "error.log.emails.to";
	public static final String ERROR_LOG_EMAIL_FROM = "error.log.emails.from";
	public static final String ERROR_LOG_EMAIL_SUBJECT = "error.log.emails.subject";
	public static final String ERROR_LOG_EMAIL_MESSAGE = "error.log.emails.message";

	public static final String DEFAULT_LOG_FILENAME = "default.log.filename";
	
	//@Krishna New propertiesfor journalimports
	
	public static final String JOURNAL_IMP_ESS_JOB_PARAM3_CONFIG = "ji.ess.job.param3.configread";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM4_CONFIG = "ji.ess.job.param4.configread";
	public static final String JOURNAL_IMP_ESS_JOB_PACKAGENAME = "ji.ess.job.jobPackageName";
	public static final String JOURNAL_IMP_ESS_JOB_DEFINITIONNAME = "ji.ess.job.definitionname";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM1 = "ji.ess.job.param1";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM2 = "ji.ess.job.param2";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM3 = "ji.ess.job.param3";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM4 = "ji.ess.job.param4";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM5 = "ji.ess.job.param5";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM6 = "ji.ess.job.param6";
	public static final String JOURNAL_IMP_ESS_JOB_PARAM7 = "ji.ess.job.param7";
	
	
	//Threading constants
	public static final String THREADS_COUNT="thread.count";
	public static final String SLEEP_TIME="sleep.time";
			
			
	
	
	
	private static final Properties prop = new Properties();

	/**
	 * @param path
	 * @throws IOException
	 */
	public Config(String path) throws IOException {
		InputStream fin = null;
		try {
			fin = new FileInputStream(path);
			prop.load(fin);
		} catch (Exception e) {
			throw new AppException("Failed loading config file: " + path);
		} finally {
			if (fin != null)
				fin.close();
		}
	}

	/**
	 * Set value to the given key
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		prop.setProperty(key, value);
	}

	/**
	 * Get value for the give key property
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		String value = prop.getProperty(key);
		if (value == null) {
			throw new AppException("Configuration not found for: " + key);
		}
		return value;
	}
}
