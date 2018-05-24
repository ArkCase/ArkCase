package com.armedia.acm.plugins.onlyoffice.model.config;

public class Document
{
    /**
     * Defines the type of the file for the source viewed or edited document.
     */
    private String fileType;
    /**
     * Defines the unique document identifier used for document recognition by the service. In case the known key is
     * sent the document will be taken from the cache. Every time the document is edited and saved, the key must be
     * generated anew. The document url can be used as the key but without the special characters and the length is
     * limited to 20 symbols.
     */
    private String key;
    /**
     * Defines the desired file name for the viewed or edited document which will also be used as file name when the
     * document is downloaded.
     */
    private String title;
    /**
     * Defines the absolute URL where the source viewed or edited document is stored.
     */
    private String url;
    /**
     * The document info section allows to change additional parameters for the document (document author, folder where
     * the document is stored, creation date, sharing settings).
     */
    private DocumentInfo info;
    /**
     * The document permission section allows to change the permission for the document to be edited and downloaded or
     * not.
     */
    private DocumentPermissions permissions;

    public Document(String fileType, String key, String title, String url)
    {
        this.fileType = fileType;
        this.key = key;
        this.title = title;
        this.url = url;
    }

    public void setInfo(DocumentInfo info)
    {
        this.info = info;
    }

    public void setPermissions(DocumentPermissions permissions)
    {
        this.permissions = permissions;
    }

    public String getFileType()
    {
        return fileType;
    }

    public String getKey()
    {
        return key;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }

    public DocumentInfo getInfo()
    {
        return info;
    }

    public DocumentPermissions getPermissions()
    {
        return permissions;
    }
}
