package com.armedia.acm.pdf.service;

import com.armedia.acm.pdf.PdfServiceException;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi;
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * PDF document generator built upon XSL-FO library (Apache FOP). Created by Petar Ilin <petar.ilin@armedia.com> on
 * 02.10.2015.
 */
public class PdfServiceImpl implements PdfService
{
    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Random number generator.
     */
    private Random random = new Random();

    /**
     * Megabyte in bytes.
     */
    public static final long MEGABYTE = 1024 * 1024;

    /**
     * Use no more than 32MB of main memory when merging PDFs, the disk is used for the rest.
     */
    public static final int MAX_MAIN_MEMORY_BYTES = 1024 * 1024 * 32;

    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters. NOTE: the caller is
     * responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile XSL-FO stylesheet
     * @param source XML data source required for XML transformation
     * @param parameters a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    @Override
    public String generatePdf(File xslFile, Source source, Map<String, String> parameters) throws PdfServiceException
    {
        // create a temporary file name
        String filename = String.format("%s/acm-%020d.pdf", System.getProperty("java.io.tmpdir"), Math.abs(random.nextLong()));
        log.debug("PDF creation: using [{}] as temporary file name", filename);
        try
        {
            // Base URI is where the FOP engine will be resolving URIs against
            URI baseURI = xslFile.getParentFile().toURI();
            log.debug("Using [{}] as FOP base URI", baseURI);
            FopFactory fopFactory = FopFactory.newInstance(baseURI);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslFile));

