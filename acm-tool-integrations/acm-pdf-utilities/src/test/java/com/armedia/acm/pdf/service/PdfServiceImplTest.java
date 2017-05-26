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
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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

    private String tiffOutputFileName = System.getProperty("java.io.tmpdir") + File.separator + "multipageImage.tif";
    private File tiffOutputFile = new File(tiffOutputFileName);

    private String pdfOutputFileName = System.getProperty("java.io.tmpdir") + File.separator + "Merged.pdf";
    private File pdfOutputFile = new File(pdfOutputFileName);

    @Before
    public void setUp() throws Exception
    {
        FileUtils.deleteQuietly(tiffOutputFile);
    }

    @Test
    public void generateMultiPageTiffFromPdf() throws PdfServiceException
    {
        assertNotNull(pdfService);


        FileSystemResource multipagePdf = new FileSystemResource(this.getClass().getResource("/pdfs/multipage_document.pdf").getFile());
        assertTrue(multipagePdf.exists());

        log.debug("file length is {}", tiffOutputFile.length());
        pdfService.generateTiffFromPdf(multipagePdf.getFile(), tiffOutputFile);
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
        pdfService.mergeSources(pdfMergerUtility, pdfOutputFileName);

        PDDocument mergedDoc = PDDocument.load(pdfOutputFile);
        int mergedPageCount = mergedDoc.getNumberOfPages();
        log.debug("Number of pages in merged document [{}]", mergedPageCount);
        mergedDoc.close();

        assertEquals(mergedPageCount, abstractPageCount + authorizationPageCount + invoicePageCount);
    }

    @Test
    public void extractPages() throws Exception
    {
        String filename = "multipage_document.pdf";
        List<Integer> pageNumbers = Arrays.asList(1, 2, 4);
        assertNotNull(pdfService);

        FileSystemResource sourcePdf = new FileSystemResource(this.getClass().getResource("/pdfs/" + filename).getFile());
        assertTrue(sourcePdf.exists());

        InputStream sourceDocInputStream = sourcePdf.getInputStream();
        InputStream extractedDocInputStream = pdfService.extractPages(sourceDocInputStream, filename, pageNumbers);
        PDDocument extractedDoc = PDDocument.load(extractedDocInputStream);
        int extractedPageCount = extractedDoc.getNumberOfPages();
        log.debug("Number of pages in extracted document [{}]", extractedPageCount);
        extractedDoc.close();
        extractedDocInputStream.close();

        assertEquals(extractedPageCount, pageNumbers.size());
    }

    @Test(expected = PdfServiceException.class)
    public void failPageExtraction() throws Exception
    {
        String filename = "multipage_document.pdf";
        List<Integer> pageNumbers = Arrays.asList(1, 2, 9); // the source document has 4 pages only
        assertNotNull(pdfService);

        FileSystemResource sourcePdf = new FileSystemResource(this.getClass().getResource("/pdfs/" + filename).getFile());
        assertTrue(sourcePdf.exists());

        InputStream sourceDocInputStream = sourcePdf.getInputStream();
        pdfService.extractPages(sourceDocInputStream, filename, pageNumbers);
    }

    @After
    public void tearDown() throws Exception
    {
        log.debug(tiffOutputFile.getAbsolutePath());
        FileUtils.deleteQuietly(tiffOutputFile);
    }
}
