package com.armedia.acm.calendar.service;

import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 1, 2017
 *
 */
public class AcmEventAttachmentDTO
{

    private long contentLength;

    private MediaType mediaType;

    private InputStream content;

    private String fileName;

    /**
     * @return
     */
    public HttpHeaders getHttpHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
        return headers;
    }

    /**
     * @return
     */
    public long getContentLength()
    {
        return contentLength;
    }

    /**
     * @param contentLength
     *            the contentLength to set
     */
    public void setContentLength(long contentLength)
    {
        this.contentLength = contentLength;
    }

    /**
     * @return
     */
    public MediaType getMediaType()
    {
        return mediaType;
    }

    /**
     * @param mediaType
     *            the mediaType to set
     */
    public void setMediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
    }

    /**
     * @param mediaType
     *            the mediaType to set
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = MediaType.parseMediaType(mediaType);
    }

    /**
     * @return
     */
    public InputStream getContent()
    {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(InputStream content)
    {
        this.content = content;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

}
