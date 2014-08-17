package com.armedia.acm.service.orbeon.forms.model;

import java.util.List;

public class FormAttachment {
	List<String> fileInfo;

	public List<String> getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(List<String> fileInfo) {
		this.fileInfo = fileInfo;
	}

	@Override
	public String toString() {
		return "FormAttachment [fileInfo=" + fileInfo + "]";
	}
	
}
