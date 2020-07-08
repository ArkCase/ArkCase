package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import gov.privacy.util.JsonDateSerializer;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class PortalSARReadingRoom
{
    public static class File
    {

        private String fileId;

        private String fileName;

        /**
         * @return the fileId
         */
        public String getFileId()
        {
            return fileId;
        }

        /**
         * @param fileId
         *            the fileId to set
         */
        public void setFileId(String fileId)
        {
            this.fileId = fileId;
        }

        /**
         * @return the fileName
         */
        public String getFileName()
        {
            return fileName;
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

    private String requestId;

    private String requestTitle;

    private String contentSearch;

    private File file;

    private String description;


    @JsonSerialize(using = JsonDateSerializer.class)
    private Date publishedDate;

    /**
     * @return the requestId
     */
    public String getRequestId()
    {
        return requestId;
    }

    /**
     * @param requestId
     *            the requestId to set
     */
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    /**
     * @return the requestTitle
     */
    public String getRequestTitle()
    {
        return requestTitle;
    }

    /**
     * @param requestTitle
     *            the requestTitle to set
     */
    public void setRequestTitle(String requestTitle)
    {
        this.requestTitle = requestTitle;
    }

    /**
     * @return the contentSearch
     */
    public String getContentSearch()
    {
        return contentSearch;
    }

    /**
     * @param contentSearch
     *            the contentSearch to set
     */
    public void setContentSearch(String contentSearch)
    {
        this.contentSearch = contentSearch;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the publishedDate
     */
    public Date getPublishedDate()
    {
        return publishedDate;
    }

    /**
     * @param publishedDate
     *            the publishedDate to set
     */
    public void setPublishedDate(Date publishedDate)
    {
        this.publishedDate = publishedDate;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
