package com.armedia.acm.snowbound.service.web;

import com.armedia.acm.snowbound.model.AcmDocument;
import com.armedia.acm.snowbound.utils.ArkCaseUtils;
import com.armedia.acm.snowbound.utils.HttpHeaderFactory;
import com.armedia.acm.snowbound.utils.WebUtils;

import com.snowbound.common.utils.ClientServerIO;
import com.snowbound.common.utils.Logger;
import com.snowbound.common.utils.URLReturnData;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by joseph.mcgrady on 8/30/2015.
 */
public class ArkCaseRestClient {

    private String acmTicket;
    private String baseURL;
    private String uploadNewFileService;
    private String sendFileService;
    private String retrieveFileService;

    private Logger log = Logger.getInstance();

    public ArkCaseRestClient(String baseURL, String uploadNewFileService, String sendFileService, String retrieveFileService, String acmTicket) {
        this.baseURL = baseURL;
        this.uploadNewFileService = uploadNewFileService;
        this.sendFileService = sendFileService;
        this.retrieveFileService = retrieveFileService;
        this.acmTicket = acmTicket;
    }

    /**
     * Downloads the specified document from the ArkCase download webscript
     * @param documentId - id number of the document which will be downloaded
     * @return document object containing the binary content and the name
     * @throws Exception if the download REST call fails
     */
    public AcmDocument getDocument(String documentId) throws Exception
    {
        log.log(Level.FINE, "Attempting to download document " + documentId + " from ArkCase");
        String completeURL = ArkCaseUtils.buildDownloadFileUrl(baseURL, retrieveFileService, documentId, acmTicket);
        log.log(Level.FINE, "complete url: " + completeURL);

        // Pulls the content of the document from the ArkCase server
        URLReturnData documentDownloadResponse = ClientServerIO.getURLBytes(completeURL);
        byte[] documentData = documentDownloadResponse.getData();
        log.log(Level.FINE, "pulled " + documentData.length + " document bytes from ArkCase");

        // Determines the document name from the ACM response if it exists, otherwise uses the id number as the name
        String documentName = extractFilenameFromResponseHeaders(documentDownloadResponse.getHeaderFields());
        if (documentName == null)
            documentName = documentId;

        return new AcmDocument(documentData, documentName);
    }

    /**
     * Uploads a new document to ArkCase via an HTTP REST call.  The new document
     * will be uploaded as a child of a particular folder in ArkCase.
     * @param documentData - binary data comprising the document to upload
     * @param fileMimeType - mimetype string of the document file to upload
     * @param fileName - name of the document file to upload
     * @param parentType - type of the ArkCase parent folder in which the new document will be uploaded
     * @param parentId - unique identifies of the ArkCase parent folder in which the new document will be uploaded
     * @return response message (should be JSON) containing the id of the newly created document and version information, etc.
     * @throws Exception if the call fails or returns a non success HTTP code (other than 200)
     */
    public String uploadDocumentToArkCase(byte[] documentData, String fileMimeType, String fileName, String parentType, String parentId) throws Exception
    {
        // Constructs the url to the ArkCase upload script which includes the authentication ticket and parent node information
        String acmUploadUrlString = ArkCaseUtils.buildUploadNewFileUrl(baseURL, uploadNewFileService, parentType, parentId, acmTicket);
        log.log(Level.FINE, "document upload target URL: " + acmUploadUrlString);

        // Uploads new document content to ArkCase, it will be persisted in the Alfresco repository
        return postDocumentToArkCase(acmUploadUrlString, documentData, fileMimeType, fileName);
    }

    /**
     * Replaces the specified document in ArkCase with new version.
     * @param documentData - binary data comprising the document to be uploaded
     * @param acmDocumentId - identifies the ArkCase document whose contents will be replaced
     * @param fileMimeType - mimetype string of the document file to replace
     * @param fileName - name of the document file to replace
     * @return response message (should be JSON) containing the id of the replaced document and version information, etc.
     * @throws Exception if the call fails or returns a non success HTTP code (other than 200)
     */
    public String replaceDocumentInArkCase(byte[] documentData, String acmDocumentId, String fileMimeType, String fileName) throws Exception
    {
        // Constructs the url to the ArkCase replace document script which includes the authentication ticket and the document whose contents will be overwritten
        String acmUploadUrlString = ArkCaseUtils.buildReplaceFileUrl(baseURL, sendFileService, acmDocumentId, acmTicket);
        log.log(Level.FINE, "document replace target URL: " + acmUploadUrlString);

        // Replaces an existing document in ArkCase with new data content
        return postDocumentToArkCase(acmUploadUrlString, documentData, fileMimeType, fileName);
    }

