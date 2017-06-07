package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.Range;
import com.armedia.acm.plugins.ecm.service.StreamVideoService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.collections.map.HashedMap;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 6/6/2017.
 */
public class StreamVideoServiceImpl implements StreamVideoService
{
    private final long DEFAULT_EXPIRE_TIME = 604800000L;
    private final int DEFAULT_BUFFER_SIZE = 10240;
    private final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    private CmisConfigUtils cmisConfigUtils;
    private MuleContextManager muleContextManager;
    private FolderAndFilesUtils folderAndFilesUtils;

    @Override
    public void stream(String cmisId, HttpServletRequest request, HttpServletResponse response, EcmFile ecmFile, String version) throws AcmUserActionFailedException, MuleException, AcmObjectNotFoundException, IOException
    {
        Map<String, Object> messageProps = new HashedMap();
        messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, getCmisConfigUtils().getCmisConfiguration(ecmFile.getCmisRepositoryId()));
        MuleMessage downloadedFile = getMuleContextManager().send("vm://downloadFileFlow.in", cmisId, messageProps);

        if (downloadedFile == null || downloadedFile.getPayload() == null || !(downloadedFile.getPayload() instanceof ContentStream))
        {
            throw new AcmObjectNotFoundException(null, null, "File not found", null);
        }

        ContentStream payload = (ContentStream) downloadedFile.getPayload();

        stream(request, response, payload, ecmFile, version);
    }

    @Override
    public void stream(HttpServletRequest request, HttpServletResponse response, ContentStream payload, EcmFile ecmFile, String version) throws IOException
    {

        if (payload == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream input = null;
        OutputStream output = null;
        ByteArrayOutputStream baos = null;
        try(InputStream inputStream = payload.getStream())
        {
            baos = copy(inputStream);
            input = new ByteArrayInputStream(baos.toByteArray());
            long totalSize = baos.size();


            // Prepare some variables. The ETag is an unique identifier of the file.
            String fileName = payload.getFileName();
            long length = totalSize;
            long lastModified = ecmFile.getModified().getTime();
            String eTag = fileName + "_" + length + "_" + lastModified;
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
            Range full = new Range(0, length - 1, length);
            List<Range> ranges = new ArrayList<>();

            // Validate and process Range and If-Range headers.
            String rangeHeaderValue = request.getHeader("Range");
            if (rangeHeaderValue != null)
            {
                // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
                if (!rangeHeaderValue.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$"))
                {
                    response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
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
                            start = length - end;
                            end = length - 1;
                        }
                        else if (end == -1 || end > length - 1)
                        {
                            end = length - 1;
                        }

                        // Check if Range is syntactically valid. If not, then return 416.
                        if (start > end)
                        {
                            response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                            return;
                        }

                        // Add range.
                        ranges.add(new Range(start, end, length));
                    }
                }
            }


            // Get content type by file name and set default GZIP support and content disposition.
            String contentType = getMimeType(payload, ecmFile, version);

            // If content type is unknown, then set the default value.
            // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
            // To add new content types, add new mime-mapping entry in web.xml.
            if (contentType == null) {
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
                copy(input, output, totalSize, range.getStart(), range.getLength());

            }
            else if (ranges.size() == 1)
            {
                // Return single part of file.
                Range range = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range", "bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());
                response.setHeader("Content-Length", String.valueOf(range.getLength()));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                copy(input, output, totalSize, range.getStart(), range.getLength());
            }
            else
            {
                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                // Cast back to ServletOutputStream to get the easy println methods.
                ServletOutputStream sos = (ServletOutputStream) output;

                // Copy multi part range.
                for (Range range : ranges)
                {
                    // Add multipart boundary and header fields for every range.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY);
                    sos.println("Content-Type: " + contentType);
                    sos.println("Content-Range: bytes " + range.getStart() + "-" + range.getEnd() + "/" + range.getTotal());

                    // Copy single part range of multi part range.
                    copy(input, output, totalSize, range.getStart(), range.getLength());
                }

                // End with multipart boundary.
                sos.println();
                sos.println("--" + MULTIPART_BOUNDARY + "--");
            }

            output.flush();
        }
        finally
        {
            close(input);
            close(baos);
            close(output);
        }
    }

    /**
     * Returns true if the given match header matches the given value.
     * @param header The match header.
     * @param value The value to be matched.
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
     * @param value The string value to return a substring as long for.
     * @param beginIndex The begin index of the substring to be returned as long.
     * @param endIndex The end index of the substring to be returned as long.
     * @return A substring of the given string value as long or -1 if substring is empty.
     */
    private long sublong(String value, int beginIndex, int endIndex)
    {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    /**
     * Returns true if the given accept header accepts the given value.
     * @param header The accept header.
     * @param value The value to be accepted.
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
     * @param input The input to copy the given range to the given output for.
     * @param output The output to copy the given range from the given input for.
     * @param total Full bytes of the input.
     * @param start Start of the byte range.
     * @param length Length of the byte range.
     * @throws IOException If something fails at I/O level.
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
            input.skip(start);
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
     * Close the given resource.
     * @param resource The resource to be closed.
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

    /**
     * Get MimeType for the requested file
     * @param payload The stream returned from the Mule flow
     * @param ecmFile The EcmFile object
     * @param version The version of the requested file
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

    private ByteArrayOutputStream copy(InputStream inputStream)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            if (inputStream != null)
            {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > -1)
                {
                    baos.write(buffer, 0, len);
                }
                baos.flush();
            }
        }
        catch (Exception e)
        {
            // Do nothing. Silent exception
        }

        return baos;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }
}
