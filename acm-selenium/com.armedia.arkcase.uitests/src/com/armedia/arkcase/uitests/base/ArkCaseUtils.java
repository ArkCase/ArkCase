package com.armedia.arkcase.uitests.base;

import java.io.File;

import org.junit.Assert;

public class ArkCaseUtils {

	public void checkIfFileIsDownloaded(String name) {
		String downloadPath = System.getProperty("user.home") + "/.arkcase/seleniumTests/seleniumDownload";
		Assert.assertTrue("File does not exist in SeleniumDownload folder", isFileDownloaded_Ext(downloadPath, name));

	}

	public void deleteFolder() {

		deleteDir(new File(System.getProperty("user.home") + "/.arkcase/seleniumTests/seleniumDownload"));
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

	public void createFolder() {
		String path = (System.getProperty("user.home") + "/.arkcase/seleniumTests/seleniumDownload");

		File dir = new File(path);
		dir.mkdirs();

	}

	public static boolean deleteDir(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();

	}

}
