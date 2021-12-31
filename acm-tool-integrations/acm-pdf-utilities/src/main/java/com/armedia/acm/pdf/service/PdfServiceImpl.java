package com.armedia.acm.pdf.service;

/*-
 * #%L
 * Tool Integrations: PDF creation and manipulation utilities
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

import com.armedia.acm.pdf.PdfServiceException;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi;
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * PDF document generator built upon XSL-FO library (Apache FOP). Created by Petar Ilin <petar.ilin@armedia.com> on
 * 02.10.2015.
 */
public class PdfServiceImpl implements PdfService
{
    /**
     * Megabyte in bytes.
     */
    public static final long MEGABYTE = 1024 * 1024;
    /**
     * Use no more than 32MB of main memory when merging PDFs, the disk is used for the rest.
     */
    public static final int MAX_MAIN_MEMORY_BYTES = 1024 * 1024 * 32;
    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());
    /**
     * Random number generator.
     */
    private Random random = new Random();

    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters. NOTE: the caller is
     * responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslStream
     *            XSL-FO stylesheet
     * @param baseURI
     *            base URI where the FOP engine will be resolving URIs against
     * @param source
     *            XML data source required for XML transformation
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException
     *             on PDF creation error
     */
    public String generatePdf(InputStream xslStream, URI baseURI, Source source) throws PdfServiceException
    {
        // create a temporary file name
        String filename = String.format("%s/acm-%s.pdf", System.getProperty("java.io.tmpdir"), UUID.randomUUID());
        log.debug("PDF creation: using [{}] as temporary file name", filename);
        try
        {
            log.debug("Using [{}] as FOP base URI", baseURI);
            FopFactory fopFactory = FopFactory.newInstance(baseURI);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            /**
             * com.sun.org.apache.xalan.internal.xsltc.trax - JDK
             * org.apache.xalan.processor - Xalan
             * org.apache.xalan.xsltc.trax - Xalan
             * 
             * those are TransformerFactory implementation providers and not sure which implementation doesn't support below XMLConstants 
             * that's why suppressing IllegalArgumentException.
             */
         
            try
            {
                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
                transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            }
            catch (IllegalArgumentException e)
            {
                // TODO: handle exception
            }
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslStream));

            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filename)))
            {
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, os);
                Result result = new SAXResult(fop.getDefaultHandler());
                transformer.transform(source, result);
            }

        }
        catch (FOPException | TransformerException | IOException e)
        {
            log.error("Unable to generate PDF document", e);
            throw new PdfServiceException(e);
        }
        return filename;
    }

    @Override
    public String generatePdf(InputStream xslStream, URI baseURI, Map<String, String> parameters) throws PdfServiceException
    {
        // create a temporary file name
        String filename = String.format("%s/acm-%020d.pdf", System.getProperty("java.io.tmpdir"), Math.abs(random.nextLong()));
        log.debug("PDF creation: using [{}] as temporary file name", filename);
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filename)))
        {
            generatePdf(xslStream, baseURI, os, parameters);
        }
        catch (IOException e)
        {
            log.error("Unable to generate PDF document", e);
            throw new PdfServiceException(e);
        }
        return filename;
    }

    @Override
    public void generatePdf(InputStream xslStream, URI baseURI, OutputStream targetStream, Map<String, String> parameters) throws PdfServiceException
    {
        try
        {
            log.debug("Using [{}] as FOP base URI", baseURI);
            FopFactory fopFactory = FopFactory.newInstance(baseURI);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslStream));

            parameters.forEach((name, value) -> transformer.setParameter(name, value != null ? value : "N/A"));

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, targetStream);
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(new DOMSource(), result);
        }
        catch (FOPException | TransformerException e)
        {
            log.error("Unable to generate PDF document", e);
            throw new PdfServiceException(e);
        }

    }

    /**
     * Append one PDF file to another (incremental merge).
     *
     * @param pdDocument
     *            source PDF, the document we are appending to
     * @param is
     *            input stream of the document we are appending
     * @param pdfMergerUtility
     *            PDF merger utility
     * @return merged document
     * @throws PdfServiceException
     *             on error while merging
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
            }
            else
            {
                PDDocument next = PDDocument.load(is);
                pdfMergerUtility.appendDocument(pdDocument, next);
                next.close();
            }
        }
        catch (IOException e)
        {
            log.error("Unable to generate PDF document", e);
            throw new PdfServiceException(e);
        }
        return pdDocument;
    }

    /**
     * Append one PDF file to another (incremental merge).
     *
     * @param pdDocument
     *            source PDF, the document we are appending to
     * @param filename
     *            path to the document we are appending
     * @param pdfMergerUtility
     *            PDF merger utility
     * @return merged document
     * @throws PdfServiceException
     *             on error while merging
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
        }
        catch (FileNotFoundException e)
        {
            throw new PdfServiceException(e);
        }
        return append(pdDocument, is, pdfMergerUtility);
    }

    /**
     * Generates multipage TIFF from PDF file.
     *
     * @param inputPdf
     *            pdf file to be processed
     * @param outputTiff
     *            location where generated TIFF to be saved
     * @throws PdfServiceException
     *             on error generating TIFF
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
                }
                catch (UnsupportedOperationException e)
                {
                    log.warn("Not supported compression:", e);
                    writer.writeToSequence(image, null);
                }
                log.debug("Successfully written one image to the sequence, {} more to go.", document.getNumberOfPages() - page);
                page++;
            }
            ios.flush();
        }
        catch (IOException e)
        {
            throw new PdfServiceException(e);
        }
        log.debug("Successfully written tiff sequence into file {}. With length: {} MBytes", outputTiff.getPath(),
                outputTiff.length() / MEGABYTE);
    }

    /**
     * Add source for merging into single PDF document.
     *
     * @param pdfMergerUtility
     *            PDF merger utility
     * @param is
     *            input stream of the document we are appending
     * @throws PdfServiceException
     *             on error adding source stream
     */
    @Override
    public void addSource(PDFMergerUtility pdfMergerUtility, InputStream is) throws PdfServiceException
    {
        pdfMergerUtility.addSource(is);
    }

    /**
     * Add source for merging into single PDF document.
     *
     * @param pdfMergerUtility
     *            PDF merger utility
     * @param filename
     *            path to the document we are appending
     * @throws PdfServiceException
     *             on error adding source file
     */
    @Override
    public void addSource(PDFMergerUtility pdfMergerUtility, String filename) throws PdfServiceException
    {
        InputStream is = null;
        try
        {
            log.debug("About to add PDF document [{}] for merging", filename);
            is = new FileInputStream(filename);
        }
        catch (FileNotFoundException e)
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
     * @param pdfMergerUtility
     *            PDF merger utility
     * @param filename
     *            path to the merged document
     * @throws PdfServiceException
     *             on error creating merged document
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
        }
        catch (IOException e)
        {
            log.error("Unable to merge multiple PDF documents to [{}]", filename, e);
            throw new PdfServiceException(e);
        }
        log.debug("Multiple PDF documents successfully merged to [{}]", filename);
    }

    /**
     * Merge multiple sources into single PDF document.
     *
     * @param pdfMergerUtility
     *            PDF merger utility
     * @param fos
     *            output stream
     * @throws PdfServiceException
     *             on error creating merged document
     */
    @Override
    public void mergeSources(PDFMergerUtility pdfMergerUtility, FileOutputStream fos) throws PdfServiceException
    {
        // using at most 32MB memory, the rest goes to disk
        MemoryUsageSetting memoryUsageSetting = MemoryUsageSetting.setupMixed(MAX_MAIN_MEMORY_BYTES);
        try
        {
            log.debug("About to merge multiple PDF documents to a file output stream");
            pdfMergerUtility.setDestinationStream(fos);
            pdfMergerUtility.mergeDocuments(memoryUsageSetting);
        }
        catch (IOException e)
        {
            log.error("Unable to merge multiple PDF documents to a file output stream", e);
            throw new PdfServiceException(e);
        }
        log.debug("Multiple PDF documents successfully merged to a file output stream");
    }

    /**
     * Create new document out of extracted pages from another document.
     *
     * @param is
     *            input stream of the source document
     * @param filename
     *            source document filename
     * @param pageNumbers
     *            list of page numbers (1-based) to be extracted from source
     * @return new document stream
     * @throws PdfServiceException
     *             on error creating extracted document
     */
    @Override
    public InputStream extractPages(InputStream is, String filename, List<Integer> pageNumbers) throws PdfServiceException
    {
        try (PDDocument extractedDocument = new PDDocument(); PDDocument sourceDocument = PDDocument.load(is))
        {
            extractedDocument.setDocumentInformation(sourceDocument.getDocumentInformation());
            extractedDocument.getDocumentCatalog().setViewerPreferences(sourceDocument.getDocumentCatalog().getViewerPreferences());

            for (Integer pageNumber : pageNumbers)
            {
                PDPage page = sourceDocument.getPage(pageNumber - 1);
                PDPage imported = extractedDocument.importPage(page);
                imported.setCropBox(page.getCropBox());
                imported.setMediaBox(page.getMediaBox());
                imported.setResources(page.getResources());
                imported.setRotation(page.getRotation());
            }
            log.debug("Successfully extracted pages from [{}]", filename);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            extractedDocument.save(baos);
            // return input stream of newly generated document
            return new ByteArrayInputStream(baos.toByteArray());
        }
        catch (IOException | IndexOutOfBoundsException e)
        {
            log.error("Unable to extract pages from [{}]", filename, e);
            throw new PdfServiceException(e);
        }
    }

    /**
     *
     * Replaces the bottom of each page of the document with a rectangle box that contains the page numbers and adds the
     * fiscal year
     *
     * @param document
     *            source document
     * @param fiscalYear
     *            the fiscal year for the document
     * @return document with numbering
     */
    @Override
    public PDDocument replacePageNumbersAndAddFiscalYear(PDDocument document, int fiscalYear) throws IOException
    {
        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 8.0f;

        for (int i = 0; i < document.getNumberOfPages(); i++)
        {
            PDPage page = document.getPage(i);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true,
                    true))
            {
                // Adds a blank rectangle to bottom of the page to replace text
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.addRect(550, 670, 100, 100);
                contentStream.fill();

                PDRectangle pageSize = document.getPage(i).getMediaBox();
                String pageNumberingString = String.format("Page %d of %d", i + 1, document.getNumberOfPages());
                contentStream.setLeading(14.5f);

                float stringWidth = font.getStringWidth(pageNumberingString) * fontSize / 1000f;
                // calculate to center of the page
                int rotation = document.getPage(i).getRotation();
                boolean rotate = rotation == 90 || rotation == 270;
                float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
                float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
                float centerX = rotate ? pageHeight / 1.05f : (pageWidth - stringWidth) / 2f;
                float centerY = rotate ? (pageWidth - stringWidth) / 2f : pageHeight / 2f;

                // append the content to the existing stream

                contentStream.beginText();
                // set font and font size
                contentStream.setFont(font, fontSize);
                // set text color
                contentStream.setNonStrokingColor(0, 0, 0);
                if (rotate)
                {
                    // rotate the text according to the page rotation
                    contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, centerX, centerY));
                }
                else
                {
                    contentStream.setTextMatrix(Matrix.getTranslateInstance(centerX, centerY));
                }
                contentStream.showText(pageNumberingString);

                String fiscalYearString = "Fiscal Year: " + fiscalYear;
                float fiscalYearStringWidth = font.getStringWidth(fiscalYearString) * fontSize / 1000f;

                contentStream.newLineAtOffset((stringWidth - fiscalYearStringWidth) / 2, -14.5f);
                contentStream.showText(fiscalYearString);

                contentStream.endText();
            }
        }

        return document;
    }
}
