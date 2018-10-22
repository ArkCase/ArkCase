package gov.foia.model;

import java.io.Serializable;

/**
 * @author sasko.tanaskoski
 *
 */
public class PortalFOIARequestFile implements Serializable
{

    private static final long serialVersionUID = -1472356731514343571L;

    private String fileName;

    private String content;

    private String contentType;

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

    /**
     * @return the content
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}