package com.armedia.acm.pdf.service;

import com.armedia.acm.pdf.PdfServiceException;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by nebojsha on 30.01.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-pdf-utilities-test.xml"
})
public class PdfServiceImplTest
{
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    PdfService pdfService;

    private String outputFileName = System.getProperty("java.io.tmpdir") + File.separator + "multipageImage.tif";
    private File outputFile = new File(outputFileName);

    @Before
    public void setUp() throws Exception
    {
        FileUtils.deleteQuietly(outputFile);
    }

    @Test
    public void generateMultiPageTiffFromPdf() throws PdfServiceException
    {
        assertNotNull(pdfService);


        FileSystemResource multipagePdf = new FileSystemResource(this.getClass().getResource("/pdfs/multipage_document.pdf").getFile());
        assertTrue(multipagePdf.exists());

        log.debug("file length is {}", outputFile.length());
        pdfService.generateTiffFromPdf(multipagePdf.getFile(), outputFile);
    }

    @Test
    public void mergeDocuments() throws Exception
    {
        assertNotNull(pdfService);

        FileSystemResource abstractPdf = new FileSystemResource(this.getClass().getResource("/pdfs/Abstract.pdf").getFile());
        assertTrue(abstractPdf.exists());
        FileSystemResource authorizationPdf = new FileSystemResource(this.getClass().getResource("/pdfs/Authorization.pdf").getFile());
        assertTrue(authorizationPdf.exists());
        FileSystemResource invoicePdf = new FileSystemResource(this.getClass().getResource("/pdfs/Invoice.pdf").getFile());
        assertTrue(invoicePdf.exists());

        // get page count of the Abstract
        PDDocument abstractDoc = PDDocument.load(abstractPdf.getFile());
        int abstractPageCount = abstractDoc.getNumberOfPages();
        log.debug("Number of pages in Abstract document [{}]", abstractPageCount);
        abstractDoc.close();

        // get page count of the Abstract
        PDDocument authorizationDoc = PDDocument.load(authorizationPdf.getFile());
        int authorizationPageCount = authorizationDoc.getNumberOfPages();
        log.debug("Number of pages in Authorization document [{}]", authorizationPageCount);
        authorizationDoc.close();

        // get page count of the Abstract
        PDDocument invoiceDoc = PDDocument.load(invoicePdf.getFile());
        int invoicePageCount = invoiceDoc.getNumberOfPages();
        log.debug("Number of pages in Invoice document [{}]", invoicePageCount);
        invoiceDoc.close();

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfService.addSource(pdfMergerUtility, abstractPdf.getInputStream());
        pdfService.addSource(pdfMergerUtility, authorizationPdf.getInputStream());
        pdfService.addSource(pdfMergerUtility, invoicePdf.getInputStream());
        pdfService.mergeSources(pdfMergerUtility, outputFileName);

        PDDocument mergedDoc = PDDocument.load(outputFile);
        int mergedPageCount = mergedDoc.getNumberOfPages();
        log.debug("Number of pages in merged document [{}]", invoicePageCount);
        mergedDoc.close();

        assertEquals(mergedPageCount, abstractPageCount + authorizationPageCount + invoicePageCount);
    }


    @After
    public void tearDown() throws Exception
    {
        log.debug(outputFile.getAbsolutePath());
        FileUtils.deleteQuietly(outputFile);
    }
}