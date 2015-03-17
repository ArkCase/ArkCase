/**
 * 
 */
package com.armedia.acm.plugins.ecm.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author riste.tutureski
 *
 */
public class AcmMultipartFile implements MultipartFile {

	private String name;
	private String originalFilename;
	private String contentType;
	private boolean empty;
	private long size;
	private byte[] bytes;
	private InputStream inputStream;
	
	public AcmMultipartFile(){
		
	}
	
	public AcmMultipartFile(String name, String originalFileName, String contentType, boolean empty, long size, byte[] bytes, InputStream inputStream, boolean uniqueFileName){
		if (uniqueFileName)
		{
			this.name = createUniqueIdentificator(name);	
			this.originalFilename = createUniqueIdentificator(originalFileName);
		}
		else
		{
			this.name = name;	
			this.originalFilename = originalFileName;
		}
		
		this.contentType = contentType;
		this.empty = empty;
		this.size = size;
		this.bytes = bytes;
		this.inputStream = inputStream;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}	

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean isEmpty() {
		return empty;
	}
	
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	@Override
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		FileCopyUtils.copy(bytes, dest);
	}
	
	private String createUniqueIdentificator(String input)
	{
		if (input != null && input.length() > 0)
		{
			input = input.replace(" ", "_");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
			String dateString = dateFormat.format(new Date());
			
			String[] inputArray = input.split("\\.");
			
			if (inputArray != null && inputArray.length == 1)
			{
				input = input +  "_" + dateString;
			} 
			else if (inputArray != null && inputArray.length > 1)
			{
				input = input.replace("." + inputArray[inputArray.length - 1], "_" + dateString + "." + inputArray[inputArray.length - 1]);
			}
		}
		
		return input;
	}

}
