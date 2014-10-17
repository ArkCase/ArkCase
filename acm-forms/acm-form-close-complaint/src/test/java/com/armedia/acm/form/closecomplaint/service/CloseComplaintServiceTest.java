/**
 * 
 */
package com.armedia.acm.form.closecomplaint.service;

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:/spring/spring-close-complaint-service-test.xml"    
})
public class CloseComplaintServiceTest {

	@Autowired
	private CloseComplaintService closeComplaintServiceTest;
	
	@Ignore
	@Test
	public void testInitFormData() throws Exception {

		String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/InitFormData.txt"));
		assertEquals(expected, closeComplaintServiceTest.get("init-form-data").toString());
	}

}
