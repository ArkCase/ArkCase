package com.armedia.acm.plugins.onlyoffice.model;

import com.armedia.acm.plugins.onlyoffice.exceptions.UnsupportedExtension;

import javax.validation.constraints.NotNull;

import java.util.*;

public class DocumentTypeResolver
{
    private List<String> textExt = Collections.unmodifiableList(Arrays.asList(
            ".doc", ".docx", ".docm",
            ".dot", ".dotx", ".dotm",
            ".odt", ".fodt", ".rtf", ".txt",
            ".html", ".htm", ".mht",
            ".pdf", ".djvu", ".fb2", ".epub", ".xps"));

    private List<String> spreadsheetExt = Collections.unmodifiableList(Arrays.asList(
            ".xls", ".xlsx", ".xlsm",
            ".xlt", ".xltx", ".xltm",
            ".ods", ".fods", ".csv"));

    private List<String> presentationExt = Collections.unmodifiableList(Arrays.asList(
            ".pps", ".ppsx", ".ppsm",
            ".ppt", ".pptx", ".pptm",
            ".pot", ".potx", ".potm",
            ".odp", ".fodp"));

    private Map<String, String> extensionDocumentTypeMap = new HashMap<>();

    public DocumentTypeResolver()
    {
        for (String ext : textExt)
        {
            extensionDocumentTypeMap.put(ext, "text");
        }
        for (String ext : spreadsheetExt)
        {
            extensionDocumentTypeMap.put(ext, "spreadsheet");
        }
        for (String ext : presentationExt)
        {
            extensionDocumentTypeMap.put(ext, "presentation");
        }

    }

    /**
     * resolves document type for give extension. Requires not null argument.
     * if not found, return null.
     * 
     * @param extension
     * @return String document type
     */
    public String resolveDocumentType(@NotNull String extension)
    {
        if (!extensionDocumentTypeMap.containsKey(extension))
        {
            throw new UnsupportedExtension("Extension " + extension + " is not supported.");
        }
        return extensionDocumentTypeMap.get(extension);
    }

    public List<String> getTextExt()
    {
        return textExt;
    }

    public List<String> getSpreadsheetExt()
    {
        return spreadsheetExt;
    }

    public List<String> getPresentationExt()
    {
        return presentationExt;
    }
}
