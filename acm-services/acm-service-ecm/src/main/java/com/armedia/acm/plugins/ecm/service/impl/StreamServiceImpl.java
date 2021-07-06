package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.Range;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.StreamService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.web.api.MDCConstants;

import com.google.json.JsonSanitizer;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.collections.map.HashedMap;
import org.owasp.encoder.Encode;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 6/6/2017.
 */
public class StreamServiceImpl implements StreamService
{
    private final long DEFAULT_EXPIRE_TIME = 604800000L;
    private final int DEFAULT_BUFFER_SIZE = 10240;
    private final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    private CmisConfigUtils cmisConfigUtils;
    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmFileService ecmFileService;
    private CamelContextManager camelContextManager;

    /**
     * Close the given resource.
     *
     * @param resource
     *            The resource to be closed.
     */
    private static void close(Closeable resource)
    {
        if (resource != null)
        {
            try
            {
                resource.close();
            }
            catch (IOException ignore)
            {
                // Do nothing. Silent close.
            }
        }
    }

    @Override
    public void stream(Long id, String version, HttpServletRequest request, HttpServletResponse response)
            throws AcmObjectNotFoundException, IOException
    {
        EcmFile file = getEcmFileService().findById(id);

        if (file == null)
        {
            throw new AcmObjectNotFoundException(null, null, "File not found", null);
        }

        String cmisFileId = getFolderAndFilesUtils().getVersionCmisId(file, version);
        Map<String, Object> messageProps = new HashedMap();
        messageProps.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        messageProps.put(ArkCaseCMISConstants.CMIS_OBJECT_ID, cmisFileId);

        CmisObject downloadedFile = null;
        try
        {
            downloadedFile = (CmisObject) getCamelContextManager().send(ArkCaseCMISActions.GET_OBJECT_BY_ID, messageProps);
        }
        catch (ArkCaseFileRepositoryException e)
        {
            throw new AcmObjectNotFoundException(null, file.getId(), "Exception while downloading object by id", null);
        }

        if (downloadedFile == null || !(downloadedFile instanceof Document))
        {
            throw new AcmObjectNotFoundException(null, null, "File not found", null);
        }

        stream((Document) downloadedFile, file, version, request, response);
    }

