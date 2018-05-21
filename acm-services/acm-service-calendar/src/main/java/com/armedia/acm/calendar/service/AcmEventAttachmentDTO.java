package com.armedia.acm.calendar.service;

/*-
 * #%L
 * ACM Service: Calendar Service
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

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.InputStream;

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
    public void setMediaType(String mediaType)
    {
        this.mediaType = MediaType.parseMediaType(mediaType);
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
