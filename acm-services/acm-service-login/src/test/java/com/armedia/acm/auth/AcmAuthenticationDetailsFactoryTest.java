package com.armedia.acm.auth;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Vector;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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

        Vector<String> headerNames = new Vector<String>();
        if ( forwardFor != null )
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
        if ( expectedIp.indexOf(",") > 0 )
        {
            expectedIp = expectedIp.substring(0, expectedIp.indexOf(","));
        }

        assertEquals(expectedIp, details.getRemoteAddress());
    }
}
