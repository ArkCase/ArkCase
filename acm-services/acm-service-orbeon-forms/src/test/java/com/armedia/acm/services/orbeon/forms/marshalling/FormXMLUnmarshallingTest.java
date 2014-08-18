package com.armedia.acm.services.orbeon.forms.marshalling;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.armedia.acm.service.orbeon.forms.model.ROIFormOrbeon;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-form-service-test.xml"
})
public class FormXMLUnmarshallingTest
{
    String FILE_NAME = "file.xml";
    @Autowired
	private Unmarshaller unmarshaller;
    
    @Autowired
	private Marshaller marshaller;
    
    private File orbeonXMLfile;
    
    @Before
    public void setUp() throws Exception
    {    	
    	URL url = this.getClass().getResource("/orbeonFormData.xml");
    	orbeonXMLfile = new File(url.getFile());
    }
    
    /**
     * This test tests the Castor unmarshalling capability of unmarshalling of a
     * sample orbeon form data.
     * 
     * @throws IOException
     */
    @Test
    public void xmlStringToForm() throws IOException {
        FileInputStream is = null;
        
        ROIFormOrbeon form = null;
        try {
            is = new FileInputStream(orbeonXMLfile);
            form = (ROIFormOrbeon) this.unmarshaller.unmarshal(new StreamSource(is));
        } finally {
            if (is != null) {
                is.close();
            }
        }

        assertEquals("David", form.getHeader().getFirstName());
        assertEquals("20140430_52", form.getDetail().getComplaintNumber());
        assertEquals("52", form.getFooter().getComplaint_id());
		
	}
  
}
