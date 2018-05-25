package com.armedia.acm.plugins.onlyoffice.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The config base section allows to change the platform type used, document display size (width and height) and type of
 * the document opened.
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Config
{
    /**
     * Defines the document type to be opened: open a text document (.doc, .docm, .docx, .dot, .dotm, .dotx, .epub,
     * .fodt, .htm, .html, .mht, .odt, .ott, .pdf, .rtf, .txt, .djvu, .xps) for viewing or editing - text, open a
     * spreadsheet (.csv, .fods, .ods, .ots, .xls, .xlsm, .xlsx, .xlt, .xltm, .xltx) for viewing or editing -
     * spreadsheet, open a presentation (.fodp, .odp, .otp, .pot, .potm, .potx, .pps, .ppsm, .ppsx, .ppt, .pptm, .pptx)
     * for viewing or editing - presentation.
     */
    private String documentType;
    /**
     * Defines the document height (100% by default) in the browser window.
     */
    private String height;
    /**
     * Defines the encrypted signature added to the Document Server config in the form of a token.
     */
    private String token;
    /**
     * Defines the platform type used to access the document. Can be: optimized to access the document from a desktop or
     * laptop computer - desktop, optimized to access the document from a tablet or a smartphone - mobile, specifically
     * formed to be easily embedded into a web page - embedded. The default value is "desktop".
     */
    private String type;
    /**
     * Defines the document width (100% by default) in the browser window.
     */
    private String width;
    /**
     * The document section allows to change all the parameters pertaining to the document (title, url, file type,
     * etc.).
     */
    private Document document;
    /**
     * The editorConfig section allows to change the parameters pertaining to the editorConfig interface: opening mode
     * (viewer
     * or editorConfig), interface language, additional buttons, etc.).
     */
    private EditorConfig editorConfig;

    public String getDocumentType()
    {
        return documentType;
    }

    public void setDocumentType(String documentType)
    {
        this.documentType = documentType;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getWidth()
    {
        return width;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    public Document getDocument()
    {
        return document;
    }

    public void setDocument(Document document)
    {
        this.document = document;
    }

    public EditorConfig getEditorConfig()
    {
        return editorConfig;
    }

    public void setEditorConfig(EditorConfig editorConfig)
    {
        this.editorConfig = editorConfig;
    }
}
