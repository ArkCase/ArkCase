package com.armedia.arkcase.uitests.base;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;



public class ArkCaseTestUtils extends ArkCaseTestBase{

	public static void uploadPNGPicture() throws IOException, AWTException, InterruptedException {

		String home = System.getProperty("user.home");
		File file = new File(home + "/.arkcase/seleniumTests/filesForUpload/imageprofile.png");
		try 
		{
		setClipboardData(file.toString());
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		}
		catch (Exception exp) {
        	exp.printStackTrace();
		}

	}

	public static void uploadPdf() throws IOException, AWTException {

		String home = System.getProperty("user.home");
		File file = new File(home + "/.arkcase/seleniumTests/filesForUpload/caseSummary.pdf");
		setClipboardData(file.toString());
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

		String home = System.getProperty("user.home");
		File file = new File(home +  "/.arkcase/seleniumTests/filesForUpload/ArkCaseTesting.docx");
		setClipboardData(file.toString());
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

		String home = System.getProperty("user.home");
		File file = new File(home + "/.arkcase/seleniumTests/filesForUpload/caseSummary.xlsx");
		setClipboardData(file.toString());
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

	public static void closeWordDocument() throws AWTException {
		try {

			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ALT);
			robot.delay(1000);
			robot.keyPress(KeyEvent.VK_F4);
			robot.delay(2000);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_F4);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}
	
	public static void presEnter() throws AWTException{
	
	Robot robot=new Robot();
	robot.keyPress(KeyEvent.VK_ENTER);
	robot.keyRelease(KeyEvent.VK_ENTER);
		
	}
	
	public static void shiftLeftAndPressEnter() throws AWTException{
		
	Robot robot=new Robot();
	robot.keyPress(KeyEvent.VK_LEFT);
	robot.keyRelease(KeyEvent.VK_LEFT);
	robot.delay(2000);
	robot.keyPress(KeyEvent.VK_ENTER);
	robot.keyRelease(KeyEvent.VK_ENTER);
		
	}
	
	public static void saveWordDocument()throws AWTException{
		
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_S);
		robot.delay(2000);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_S);
		
		
	}
	

	
	
	
	
	

}
