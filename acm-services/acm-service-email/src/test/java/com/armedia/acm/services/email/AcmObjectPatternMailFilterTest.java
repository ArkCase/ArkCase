package com.armedia.acm.services.email;

/*-
 * #%L
 * ACM Service: Email
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
