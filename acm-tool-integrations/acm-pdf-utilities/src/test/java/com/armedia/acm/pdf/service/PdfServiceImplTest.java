package com.armedia.acm.pdf.service;

import com.armedia.acm.pdf.PdfServiceException;
import org.apache.commons.io.FileUtils;
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
import java.io.IOException;

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

    private String outputFileName = "multipageImage.tif";
    private File outputFile = new File(System.getProperty("java.io.tmpdir") + File.separator + outputFileName);

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


    @After
    public void tearDown() throws Exception
    {
        log.debug(outputFile.getAbsolutePath());
        FileUtils.deleteQuietly(outputFile);
    }
}