package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.Vector;

public class AcmAuthenticationDetailsFactoryTest extends EasyMockSupport
{
    private HttpServletRequest mockRequest;
    private AcmAuthenticationDetailsFactory unit;

    private String xForwardedForHeader = "x-forwarded-for";

    @Before
    public void setUp() throws Exception
    {
        mockRequest = createMock(HttpServletRequest.class);

        unit = new AcmAuthenticationDetailsFactory();
    }

    @Test
    public void remoteIpAddress_shouldBeFromRequest_whenNoXForwardedForHeader() throws Exception
    {
        String forwardFor = null;
        verifyRemoteIp(forwardFor, xForwardedForHeader);
    }

    @Test
    public void remoteIpAddress_shouldBeHeader_whenXForwardedForHeaderHasOneValue() throws Exception
    {
        String forwardFor = "192.168.10.100";
        verifyRemoteIp(forwardFor, xForwardedForHeader);
    }

    @Test
    public void remoteIpAddress_shouldBeHeader_whenXForwardedForHeaderHasDifferentCase() throws Exception
    {
        String forwardFor = "192.168.10.100";
        String headerName = "X-Forwarded-For";
        verifyRemoteIp(forwardFor, headerName);
    }

    @Test
    public void remoteIpAddress_shouldBeFirstValue_whenXForwardedForHeaderHasMultipleValues() throws Exception
    {
        String forwardFor = "192.168.10.50, 10.40.10.55";
        verifyRemoteIp(forwardFor, xForwardedForHeader);
    }

    private void verifyRemoteIp(String forwardFor, String forwardHeaderName) throws Exception
    {
        String ip = "192.168.1.1";

        Vector<String> headerNames = new Vector<>();
        if (forwardFor != null)
        {
            headerNames.add(forwardHeaderName);
            expect(mockRequest.getHeader(forwardHeaderName)).andReturn(forwardFor);
        }

        expect(mockRequest.getRemoteAddr()).andReturn(ip);
        expect(mockRequest.getSession(false)).andReturn(null);
        expect(mockRequest.getHeaderNames()).andReturn(headerNames.elements());

        replayAll();

        AcmAuthenticationDetails details = unit.buildDetails(mockRequest);

        verifyAll();

        String expectedIp = forwardFor == null ? ip : forwardFor;
        if (expectedIp.indexOf(",") > 0)
        {
            expectedIp = expectedIp.substring(0, expectedIp.indexOf(","));
        }

        assertEquals(expectedIp, details.getRemoteAddress());
    }
}
