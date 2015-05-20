/**
 * File:CSVHeader.java
 * @author Krishna Mohan
 * Date:03 Apr 2015
 * Company:Gigatude
 * 
 * */
package com.gt;

/**
 * This POJO class returns ledger id and group id [row index]
 * as exists in the excel file
 * 
 * */
public class CSVHeader {
	
	//Assigning Ledger ID row index from CSV file
	private static final int LEDGER_ID = 1;
	
	//Assigning Group ID row index from CSV file
	private static final int GROUP_ID=66;
	
	//Getters of LedgerID and GroupID
		public static int getLedgerId() 
		{
			return LEDGER_ID;
		}
		public static int getGroupId() {
			return GROUP_ID;
		}
		 
		
		   
		   
}