    @Override
    public void stream(Document payload, EcmFile file, String version, HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {

        if (payload == null || payload.getContentStream() == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        OutputStream output = null;
        try (InputStream input = payload.getContentStream().getStream())
        {
            long total = payload.getContentStreamLength();

            // Prepare some variables. The ETag is an unique identifier of the file.
            String fileName = payload.getContentStreamFileName();
            long lastModified = payload.getLastModificationDate().getTime().getTime();
            String eTag = fileName + "_" + total + "_" + lastModified;
            long expires = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME;

            // If-None-Match header should contain "*" or ETag. If so, then return 304.
            String ifNoneMatch = request.getHeader("If-None-Match");
            if (ifNoneMatch != null && matches(ifNoneMatch, eTag))
            {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader("ETag", eTag); // Required in 304.
                response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
                return;
            }

            // If-Modified-Since header should be greater than LastModified. If so, then return 304.
            // This header is ignored if any If-None-Match header is specified.
            long ifModifiedSince = request.getDateHeader("If-Modified-Since");
            if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified)
            {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader("ETag", eTag); // Required in 304.
                response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
                return;
            }

            // If-Match header should contain "*" or ETag. If not, then return 412.
            String ifMatch = request.getHeader("If-Match");
            if (ifMatch != null && !matches(ifMatch, eTag))
            {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }

            // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
            long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
            if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified)
            {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }

            // Prepare some variables. The full Range represents the complete file.
            Range full = new Range(0, total - 1, total);
            List<Range> ranges = new ArrayList<>();

            // Validate and process Range and If-Range headers.
            String rangeHeaderValue = request.getHeader("Range");
            if (rangeHeaderValue != null)
            {
                // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
                if (!rangeHeaderValue.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$"))
                {
                    response.setHeader("Content-Range", "bytes */" + total); // Required in 416.
                    response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    return;
                }

                // If-Range header should either match ETag or be greater then LastModified. If not,
                // then return full file.
                String ifRange = request.getHeader("If-Range");
                if (ifRange != null && !ifRange.equals(eTag))
                {
                    try
                    {
                        long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                        if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified)
                        {
                            ranges.add(full);
                        }
                    }
                    catch (IllegalArgumentException ignore)
                    {
                        ranges.add(full);
                    }
                }

                // If any valid If-Range header, then process each part of byte range.
                if (ranges.isEmpty())
                {
                    for (String part : rangeHeaderValue.substring(6).split(","))
                    {
                        // Assuming a file with length of 100, the following examples returns bytes at:
                        // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
                        long start = sublong(part, 0, part.indexOf("-"));
                        long end = sublong(part, part.indexOf("-") + 1, part.length());

                        if (start == -1)
                        {
                            start = total - end;
                            end = total - 1;
                        }
                        else if (end == -1 || end > total - 1)
                        {
                            end = total - 1;
                        }

                        // Check if Range is syntactically valid. If not, then return 416.
                        if (start > end)
                        {
                            response.setHeader("Content-Range", "bytes */" + total); // Required in 416.
                            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                            return;
                        }

                        // Add range.
                        ranges.add(new Range(start, end, total));
                    }
                }
            }

            // Get content type
            String contentType = getMimeType(payload.getContentStream(), file, version);

            // If content type is unknown, then set the default value.
            // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
            // To add new content types, add new mime-mapping entry in web.xml.
            if (contentType == null)
            {
                contentType = "application/octet-stream";
            }

            String accept = request.getHeader("Accept");
            String disposition = accept != null && accepts(accept, contentType) ? "inline" : "attachment";

            // Initialize response.
            response.reset();
            response.setBufferSize(DEFAULT_BUFFER_SIZE);
            response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("ETag", eTag);
            response.setDateHeader("Last-Modified", lastModified);
            response.setDateHeader("Expires", expires);

            // Open streams.
            output = response.getOutputStream();

            if (ranges.isEmpty() || ranges.get(0) == full)
            {
                // Return full file.
                Range range = full;
                response.setContentType(contentType);
                response.setHeader("Content-Length", String.valueOf(range.getLength()));

                // Copy full range.
                copy(input, output, total, range.getStart(), range.getLength());

            }
            else if (ranges.size() == 1)
            {
                // Return single part of file.
                Range range = ranges.get(0);
                response.setContentType(Encode.forJava(contentType));
                response.setHeader("Content-Range", "bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());
                response.setHeader("Content-Length", String.valueOf(range.getLength()));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                copy(input, output, total, range.getStart(), range.getLength());
            }
            else
            {
                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                // Cast back to ServletOutputStream to get the easy println methods.
                ServletOutputStream sos = (ServletOutputStream) output;

                String safeContentType = JsonSanitizer.sanitize(contentType);

                // Copy multi part range.
                for (Range range : ranges)
                {
                    // Add multipart boundary and header fields for every range.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY);
                    sos.println("Content-Type: " + safeContentType);
                    sos.println("Content-Range: bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());

                    // Copy single part range of multi part range.
                    copy(input, output, total, range.getStart(), range.getLength());
                }

                // End with multipart boundary.
                sos.println();
                sos.println("--" + MULTIPART_BOUNDARY + "--");
            }

        }
        finally
        {
            close(output);
        }
    }

    /**
     * Returns true if the given match header matches the given value.
     *
     * @param header
     *            The match header.
     * @param value
     *            The value to be matched.
     * @return True if the given match header matches the given value.
     */
    private boolean matches(String header, String value)
    {
        String[] values = header.split("\\s*,\\s*");
        Arrays.sort(values);
        return Arrays.binarySearch(values, value) > -1 || Arrays.binarySearch(values, "*") > -1;
    }

    /**
     * Returns a substring of the given string value from the given begin index to the given end
     * index as a long. If the substring is empty, then -1 will be returned
     *
     * @param value
     *            The string value to return a substring as long for.
     * @param beginIndex
     *            The begin index of the substring to be returned as long.
     * @param endIndex
     *            The end index of the substring to be returned as long.
     * @return A substring of the given string value as long or -1 if substring is empty.
     */
    private long sublong(String value, int beginIndex, int endIndex)
    {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    /**
     * Returns true if the given accept header accepts the given value.
     *
     * @param header
     *            The accept header.
     * @param value
     *            The value to be accepted.
     * @return True if the given accept header accepts the given value.
     */
    private boolean accepts(String header, String value)
    {
        String[] values = header.split("\\s*(,|;)\\s*");
        Arrays.sort(values);
        return Arrays.binarySearch(values, value) > -1
                || Arrays.binarySearch(values, value.replaceAll("/.*$", "/*")) > -1
                || Arrays.binarySearch(values, "*/*") > -1;
    }

    /**
     * Copy the given byte range of the given input to the given output.
     *
     * @param input
     *            The input to copy the given range to the given output for.
     * @param output
     *            The output to copy the given range from the given input for.
     * @param total
     *            Full bytes of the input.
     * @param start
     *            Start of the byte range.
     * @param length
     *            Length of the byte range.
     * @throws IOException
     *             If something fails at I/O level.
     */
    private void copy(InputStream input, OutputStream output, long total, long start, long length) throws IOException
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;

        if (total == length)
        {
            while ((read = input.read(buffer)) > 0)
            {
                output.write(buffer, 0, read);
            }
        }
        else
        {
            // make sure correct number of bytes are skipped
            long skipped = 0;
            long totalSkipped = 0;
            while (totalSkipped < start)
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // do nothing
                }
                skipped = input.skip(start - totalSkipped);
                if (skipped == 0)
                {
                    break;
                }
                totalSkipped += skipped;
            }

            long toRead = length;

            while ((read = input.read(buffer)) > 0)
            {
                if ((toRead -= read) > 0)
                {
                    output.write(buffer, 0, read);
                }
                else
                {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        }
    }

    /**
     * Get MimeType for the requested file
     *
     * @param payload
     *            The stream returned from the Camel route
     * @param ecmFile
     *            The EcmFile object
     * @param version
     *            The version of the requested file
     * @return MimeType of the file
     */
    private String getMimeType(ContentStream payload, EcmFile ecmFile, String version)
    {
        String mimeType = "";

        if (payload != null)
        {
            mimeType = payload.getMimeType();
        }

        EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(ecmFile, version);
        // OpenCMIS thinks this is an application/octet-stream since the file has no extension
        // we will use what Tika detected in such cases
        if (ecmFileVersion != null)
        {
            if (ecmFile != null && (mimeType == null || !mimeType.equals(ecmFileVersion.getVersionMimeType())))
            {
                mimeType = ecmFileVersion.getVersionMimeType();
            }
        }
        else
        {
            if (ecmFile != null && (mimeType == null || !mimeType.equals(ecmFile.getFileActiveVersionMimeType())))
            {
                mimeType = ecmFile.getFileActiveVersionMimeType();
            }
        }

        return mimeType;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
