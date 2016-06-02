package com.armedia.arkcase.uitests.base;

import java.io.IOException;

public class ArkCaseTestUtils {

	
	
	
	public static void uploadPicture() throws IOException{
		
		String[] commands = new String[] {};
		commands = new String[] { "C:\\Users\\milan.jovanovski\\SleniumTests\\FilesForUppload\\test.exe" };
		Runtime.getRuntime().exec(commands);	
		
	}
	
	public static void uploadPdf()throws IOException{
		

		String[] commands = new String[] {};
		commands = new String[] {"C:\\Users\\milan.jovanovski\\SleniumTests\\FilesForUppload\\Witness.exe" };
		Runtime.getRuntime().exec(commands);		
		
		
	}
	
	public static void uploadDocx()throws IOException{
		
		
		String[] commands = new String[] {};
		commands = new String[] {"C:\\Users\\milan.jovanovski\\SleniumTests\\FilesForUppload\\SfSignature.exe" };
		Runtime.getRuntime().exec(commands);	
		
		
	}
	
	
	
	public static void uploadXlsx()throws IOException{
		
		
		String[] commands = new String[] {};
		commands = new String[] {"C:\\Users\\milan.jovanovski\\SleniumTests\\FilesForUppload\\GeneralRelease.exe" };
		Runtime.getRuntime().exec(commands);	
		
		
	}
	
	
	
	
	
	
	

}
