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
            try (PDDocument pdDocument = PDDocument.load(new ByteArrayInputStream(data)))
            {
                numberOfPages = pdDocument.getNumberOfPages();
            }
        } else
        {
            log.warn("Still don't know how to retrieve the page count for [{}] mime type");
        }
        return numberOfPages;
    }

}
