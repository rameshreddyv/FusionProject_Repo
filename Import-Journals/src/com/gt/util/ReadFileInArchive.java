package com.gt.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
 
/*
 * Here we will learn to read the file in Archive without extracting it.
 */
public class ReadFileInArchive {
 
 public static void main(String[] args) throws ZipException, IOException {
 Scanner scanner = new Scanner(System.in);
 System.out
 .println("Enter the location of Zip file to read(Along with file name)");
 String zipFileLocation = scanner.next();
 
 File file = new File("D:/importjournals/temp/40704.zip");
 ZipFile zipfile = new ZipFile(file);
 
 ZipEntry zipentry;
 /*
 System.out.println("nList of files in zip archive");
 int fileNumber = 0;
 for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e
 .hasMoreElements(); fileNumber++) {
 zipentry = e.nextElement();
 
 if (!zipentry.isDirectory()) {
 System.out.println(fileNumber + "-" + zipentry.getName());
 }
 }*/
 /*
 System.out
 .println("Enter the file name(With path) to see the content of the file ");
  */
 String fileNameToView = scanner.next();
 for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e
 .hasMoreElements();) {
 zipentry = e.nextElement();

 if (!zipentry.isDirectory()
 && zipentry.getName().equals(fileNameToView)) {
 InputStream inputstream = zipfile.getInputStream(zipentry);
 String str = new java.util.Scanner(inputstream).useDelimiter("").next();
 System.out.println("value in scanner str:" + str);
 }
 }
 
 }
}