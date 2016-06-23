package com.armedia.arkcase.uitests.base;

import java.io.File;

import org.junit.Assert;

public class CheckIfFileIsDownloaded {

	public void checkIfFileIsDownloaded(String name) {
		String downloadPath = System.getProperty("user.home") + "/.arkcase/seleniumTests/SeleniumDownload";
		Assert.assertTrue("File does not exist in SeleniumDownload folder", isFileDownloaded_Ext(downloadPath, name));

	}

	private boolean isFileDownloaded_Ext(String dirPath, String ext) {
		boolean flag = false;
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			flag = false;
		}

		for (File file : files) {
			if (file.getName().startsWith(ext)) {
				flag = true;
			}
		}
		return flag;
	}

}
