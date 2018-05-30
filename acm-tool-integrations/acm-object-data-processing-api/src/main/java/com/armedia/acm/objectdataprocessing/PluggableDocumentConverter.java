package com.armedia.acm.objectdataprocessing;

/*-
 * #%L
 * ACM Object Data Processing API
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of <code>DocumentConverter</code> that allows converters capable of converting from specific mime
 * types to be registered.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 *
 */
public class PluggableDocumentConverter implements DocumentConverter
{

    private static final DocumentConverter NON_CONVERTER = new NonConverter();
    private String outputMimeType;
    /**
     * Converters registry. Converters are registered with a key that corresponds to the mime type they are capable of
     * converting to PDF.
     */
    private Map<String, DocumentConverter> convertеrRegistry = new HashMap<>();

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.printdocuments.DocumentConverter#getOutputMimeType()
     */
    @Override
    public String getOutputMimeType()
    {
        return outputMimeType;
    }

    /**
     * @param outputMimeType
     *            the outputMimeType to set
     */
    public void setOutputMimeType(String outputMimeType)
    {
        this.outputMimeType = outputMimeType;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.printdocuments.DocumentConverter#getSupportedMimeTypes()
     */
    @Override
    public Set<String> getSupportedMimeTypes()
    {
        return convertеrRegistry.keySet();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.printdocuments.PdfConverter#convertToPdf(java.io.InputStream, java.lang.String)
     */
    @Override
    public InputStream convertStream(InputStream sourceStream, String mimeType) throws UnconvertableSourceException
    {
        if (getOutputMimeType().equalsIgnoreCase(mimeType))
        {
            return sourceStream;
        }
        return convertеrRegistry.getOrDefault(mimeType, NON_CONVERTER).convertStream(sourceStream, mimeType);
    }

    /**
     * @param convertеrs
     *            the convertеrRegistry to set
     */
    public void setConvertеrRegistry(List<DocumentConverter> convertеrs)
    {
        for (DocumentConverter converter : convertеrs)
        {
            if (!getOutputMimeType().equals(converter.getOutputMimeType()))
            {
                continue;
            }
            Set<String> supportedMimeTypes = converter.getSupportedMimeTypes();
            for (String mimeType : supportedMimeTypes)
            {
                convertеrRegistry.put(mimeType, converter);
            }
        }
    }

    /**
     * Implementation used for throwing <code>UnconvertableSourceException</code> if there is no converter registered
     * for the mime type of the source input stream.
     *
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
     *
     */
    private static class NonConverter implements DocumentConverter
    {

        /*
         * (non-Javadoc)
         * @see com.armedia.acm.printdocumentse.PdfConverter#convertToPdf(java.io.InputStream, java.lang.String)
         */
        @Override
        public InputStream convertStream(InputStream sourceInputStream, String mimeType) throws UnconvertableSourceException
        {
            throw new UnconvertableSourceException(String.format("No registered converter for mime type %s.", mimeType));
        }

    }

}
