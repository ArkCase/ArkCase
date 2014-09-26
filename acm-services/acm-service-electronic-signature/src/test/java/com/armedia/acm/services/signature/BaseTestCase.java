package com.armedia.acm.services.signature;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

public class BaseTestCase {

	@Rule public TestName name = new TestName();
	
	@Before
	public void printBeforeTestRun() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("Starting Test: " + name.getMethodName());
		System.out.println("-------------------------------------------------------------------------------------");
	}
	
	@After
	public void printAfterTestRun() throws Exception
	{
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("Finished Test: " + name.getMethodName());
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println();
	}
}
