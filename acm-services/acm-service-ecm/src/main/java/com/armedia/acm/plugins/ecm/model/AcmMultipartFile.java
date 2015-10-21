/**
 * 
 */
package com.armedia.acm.plugins.ecm.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
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
	private String type;
	
	public AcmMultipartFile(){
		
	}
	
	public AcmMultipartFile(String name, String originalFileName, String contentType, boolean empty, long size, byte[] bytes, InputStream inputStream, boolean uniqueFileName)
	{
		init(name, originalFileName, contentType, empty, size, bytes, inputStream, uniqueFileName);
	}

	public AcmMultipartFile(String name, String originalFileName, String contentType, boolean empty, long size, byte[] bytes, InputStream inputStream, boolean uniqueFileName, String type)
	{
		init(name, originalFileName, contentType, empty, size, bytes, inputStream, uniqueFileName);
		this.type = type;
	}

	private void init(String name, String originalFileName, String contentType, boolean empty, long size, byte[] bytes, InputStream inputStream, boolean uniqueFileName)
	{
		FolderAndFilesUtils folderAndFilesUtils = new FolderAndFilesUtils();
		if (uniqueFileName)
		{
			this.name = folderAndFilesUtils.createUniqueIdentificator(name);
			this.originalFilename = folderAndFilesUtils.createUniqueIdentificator(originalFileName);
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
}
