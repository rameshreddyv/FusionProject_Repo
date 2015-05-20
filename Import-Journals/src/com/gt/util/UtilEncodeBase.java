/**
 * Copyright (c) 2014-15 GT. All rights reserved.
 */
package com.gt.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.gt.Config;

public class UtilEncodeBase {

	private static Logger logger = Logger.getLogger(UtilEncodeBase.class);

	public UtilEncodeBase() {
		super();
	}

	public static void main(String[] a) throws Exception {

		// Enter the filename as input
		File br = new File(a[0]);
		// Convert the file into Byte
		byte[] bytes = loadFile(br);

		// Call the api for Base64 encoding
		byte[] encoded = Base64.encodeBase64(bytes);
		String encStr = new String(encoded);
		// Print the file
		System.out.println(encStr);

	}

	public static byte[] getByteArray(String fileName) {
		File file = new File(fileName);
		byte[] retBytesData = null;
		FileInputStream is = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		// byte[] data = new byte[16384];
		byte[] data = new byte[(int) file.length()];
		try {

			is = new FileInputStream(file);
			/*
			 * while ((nRead = is.read(data, 0, data.length)) != -1) { buffer.write(data, 0, nRead); } buffer.flush();
			 */
			retBytesData = IOUtils.toByteArray(is);
			is.close();
		} catch (IOException e) {
			System.out.println("In getByteArray:IO Exception");
			e.printStackTrace();
		}
		// return buffer.toByteArray();
		return retBytesData;
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}

	public static File reConstructDownloadFile(String fileName, Config config, byte[] fileAsBytes) {

		try {
			String folderName = config.getProperty(Config.DEFAULT_TEMP_FOLDER);
			// String fileName = config.getProperty(Config.DFAULT_CSV_FILE_NAME) + CSV_EXT;
			Files.write(Paths.get(folderName + fileName), fileAsBytes);
			return new File(folderName + fileName);

		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
