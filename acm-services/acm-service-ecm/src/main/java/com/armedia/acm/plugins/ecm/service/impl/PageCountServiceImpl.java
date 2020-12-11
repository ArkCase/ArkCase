package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.service.PageCountService;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PageCountServiceImpl implements PageCountService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private final static String MIME_TYPE_PDF = "application/pdf";
    private final static String MIME_TYPE_DOC = "application/msword";
    private final static String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private final static String MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    private final static String MIME_TYPE_XLS = "application/vnd.ms-excel";
    private final static String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final static int DEFAULT_NUMBER_OF_PAGES = 1;

    @Override
    @Deprecated
    /**
     * @deprecated use getNumberOfPages(String, File)
     */
    public int getNumberOfPages(String mimeType, byte[] data) throws IOException
    {
        File file = null;
        try
        {
            file = File.createTempFile("arkcase-get-number-of-pages-", null);
            FileUtils.writeByteArrayToFile(file, data);
            return getNumberOfPages(mimeType, file);
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }
    }

    @Override
    public int getNumberOfPages(String mimeType, File file) throws IOException
    {
        int numberOfPages = -1;

        try
        {
            numberOfPages = getNumberOfPagesPerMimeType(mimeType, file);
        }
        catch (Exception e)
        {
            log.warn("Failed to find number of pages for document", e);
        }
        return numberOfPages;
    }

    private int getNumberOfPagesPerMimeType(String mimeType, File file) throws IOException
    {
        switch (mimeType)
        {
        case MIME_TYPE_PDF:
            return getNumberOfPagesPdfFile(file);
        case MIME_TYPE_DOCX:
            return getNumberOfPagesDocxFile(file);
        case MIME_TYPE_DOC:
            return getNumberOfPagesDocFile(file);
        case MIME_TYPE_PPTX:
            return getNumberOfPagesPptxFile(file);
        case MIME_TYPE_XLS:
            return getNumberOfPagesXlsFile(file);
        case MIME_TYPE_XLSX:
            return getNumberOfPagesXlsxFile(file);
        default:
            log.warn("Still don't know how to retrieve the page count for [{}] mime type", mimeType);
            return DEFAULT_NUMBER_OF_PAGES;
        }
    }

    private int getNumberOfPagesPdfFile(File file) throws IOException
    {
        PdfReader reader = new PdfReader(new RandomAccessFileOrArray(file.getAbsolutePath()), null);
        int numberOfPages = reader.getNumberOfPages();
        reader.close();
        return numberOfPages;
    }

    private int getNumberOfPagesDocFile(File file) throws IOException
    {
        HWPFDocument wordDoc = new HWPFDocument(new FileInputStream(file));
        int numberOfPages = wordDoc.getSummaryInformation().getPageCount();
        wordDoc.close();
        return numberOfPages;
    }

    private int getNumberOfPagesDocxFile(File file) throws IOException
    {
        XWPFDocument wordDocx = new XWPFDocument(new FileInputStream(file));
        int numberOfPages = wordDocx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
        wordDocx.close();
        return numberOfPages;
    }

    private int getNumberOfPagesPptxFile(File file) throws IOException
    {
        XMLSlideShow pptxSlideShow = new XMLSlideShow(new FileInputStream(file));
        int numberOfPages = pptxSlideShow.getSlides().size();
        pptxSlideShow.close();
        return numberOfPages;
    }

    private int getNumberOfPagesXlsFile(File file) throws IOException
    {
        HSSFWorkbook xlsDoc = new HSSFWorkbook(new FileInputStream(file));
        int numberOfPages = xlsDoc.getNumberOfSheets();
        xlsDoc.close();
        return numberOfPages;
    }

    private int getNumberOfPagesXlsxFile(File file) throws IOException
    {
        XSSFWorkbook xlsxDoc = new XSSFWorkbook(new FileInputStream(file));
        int numberOfPages = xlsxDoc.getNumberOfSheets();
        xlsxDoc.close();
        return numberOfPages;
    }

}
