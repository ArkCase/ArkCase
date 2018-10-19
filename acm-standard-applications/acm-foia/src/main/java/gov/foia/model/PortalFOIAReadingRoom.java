package gov.foia.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import gov.foia.util.JsonDateSerializer;

/**
 * @author sasko.tanaskoski
 *
 */
public class PortalFOIAReadingRoom
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

}
