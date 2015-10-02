package com.armedia.acm.pdf.service;

import com.armedia.acm.pdf.PdfServiceException;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Random;

/**
 * PDF document generator built upon XSL-FO library (Apache FOP).
 * Created by Petar Ilin <petar.ilin@armedia.com> on 02.10.2015.
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
     * Generate PDF file based on XSL-FO stylesheet, XML data source and replacement parameters.
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile    XSL-FO stylesheet
     * @param source     XML data source required for XML transformation
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

            for (Map.Entry<String, String> parameter : parameters.entrySet())
            {
                String name = parameter.getKey();
                // transformation fails on null values
                String value = (parameter.getValue() != null) ? parameter.getValue() : "N/A";
                transformer.setParameter(name, value);
            }

            OutputStream os = null;
            try
            {
                os = new BufferedOutputStream(new FileOutputStream(filename));
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, os);
                Result result = new SAXResult(fop.getDefaultHandler());
                transformer.transform(source, result);
            } finally
            {
                if (os != null)
                {
                    os.close();
                }
            }
        } catch (FOPException | TransformerException | IOException e)
        {
            log.error("Unable to generate PDF document", e);
            throw new PdfServiceException(e);
        }
        return filename;
    }

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
    @Override
    public String generatePdf(String xslFilename, Source source, Map<String, String> parameters) throws PdfServiceException
    {
        File xslFile = new File(xslFilename);
        return generatePdf(xslFile, source, parameters);
    }

    /**
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source).
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFile    XSL-FO stylesheet
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
     * Generate PDF file based on XSL-FO stylesheet and replacement parameters (no XML data source).
     * NOTE: the caller is responsible for deleting the file afterwards (not to leave mess behind)
     *
     * @param xslFilename path to XSL-FO stylesheet
     * @param parameters  a key-value map of parameters to be replaced in the stylesheet
     * @return path to the newly generated PDF file (random filename stored in temp folder)
     * @throws PdfServiceException on PDF creation error
     */
    @Override
    public String generatePdf(String xslFilename, Map<String, String> parameters) throws PdfServiceException
    {
        Source source = new DOMSource();
        return generatePdf(xslFilename, source, parameters);
    }
}
