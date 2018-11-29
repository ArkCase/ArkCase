package com.armedia.acm.proxy.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProxyServletTest
{

    @Test
    public void encodeUriQuery() throws Exception
    {
        String in = "The Grateful Dead";
        String expected = "The%20Grateful%20Dead";

        StringBuilder actual = (StringBuilder) ProxyServlet.encodeUriQuery(in);

        assertEquals(expected, actual.toString());
    }
    
}
