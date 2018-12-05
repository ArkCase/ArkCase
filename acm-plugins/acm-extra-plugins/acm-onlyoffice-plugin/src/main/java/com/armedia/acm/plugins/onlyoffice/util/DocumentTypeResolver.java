package com.armedia.acm.plugins.onlyoffice.util;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import com.armedia.acm.plugins.onlyoffice.exceptions.UnsupportedExtension;

import javax.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentTypeResolver
{
    private List<String> textExt = Collections.unmodifiableList(Arrays.asList(
            "doc", "docx", "docm",
            "dot", "dotx", "dotm",
            "odt", "fodt", "rtf", "txt",
            "html", "htm", "mht",
            "pdf", "djvu", "fb2", "epub", "xps"));

    private List<String> spreadsheetExt = Collections.unmodifiableList(Arrays.asList(
            "xls", "xlsx", "xlsm",
            "xlt", "xltx", "xltm",
            "ods", "fods", "csv"));

    private List<String> presentationExt = Collections.unmodifiableList(Arrays.asList(
            "pps", "ppsx", "ppsm",
            "ppt", "pptx", "pptm",
            "pot", "potx", "potm",
            "odp", "fodp"));

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
        String toLowerCaseExtension = extension.toLowerCase();
        if (!extensionDocumentTypeMap.containsKey(toLowerCaseExtension))
        {
            throw new UnsupportedExtension("Extension " + toLowerCaseExtension + " is not supported.");
        }
        return extensionDocumentTypeMap.get(toLowerCaseExtension);
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
