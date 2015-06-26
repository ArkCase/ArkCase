package com.armedia.acm.plugins.casefile.filter;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import javax.mail.Message;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class CaseFileMailFilterTest extends EasyMockSupport {
    private Message messageMock;

    @Test
    public void testAcceptTrue() throws Exception {
        messageMock = createMock(Message.class);
        String caseNumber = "20150511_123123";
        EasyMock.expect(messageMock.getSubject()).andReturn("aadasdasdasd_" + caseNumber + " some random text here and numbers 123123").anyTimes();

        replayAll();

        CaseFileMailFilter filter = new CaseFileMailFilter();
        assertTrue(filter.accept(messageMock));
        verifyAll();
    }

    @Test
    public void testAcceptFalse() throws Exception {
        messageMock = createMock(Message.class);
        String caseNumber = "2150511_123123";
        EasyMock.expect(messageMock.getSubject()).andReturn("aadasdasdasd_" + caseNumber + " some random text here and numbers 123123").anyTimes();

        replayAll();

        CaseFileMailFilter filter = new CaseFileMailFilter();
        assertFalse(filter.accept(messageMock));
        verifyAll();
    }
}