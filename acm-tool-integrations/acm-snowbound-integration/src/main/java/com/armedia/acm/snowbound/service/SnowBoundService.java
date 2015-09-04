package com.armedia.acm.snowbound.service;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Snow.SNBD_SEARCH_RESULT;
import Snow.Snowbnd;
import Snow.FormatHash;
import Snow.Format;

/**
 * Created by joseph.mcgrady on 8/30/2015.
 */
public class SnowBoundService {

    private Snowbnd snow;
    private int[] snowError;

    public SnowBoundService() {
        this.snowError = new int[1];
        this.snow = new Snowbnd();
    }
    public SnowBoundService(Snowbnd snow) {
        this.snowError = new int[1];
        this.snow = snow;
    }

    /**
     * Returns the total number of pages for a multi-page document (PDF, TIFF)
     * @param documentBytes - contains the binary data comprising the document
     * @return number of pages in the document
     */
    public int getNumberOfPages(byte[] documentBytes) {
        return snow.IMGLOW_get_pages(convertByteArrayToDataStream(documentBytes));
    }

    /**
     * Returns the SnowBound file type code for the given document
     * @param documentBytes - contains the binary data comprising the document
     * @return integer code for the file type of the document
     */
    public int getDocumentType(byte[] documentBytes) {
        return snow.IMGLOW_get_filetype(convertByteArrayToDataStream(documentBytes));
    }

    /**
     * Returns a snowbound Format object from which the mimetype
     * and file extension can be obtained
     * @param documentBytes - contains the binary data comprising the document
     * @return snowbound Format object which includes the extension/mimetype
     */
    public Format getDocumentFormat(byte[] documentBytes) {
        int formatCode = getDocumentType(documentBytes);
        return FormatHash.getInstance().getFormat(formatCode);
    }

    /**
     * Builds a list of the individual pages making up a document
     * @param documentBytes - contains the binary data comprising the document
     * @return List of byte arrays containing the binary data for each separate document page
     */
    public List<byte[]> getPages(byte[] documentBytes) {
        List<byte[]> pageList = new ArrayList<byte[]>();
        int numPages = getNumberOfPages(documentBytes);
        for (int i = 0; i < numPages; i++) {
            pageList.add(snow.IMGLOW_extract_page(convertByteArrayToDataStream(documentBytes), i, snowError));
        }
        return pageList;
    }

    /**
     * Wraps a byte array in a data input stream as required by many SnowBound API calls
     * @param byteArray - raw byte array which will be wrapped
     * @return data input stream wrapper around the supplied byte array
     */
    public static DataInputStream convertByteArrayToDataStream(byte[] byteArray) {
        return new DataInputStream(new ByteArrayInputStream(byteArray));
    }

    /**
     * Divides the given document into any number of sub documents
     * @param documentData - contains the binary data comprising the document
     * @param splitIndexes - array of page indexes at which the document will be split into sub documents
     * @return List of byte arrays containing the binary data of each split document
     */
    public List<byte[]> splitDocument(byte[] documentData, int[] splitIndexes) {
        List<byte[]> splitDocuments = new ArrayList<byte[]>();
        if (documentData != null && splitIndexes != null && splitIndexes.length > 0) {
            List<byte[]> pages = getPages(documentData);

            // This code requires that the split page indexes are sorted in ascending order
            Arrays.sort(splitIndexes);

            // Splits the document into sub documents from the page indexes in the document page range (0 - numpages)
            int startIndex = 0;
            int lastIndexMerged = 0;
            for (int i = 0; i < splitIndexes.length; i++) {
                int endIndex = splitIndexes[i];
                if (endIndex > 0 && endIndex <= pages.size()) { // is index within the valid page range?
                    splitDocuments.add(mergePages(pages.subList(startIndex, endIndex)));
                    startIndex = endIndex;
                    lastIndexMerged = endIndex;
                } else if (endIndex > pages.size()) { // indexes beyond this point are out of range, since the index array is sorted we can stop checking
                    break;
                }
            }

            // creates final document containing last set of pages if they haven't already been included in another split document
            if (lastIndexMerged < pages.size()) {
                splitDocuments.add(mergePages(pages.subList(startIndex, pages.size())));
            }
        }
        return splitDocuments;
    }

    /**
     * Divides the given document into two sub documents
     * @param documentData - contains the binary data comprising the document
     * @param splitIndex - the document will be divided at this index position
     * @return List of byte arrays containing the binary data of each split document
     */
    public List<byte[]> splitDocument(byte[] documentData, int splitIndex) {
        List<byte[]> splitDocuments = new ArrayList<byte[]>();
        if (documentData != null) {
            List<byte[]> pages = getPages(documentData);
            if (splitIndex <= 0 || splitIndex >= pages.size()) { // split index is out of range, just return the existing document as is
                splitDocuments.add(documentData);
            } else { // Splits the document into two documents at the specified index
                List<byte[]> l1 = pages.subList(0, splitIndex);
                List<byte[]> l2 = pages.subList(splitIndex, pages.size());
                splitDocuments.add(mergePages(l1));
                splitDocuments.add(mergePages(l2));
            }
        }
        return splitDocuments;
    }

