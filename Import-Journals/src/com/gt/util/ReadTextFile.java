package com.gt.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadTextFile {

	public static String filename="D:/importjournals/temp/40873.log";
	
	public static void main(String ar[])
	{



		FileInputStream fstream;
		try 
		{
			fstream = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null)  
			{
					int position=strLine.indexOf("Waiting on Child Process with request ID:"); 	
					if(position>0)
					{
						System.out.println( strLine.substring(50, 55));
					}			
				
			}

		
			br.close();
		} 
		catch(IOException ee)
		{
			ee.printStackTrace();
		}


	}
}
