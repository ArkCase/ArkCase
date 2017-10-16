package com.armedia.acm.services.email;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.email.filter.AcmObjectPatternMailFilter;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Message;

public class AcmObjectPatternMailFilterTest extends EasyMockSupport
{
    private Message messageMock;

    private AcmObjectPatternMailFilter unit;

    @Before
    public void setUp() throws Exception
    {
        messageMock = createMock(Message.class);
        unit = new AcmObjectPatternMailFilter("[\\d]{4}[\\d]{2}[\\d]{2}_[\\d]*", "Case");
    }

    @Test
    public void acceptMatch() throws Exception
    {
        String caseNumber = "20150511_123123";
        expect(messageMock.getSubject()).andReturn("Case " + caseNumber + " some random text here and numbers 123123").anyTimes();

        replayAll();

        assertTrue(unit.accept(messageMock));

        verifyAll();
    }

    @Test
    public void acceptNoMatch() throws Exception
    {
        expect(messageMock.getSubject()).andReturn("Invitation to happy hour tonight").anyTimes();

        replayAll();

        assertFalse(unit.accept(messageMock));

        verifyAll();
    }

}