    /**
     * Merges the given documents into one combined document with all of the pages
     * @param documentList - contains the binary data for each document, which can be multi-page documents
     * @return binary data of the merged document
     */
    public byte[] mergeDocuments(List<byte[]> documentList) {
        byte[] mergedDocument = null;
        if (documentList != null && documentList.size() > 0) {

            // Builds one combined list with the pages of all the documents
            List<byte[]> totalPageList = new ArrayList<byte[]>();
            for (byte[] document : documentList) {
                List<byte[]> pages = getPages(document);
                for (byte[] page : pages) {
                    totalPageList.add(page);
                }
            }

            // Merges the pages of the separate documents into one document
            mergedDocument = mergePages(totalPageList);
        }
        return mergedDocument;
    }

    /**
     * Merges the given pages into a combined document
     * @param pageList - contains the binary data for each individual page
     * @return binary data of the merged document
     */
    public byte[] mergePages(List<byte[]> pageList) {
        byte[] mergedDocument = null;
        if (pageList != null && pageList.size() > 0) {
            if (pageList.size() == 1) { // no merge action is required, there is only one page
                mergedDocument = pageList.get(0);
            } else {
                int fileType = getDocumentType(pageList.get(0));
                DataInputStream mergeDataStream = convertByteArrayToDataStream(pageList.get(0));
                for (int i = 0; i + 1 < pageList.size(); i++) { // Appends each page to the total document
                    mergedDocument = snow.IMGLOW_append_page(mergeDataStream, pageList.get(i + 1), fileType, snowError);
                    mergeDataStream = new DataInputStream(new ByteArrayInputStream(mergedDocument));
                }
            }
        }
        return mergedDocument;
    }

    /**
     * Reorders the supplied list of document pages according to the given page index list
     * and builds a new document with all the pages merged together in the desired order
     * @param pageList List of the individual document pages
     * @param newPageOrder List of the new reordered indexes for each page
     * @return byte array containing the new reordered document
     */
    public byte[] reorderPages(List<byte[]> pageList, List<Integer> newPageOrder) throws Exception {
        byte[] mergedDocument = null;

        if (pageList == null || pageList.size() == 0)
            throw new Exception("The document page list must be provided");
        if (newPageOrder == null || (newPageOrder.size() != pageList.size()))
            throw new Exception("The reordering page index list must be supplied and have the same size as the page list");

        // Obtains the index of the first page to append to the reordered document
        int reorderIndex = newPageOrder.get(0);
        if (reorderIndex < 0 || reorderIndex >= pageList.size())
            throw new Exception("The reorder index was out of bounds. The pages list length is " + pageList.size() + " and the reorder index is " + reorderIndex);

        DataInputStream mergeDataStream = new DataInputStream(new ByteArrayInputStream(pageList.get(reorderIndex)));
        int fileType = getDocumentType(pageList.get(reorderIndex));
        for (int i = 0; i < pageList.size() - 1; i++) {

            // Obtains the index of the next page to append to the reordered document
            reorderIndex = newPageOrder.get(i + 1);
            if (reorderIndex < 0 || reorderIndex >= pageList.size())
                throw new Exception("The reorder index was out of bounds. The pages list length is " + pageList.size() + " and the reorder index is " + reorderIndex);

            // Appends the next page in the reordered sequence to the new document
            mergedDocument = snow.IMGLOW_append_page(mergeDataStream, pageList.get(reorderIndex), fileType, snowError);
            mergeDataStream = new DataInputStream(new ByteArrayInputStream(mergedDocument));
        }
        return mergedDocument;
    }

    /**
     * Retrieves the text characters from a page (if it has text like searchable PDF)
     * as well as the coordinates where each character is located
     * @param documentStream contains the binary data comprising the document
     * @param pageToExtract index of the document page from which the text information will be extracted
     * @return byte array containing the characters and coordinates in a SnowBound specific format (not human readable)
     */
    public byte[] extractPageTextAndCoordinates(DataInputStream documentStream, int pageToExtract) {
        int[] bufferLength = new int[] { 5000 };
        return snow.IMGLOW_extract_text(documentStream, bufferLength, snowError, pageToExtract);
    }

    /**
     * Performs a text search based on the supplied search term and
     * returns an array of bounding rectangles surrounding each match
     * @param documentText output from the SnowBound extract text API call with the characters and positions in a specialized format
     * @param searchTerm String which will be searched
     * @return Array containing bounding rectangles of each search match
     */
    public SNBD_SEARCH_RESULT[] searchText(byte[] documentText, String searchTerm) {
        return snow.IMGLOW_search_text(documentText, searchTerm, 0, snowError);
    }

    public Snowbnd getSnowbnd() {
        return snow;
    }
    public void setSnowbnd(Snowbnd snow) {
        this.snow = snow;
    }

    /**
     * Obtains the last SnowBound error code that was generated
     * during the previous failed operation
     * @return integer code describing a SnowBound error
     */
    public int getSnowBoundErrorCode() {
        return snowError[0];
    }
}