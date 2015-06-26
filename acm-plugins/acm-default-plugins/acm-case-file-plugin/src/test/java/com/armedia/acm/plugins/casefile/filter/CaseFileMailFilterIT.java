package com.armedia.acm.plugins.casefile.filter;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.Message;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/spring-integration-case-file-test.xml", "classpath:/spring/spring-library-property-file-manager.xml"})
public class CaseFileMailFilterIT extends EasyMockSupport {
    private Message messageMock;

    @Autowired
    CaseFileMailFilter filter;


    @Test
    public void testAcceptTrue() throws Exception {
        messageMock = createMock(Message.class);
        String caseNumber = "20150511_123123";
        EasyMock.expect(messageMock.getSubject()).andReturn("aadasdasdasd_" + caseNumber + " some random text here and numbers 123123").anyTimes();

        replayAll();

        assertTrue(filter.accept(messageMock));
        verifyAll();
    }

    @Test
    public void testAcceptFalse() throws Exception {
        messageMock = createMock(Message.class);
        String caseNumber = "2150511_123123";
        EasyMock.expect(messageMock.getSubject()).andReturn("aadasdasdasd_" + caseNumber + " some random text here and numbers 123123").anyTimes();

        replayAll();

        assertFalse(filter.accept(messageMock));
        verifyAll();
    }
}