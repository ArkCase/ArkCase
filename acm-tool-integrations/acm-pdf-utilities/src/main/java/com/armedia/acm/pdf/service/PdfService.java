package com.armedia.acm.pdf.service;

import com.armedia.acm.pdf.PdfServiceException;

import javax.xml.transform.Source;
import java.io.File;
import java.util.Map;

/**
 * PDF document generator built upon XSL-FO library (Apache FOP).
 * Created by Petar Ilin <petar.ilin@armedia.com> on 02.10.2015.
 */
public interface PdfService
{
    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters.
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile    XSL-FO stylesheet
     * @param source     XML data source required for XML transformation
     * @param parameters a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    String generatePdf(File xslFile, Source source, Map<String, String> parameters) throws PdfServiceException;


    /**
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters.
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFilename path to XSL-FO stylesheet
     * @param source      XML data source required for XML transformation
     * @param parameters  a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    String generatePdf(String xslFilename, Source source, Map<String, String> parameters) throws PdfServiceException;

    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source).
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile    XSL-FO stylesheet
     * @param parameters a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    String generatePdf(File xslFile, Map<String, String> parameters) throws PdfServiceException;


    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source).
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFilename path to XSL-FO stylesheet
     * @param parameters  a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    String generatePdf(String xslFilename, Map<String, String> parameters) throws PdfServiceException;
}
