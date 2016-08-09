package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.service.PageCountService;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PageCountServiceImpl implements PageCountService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public int getNumberOfPages(String mimeType, byte[] data) throws IOException
    {
        int numberOfPages = -1;
        if ("application/pdf".equals(mimeType))
        {
            PDDocument pdDocument = null;
            try
            {
                pdDocument = PDDocument.load(new ByteArrayInputStream(data));
                numberOfPages = pdDocument.getNumberOfPages();
            } catch (IOException e)
            {
                throw new IOException(e);
            } finally
            {
                if (pdDocument != null)
                {
                    try
                    {
                        pdDocument.close();
                    } catch (Exception ex)
                    {
                        log.error("cannot close PDF: {}", ex.getMessage(), ex);
                    }
                }
            }
        } else
        {
            log.warn("Still don't know how to retrieve the page count for [{}] mime type");
        }
        return numberOfPages;
    }

}
