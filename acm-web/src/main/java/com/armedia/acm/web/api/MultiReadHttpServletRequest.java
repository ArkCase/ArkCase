package com.armedia.acm.web.api;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Simple wrapper on HTTP request so that its body can be read multiple times.
 * <p>
 * Created by Bojan Milenkoski on 15.1.2016.
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper
{
    private byte[] rawData;

    public MultiReadHttpServletRequest(HttpServletRequest request) throws IOException
    {
        super(request);
        rawData = IOUtils.toByteArray(request.getReader());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);
        return new ServletInputStream()
        {
            @Override
            public int read() throws IOException
            {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}