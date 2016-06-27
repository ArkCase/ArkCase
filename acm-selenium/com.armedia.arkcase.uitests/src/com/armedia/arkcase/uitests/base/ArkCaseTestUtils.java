package com.armedia.arkcase.uitests.base;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ArkCaseTestUtils {

	public static void uploadPNGPicture() throws IOException, AWTException, InterruptedException {

		String file = System.getProperty("user.home") + "\\.arkcase\\seleniumTests\\filesForUpload\\imageprofile.png";
		setClipboardData(file);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);

	}

	public static void uploadPdf() throws IOException, AWTException {

		String file = System.getProperty("user.home") + "\\.arkcase\\seleniumTests\\filesForUpload\\caseSummary.pdf";
		setClipboardData(file);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);

	}

	public static void uploadDocx() throws IOException, AWTException {

		String file = System.getProperty("user.home")
				+ "\\.arkcase\\seleniumTests\\filesForUpload\\ArkCaseTesting.docx";
		setClipboardData(file);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);

	}

	public static void uploadXlsx() throws IOException, AWTException {

		String file = System.getProperty("user.home") + "\\.arkcase\\seleniumTests\\filesForUpload\\caseSummary.xlsx";
		setClipboardData(file);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);

	}

	public static void setClipboardData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}
	
	
	
	public static void closeWordDocument() throws AWTException{
		try {
			
		
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ALT);
		robot.delay(1000);
		robot.keyPress(KeyEvent.VK_4);
		robot.delay(2000);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_4);
		}
		catch (Exception exception) {
		exception.printStackTrace();
		}
		
}
	
	
	
	
	
	
	
	
}
