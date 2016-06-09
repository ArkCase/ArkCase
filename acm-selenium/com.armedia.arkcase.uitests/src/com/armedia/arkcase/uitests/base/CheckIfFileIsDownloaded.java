package com.armedia.arkcase.uitests.base;

import java.io.File;

import org.junit.Assert;

public class CheckIfFileIsDownloaded {

	public void checkIfFileIsDownloaded(String name) {
		String downloadPath = "C:\\Users\\milan.jovanovski\\SleniumTests\\SeleniumDownload";
		Assert.assertTrue("File is not downloaded", isFileDownloaded_Ext(downloadPath, name));

	}

	private boolean isFileDownloaded_Ext(String dirPath, String ext) {
		boolean flag = false;
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			flag = false;
		}

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().startsWith(ext)) {
				flag = true;
			}
		}
		return flag;
	}

}
