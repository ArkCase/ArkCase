package com.armedia.acm.plugins.ecm.utils;

import org.apache.pdfbox.util.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by joseph.mcgrady on 9/14/2015.
 */
public class PDFUtils
{
    private static transient final Logger log = LoggerFactory.getLogger(PDFUtils.class);

    /**
     * Merges together the two supplied PDF documents
     *
     * @param originalFileStream - this PDF will be first in the combined document
     * @param newFileStream      - this PDF will be appended to the end of the original document
     */
    public static byte[] mergeFiles(InputStream originalFileStream, InputStream newFileStream)
    {
        byte[] mergedDocument = null;
        try
        {
            log.debug("Attempting to merge");

            // The original file data is first and the new file data is merged at the end (appended)
            PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
            pdfMergerUtility.addSource(originalFileStream);
            pdfMergerUtility.addSource(newFileStream);

            // Merges the documents together and creates an in-memory copy of the new combined document
            ByteArrayOutputStream outputStreamBytes = new ByteArrayOutputStream();
            pdfMergerUtility.setDestinationStream(outputStreamBytes);
            pdfMergerUtility.mergeDocuments();

            mergedDocument = outputStreamBytes.toByteArray();
            log.debug("merged length: " + mergedDocument.length);

        } catch (Exception e)
        {
            log.error("pdf merge failed: {}", e.getMessage(), e);
        }
        return mergedDocument;
    }
}