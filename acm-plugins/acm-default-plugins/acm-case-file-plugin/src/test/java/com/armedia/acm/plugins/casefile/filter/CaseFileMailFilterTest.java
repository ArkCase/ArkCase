package com.armedia.acm.plugins.casefile.filter;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Message;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class CaseFileMailFilterTest extends EasyMockSupport
{
    private Message messageMock;

    private CaseFileMailFilter unit;

    @Before
    public void setUp() throws Exception
    {
        messageMock = createMock(Message.class);
        unit = new CaseFileMailFilter();
    }


    @Test
    public void accept_matches() throws Exception
    {
        String caseNumberPattern = "[\\d]{4}[\\d]{2}[\\d]{2}_[\\d]*";
        String objectTypePattern = "Case";
        unit.setCaseNumberRegexPattern(caseNumberPattern);
        unit.setCaseObjectTypeRegexPattern(objectTypePattern);

        String caseNumber = "20150511_123123";
        expect(messageMock.getSubject()).andReturn("Case " + caseNumber + " some random text here and numbers 123123").anyTimes();

        replayAll();

        assertTrue(unit.accept(messageMock));

        verifyAll();
    }

    @Test
    public void accept_noMatch() throws Exception
    {
        String caseNumberPattern = "[\\d]{4}[\\d]{2}[\\d]{2}_[\\d]*";
        String objectTypePattern = "Case";
        unit.setCaseNumberRegexPattern(caseNumberPattern);
        unit.setCaseObjectTypeRegexPattern(objectTypePattern);

        expect(messageMock.getSubject()).andReturn("Invitation to happy hour tonight").anyTimes();

        replayAll();

        assertFalse(unit.accept(messageMock));

        verifyAll();
    }

}