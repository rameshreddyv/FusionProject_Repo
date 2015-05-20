/**
 * Copyright (c) 2014-15 GT. All rights reserved.
 */
package com.gt.util;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.gt.Config;
import com.gt.core.AppException;


/**
 * Class to send Email notification
 * 
 * @author muraliK
 */
public class MailSender {

	//private static Logger logger = Logger.getLogger(RatesHandler.class);
	private static Logger logger = Logger.getLogger(MailSender.class);

	private Config config;

	public MailSender(Config config) {
		this.config = config;
	}

	/**
	 * Send email to recipients and attach files if any
	 * 
	 * @param toAddress
	 * @param subject
	 * @param message
	 * @param attachFiles
	 */
	public void sendEmail(String toAddress, String subject, String message, String... attachFiles) {

		try {
			logger.info("in sendEmail()");

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(config.getProperty(Config.SMTP_USERNAME),
						config.getProperty(Config.SMTP_PASSWORD));
				}
			};
			Session session = Session.getInstance(getProperties(), auth);

			// creates a new e-mail message
			Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(config.getProperty(Config.JOB_STATUS_EMAIL_FROM)));
			InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());

			// creates message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message, "text/html");

			// creates multi-part
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// adds attachments
			if (attachFiles != null && attachFiles.length > 0) {
				for (String filePath : attachFiles) {
					MimeBodyPart attachPart = new MimeBodyPart();

					try {
						attachPart.attachFile(filePath);
					} catch (IOException ex) {
						if (logger.isDebugEnabled()) {
							ex.printStackTrace();
						}
						throw new AppException("Failed attaching files to notification email " + ex.getMessage());
					}

					multipart.addBodyPart(attachPart);
				}
			}

			// sets the multi-part as e-mail's content
			msg.setContent(multipart);

			// sends the e-mail
			Transport.send(msg);
			logger.info("Mail sent successfully!");
		} catch (AppException e) {
			logger.error("Failed sending email " + e.getMessage());
			// throw e;
		} catch (Exception e) {
			logger.error("Failed sending email " + e.getMessage());

			// throw new AppException("Failed sending email " + e.getMessage(), e);
		}
	}

	private Properties getProperties() {
		// sets SMTP server properties

		Properties properties = new Properties();
		properties.put("mail.smtp.host", config.getProperty(Config.SMTP_HOST));
		properties.put("mail.smtp.port", config.getProperty(Config.SMTP_PORT));
		properties.put("mail.smtp.auth", config.getProperty(Config.SMTP_AUTH));
		properties.put("mail.smtp.starttls.enable", config.getProperty(Config.SMTP_TLS_ENABLED));
		properties.put("mail.user", config.getProperty(Config.SMTP_USERNAME));
		properties.put("mail.password", config.getProperty(Config.SMTP_PASSWORD));
		return properties;
	}
}
