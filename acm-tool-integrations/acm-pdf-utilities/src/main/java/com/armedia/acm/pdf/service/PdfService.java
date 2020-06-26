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

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.transform.Source;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * PDF document generator built upon XSL-FO library (Apache FOP).
 * Created by Petar Ilin <petar.ilin@armedia.com> on 02.10.2015.
 */
public interface PdfService
{
    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters. NOTE: the caller is
     * responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile
     *            XSL-FO stylesheet
     * @param source
     *            XML data source required for XML transformation
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException
     *             on PDF creation error
     */
    String generatePdf(File xslFile, Source source) throws PdfServiceException;

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
    String generatePdf(InputStream xslStream, URI baseURI, Source source) throws PdfServiceException;

    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters.
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile
     *            XSL-FO stylesheet
     * @param source
     *            XML data source required for XML transformation
     * @param parameters
     *            a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException
     *             on PDF creation error
     */
    String generatePdf(File xslFile, Source source, Map<String, String> parameters) throws PdfServiceException;

    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters.
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFilename
     *            path to XSL-FO stylesheet
     * @param source
     *            XML data source required for XML transformation
     * @param parameters
     *            a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException
     *             on PDF creation error
     */
    String generatePdf(String xslFilename, Source source, Map<String, String> parameters) throws PdfServiceException;

    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source).
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile
     *            XSL-FO stylesheet
     * @param parameters
     *            a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException
     *             on PDF creation error
     */
    String generatePdf(File xslFile, Map<String, String> parameters) throws PdfServiceException;

    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source).
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFilename
     *            path to XSL-FO stylesheet
     * @param parameters
     *            a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException
     *             on PDF creation error
     */
    String generatePdf(String xslFilename, Map<String, String> parameters) throws PdfServiceException;

    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source) to the given
     * targetStream.
     * 
     * @param xslFile
     *            XSL-FO stylesheet
     * @param source
     *            XML data source required for XML transformation
     * @param targetStream
     *            the stream to write the generated file
     * @param parameters
     *            a key-value map of parameters to be replaced in the stylesheet
     * @throws PdfServiceException
     *             on PDF creation error
     */
    void generatePdf(File xslFile, Source source, OutputStream targetStream, Map<String, String> parameters) throws PdfServiceException;

    /**
     * Append one PDF file to another (incremental merge)
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
    PDDocument append(PDDocument pdDocument, InputStream is, PDFMergerUtility pdfMergerUtility) throws PdfServiceException;

    /**
     * Append one PDF file to another (incremental merge)
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
    PDDocument append(PDDocument pdDocument, String filename, PDFMergerUtility pdfMergerUtility) throws PdfServiceException;

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
    void generateTiffFromPdf(File inputPdf, File outputTiff) throws PdfServiceException;

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
    void addSource(PDFMergerUtility pdfMergerUtility, InputStream is) throws PdfServiceException;

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
    void addSource(PDFMergerUtility pdfMergerUtility, String filename) throws PdfServiceException;

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
    void mergeSources(PDFMergerUtility pdfMergerUtility, String filename) throws PdfServiceException;

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
    InputStream extractPages(InputStream is, String filename, List<Integer> pageNumbers) throws PdfServiceException;
}