            parameters.forEach((name, value) -> transformer.setParameter(name, value != null ? value : "N/A"));

            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filename)))
            {
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, os);
                Result result = new SAXResult(fop.getDefaultHandler());
                transformer.transform(source, result);
            }

        } catch (FOPException | TransformerException | IOException e)
        {
            log.error("Unable to generate PDF document", e);
            throw new PdfServiceException(e);
        }
        return filename;
    }

    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters. NOTE: the caller is
     * responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFilename path to XSL-FO stylesheet
     * @param source XML data source required for XML transformation
     * @param parameters a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    @Override
    public String generatePdf(String xslFilename, Source source, Map<String, String> parameters) throws PdfServiceException
    {
        File xslFile = new File(xslFilename);
        return generatePdf(xslFile, source, parameters);
    }

    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source). NOTE: the caller is
     * responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile XSL-FO stylesheet
     * @param parameters a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    @Override
    public String generatePdf(File xslFile, Map<String, String> parameters) throws PdfServiceException
    {
        Source source = new DOMSource();
        return generatePdf(xslFile, source, parameters);
    }

    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source). NOTE: the caller is
     * responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFilename path to XSL-FO stylesheet
     * @param parameters a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    @Override
    public String generatePdf(String xslFilename, Map<String, String> parameters) throws PdfServiceException
    {
        Source source = new DOMSource();
        return generatePdf(xslFilename, source, parameters);
    }

    /**
     * Append one PDF file to another (incremental merge).
     *
     * @param pdDocument source PDF, the document we are appending to
     * @param is input stream of the document we are appending
     * @param pdfMergerUtility PDF merger utility
     * @return merged document
     * @throws PdfServiceException on error while merging
     * @deprecated use {@link #addSource(PDFMergerUtility, InputStream)} and
     *             {@link #mergeSources(PDFMergerUtility, String)}
     */
    @Deprecated
    @Override
    public PDDocument append(PDDocument pdDocument, InputStream is, PDFMergerUtility pdfMergerUtility) throws PdfServiceException
    {
        try
        {
            if (pdDocument == null) // first document
            {
                pdDocument = PDDocument.load(is);
            } else
            {
                PDDocument next = PDDocument.load(is);
                pdfMergerUtility.appendDocument(pdDocument, next);
                next.close();
            }
        } catch (IOException e)
        {
            log.error("Unable to generate PDF document", e);
            throw new PdfServiceException(e);
        }
        return pdDocument;
    }

    /**
     * Append one PDF file to another (incremental merge).
     *
     * @param pdDocument source PDF, the document we are appending to
     * @param filename path to the document we are appending
     * @param pdfMergerUtility PDF merger utility
     * @return merged document
     * @throws PdfServiceException on error while merging
     * @deprecated use {@link #addSource(PDFMergerUtility, String)} and {@link #mergeSources(PDFMergerUtility, String)}
     */
    @Deprecated
    @Override
    public PDDocument append(PDDocument pdDocument, String filename, PDFMergerUtility pdfMergerUtility) throws PdfServiceException
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream(filename);
        } catch (FileNotFoundException e)
        {
            throw new PdfServiceException(e);
        }
        return append(pdDocument, is, pdfMergerUtility);
    }

    /**
     * Generates multipage TIFF from PDF file.
     *
     * @param inputPdf pdf file to be processed
     * @param outputTiff location where generated TIFF to be saved
     * @throws PdfServiceException on error generating TIFF
     */
    @Override
    public void generateTiffFromPdf(File inputPdf, File outputTiff) throws PdfServiceException
    {
        if (!inputPdf.exists())
        {
            throw new PdfServiceException(inputPdf.getAbsolutePath() + " doesn't exists");
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputTiff); PDDocument document = PDDocument.load(inputPdf))
        {
            log.debug("Reading pdf from file path {}", outputTiff.getPath());

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            TIFFImageWriterSpi tiffspi = new TIFFImageWriterSpi();
            ImageWriter writer = tiffspi.createWriterInstance();
            log.debug("Preparing to generate multi image tiff.");
            writer.setOutput(ios);
            writer.prepareWriteSequence(null);
            log.debug("Pdf contains {} pages.", document.getNumberOfPages());
            int page = 0;

            TIFFImageWriteParam writeParam = new TIFFImageWriteParam(Locale.getDefault());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionType("CCITT T.6"); // Sharecare requires CCITT Group 4 Fax Encoding

            // full quality! could be: from 0.1f to 1.0f
            writeParam.setCompressionQuality(0.75f);

            for (PDPage pdPage : document.getPages())
            {
                log.debug("Rendering PDF page [{}/{}] as TIFF image", page, document.getNumberOfPages());
                // using 200dpi b/w since Sharecare uses that resolution
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 200, ImageType.BINARY);
                IIOImage image = new IIOImage(bim, null, null);
                try
                {
                    writer.writeToSequence(image, writeParam);
                } catch (UnsupportedOperationException e)
                {
                    log.warn("Not supported compression:", e);
                    writer.writeToSequence(image, null);
                }
                log.debug("Successfully written one image to the sequence, {} more to go.", document.getNumberOfPages() - page);
                page++;
            }
            ios.flush();
        } catch (IOException e)
        {
            throw new PdfServiceException(e);
        }
        log.debug("Successfully written tiff sequence into file {}. With length: {} MBytes", outputTiff.getPath(),
                outputTiff.length() / MEGABYTE);
    }

    /**
     * Add source for merging into single PDF document.
     *
     * @param pdfMergerUtility PDF merger utility
     * @param is input stream of the document we are appending
     * @throws PdfServiceException on error adding source stream
     */
    @Override
    public void addSource(PDFMergerUtility pdfMergerUtility, InputStream is) throws PdfServiceException
    {
        pdfMergerUtility.addSource(is);
    }

    /**
     * Add source for merging into single PDF document.
     *
     * @param pdfMergerUtility PDF merger utility
     * @param filename path to the document we are appending
     * @throws PdfServiceException on error adding source file
     */
    @Override
    public void addSource(PDFMergerUtility pdfMergerUtility, String filename) throws PdfServiceException
    {
        InputStream is = null;
        try
        {
            log.debug("About to add PDF document [{}] for merging", filename);
            is = new FileInputStream(filename);
        } catch (FileNotFoundException e)
        {
            log.error("Unable to add PDF document [{}] for merging", filename, e);
            throw new PdfServiceException(e);
        }
        addSource(pdfMergerUtility, is);
        log.debug("PDF document successfully added [{}] for merging", filename);
    }

    /**
     * Merge multiple sources into single PDF document.
     *
     * @param pdfMergerUtility PDF merger utility
     * @param filename path to the merged document
     * @throws PdfServiceException on error creating merged document
     */
    @Override
    public void mergeSources(PDFMergerUtility pdfMergerUtility, String filename) throws PdfServiceException
    {
        // using at most 32MB memory, the rest goes to disk
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(MAX_MAIN_MEMORY_BYTES);
        try
        {
            log.debug("About to merge multiple PDF documents to [{}]", filename);
            pdfMergerUtility.setDestinationFileName(filename);
            pdfMergerUtility.mergeDocuments(memoryUsageSetting);
        } catch (IOException e)
        {
            log.error("Unable to merge multiple PDF documents to [{}]", filename, e);
            throw new PdfServiceException(e);
        }
        log.debug("Multiple PDF documents successfully merged to [{}]", filename);
    }
}
