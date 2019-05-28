package com.azkj.pad.model;

public class DownLoadFile
{
	private String srcnum;
	private String filename;
	private String filepath;
	private String fileid;
	private String serverpath;
	private String downName;
	private int fileType;
	
	
	public int getFileType()
	{
		return fileType;
	}
	public void setFileType(int fileType)
	{
		this.fileType = fileType;
	}
	public String getSrcnum()
	{
		return srcnum;
	}
	public void setSrcnum(String srcnum)
	{
		this.srcnum = srcnum;
	}
	public String getFilename()
	{
		return filename;
	}
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	public String getFilepath()
	{
		return filepath;
	}
	public void setFilepath(String filepath)
	{
		this.filepath = filepath;
	}
	public String getFileid()
	{
		return fileid;
	}
	public void setFileid(String fileid)
	{
		this.fileid = fileid;
	}
	public String getServerpath()
	{
		return serverpath;
	}
	public void setServerpath(String serverpath)
	{
		this.serverpath = serverpath;
	}
	public String getDownName()
	{
		return downName;
	}
	public void setDownName(String downName)
	{
		this.downName = downName;
	}
	
	
	
}