    private String postDocumentToArkCase(String acmUploadUrlString, byte[] documentData, String fileMimeType, String fileName) throws Exception
    {
        String acmResponse = null;
        HttpURLConnection acmConnection = null;
        try {
            // Creates an HTTP connection to the ArkCase upload webscript
            URL acmUploadUrl = new URL(acmUploadUrlString);
            acmConnection = (HttpURLConnection)acmUploadUrl.openConnection();
            if (acmConnection == null)
                throw new Exception("Cannot connect to ArkCase url: " + acmUploadUrlString);

            // Sets up a POST request for this connection
            acmConnection.setDoOutput(true);
            acmConnection.setDoInput(true);
            acmConnection.setRequestProperty("Accept", "application/json");
            acmConnection.setRequestMethod("POST");

            String CRLF = "\r\n"; // Line separator required by multipart/form-data.

            // The form boundary string tells the server where each POST body section begins
            String multipartFormBounary = WebUtils.createMultipartFormBoundary();
            acmConnection.setRequestProperty("Content-Type", HttpHeaderFactory.createMultipartFormContentTypeHeader(multipartFormBounary));

            OutputStream acmOutputStream = acmConnection.getOutputStream();
            if (acmOutputStream == null)
                throw new Exception("Cannot obtain an output stream for the connection to " + acmUploadUrlString);

            // Prepares the file section of the multipart POST body including the filename and mimetype metadata
            acmOutputStream.write(("--" + multipartFormBounary + CRLF).getBytes());
            acmOutputStream.write((HttpHeaderFactory.createFileContentDispositionHeader(fileName) + CRLF).getBytes());
            acmOutputStream.write((HttpHeaderFactory.createContentTypeHeader(fileMimeType)).getBytes());
            acmOutputStream.write((HttpHeaderFactory.createContentTransferEncodingHeader("binary") + CRLF).getBytes());
            acmOutputStream.write(CRLF.getBytes());
            acmOutputStream.flush();

            // Sends the actual document binary data to the ArkCase webscript
            acmOutputStream.write(documentData);
            acmOutputStream.flush();
            acmOutputStream.write(CRLF.getBytes());
            acmOutputStream.flush();

            // Tells the server that the multipart form is over
            acmOutputStream.write(("--" + multipartFormBounary + "--" + CRLF).getBytes());
            acmOutputStream.flush();

            // Determines if the request succeeded or failed with an HTTP error
            int responseCode = acmConnection.getResponseCode();
            log.log(Level.FINE, "request response (" + responseCode + "): " + acmConnection.getResponseMessage());
            if (responseCode != 200)
                throw new Exception("The call to ArkCase url " + acmUploadUrlString + " failed with HTTP error: " +
                                    responseCode + ", message: " + acmConnection.getResponseMessage());

            // Reads the response from the server (will be a JSON string containing the new file id of the uploaded content)
            acmResponse = ClientServerIO.readStringFromInputStream(acmConnection.getInputStream());

        } finally {
            if (acmConnection != null)
                acmConnection.disconnect();
        }

        return acmResponse;
    }

    /**
     * Attempts to read the document name from the download response from ArkCase.
     * @param responseHeaders - response headers from an ArkCase download document call
     * @return downloaded document filename, or null if it was not found
     */
    private static String extractFilenameFromResponseHeaders(Map responseHeaders)
    {
        String fileName = null;
        try
        {
            List contentDispositionValue = (List) responseHeaders.get("Content-Disposition");
            if (contentDispositionValue != null)
            {
                String contentDisposition = (String) contentDispositionValue.get(0);
                String[] dispositionTokens = contentDisposition.split(";");

                for (int i = 0; i < dispositionTokens.length; ++i)
                {
                    String dispositionToken = dispositionTokens[i];
                    if (dispositionToken.startsWith("filename="))
                    {
                        fileName = dispositionToken.split("=")[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }
}