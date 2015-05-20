package com.gt.util;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.gt.Config;

public class GetFilesList 
{
	private Config config;
	public GetFilesList(Config config)
	{
		this.config=config;
	}

	
	
	public List<String> GetFileNames(String folderpath)
	{
		File fpath=new File(folderpath);
		List<String> file_list=new ArrayList<String>();
		File folder =fpath;
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles)
		{
			
		
		    if (file.isFile()) 
		    {		    	
		    	
		    	if(FileTypeValid(file.toString())==true)
		    	{
		    		file_list.add(file.toString());
		    	}
		    	
		    }
		}
		
		return file_list;
	}
	
	
	public boolean FileTypeValid(String filename)
	{
		
		String ext=FilenameUtils.getExtension(filename);
		switch(ext)
		{
			case "csv":
			case "CSV":
			case "txt":
			case "TXT":
				return true;
		}
		
		return false;
		
	}
	
	
	
}
