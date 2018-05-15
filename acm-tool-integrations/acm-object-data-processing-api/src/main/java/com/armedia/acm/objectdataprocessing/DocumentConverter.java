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
import java.util.Collections;
import java.util.Set;

/**
 * Defines functionality for converting an input stream of input mime type to input stream of output mime type.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 *
 */
public interface DocumentConverter
{

    /**
     * Returns the mime type of the binary data of the converted <code>InputStream</code>.
     *
     * @return the mime type of the converted <code>InputStream</code>.
     */
    default String getOutputMimeType()
    {
        return "";
    }

    /**
     * Returns a set of mime types of the binary data of the <code>InputStream</code> this converter is capable of
     * processing.
     *
     * @return set of mime types this converter is capable of processing.
     */
    default Set<String> getSupportedMimeTypes()
    {
        return Collections.emptySet();
    }

    /**
     * Converts input stream of input mime type to input stream of output mime type.
     *
     * @param sourceInputStream
     *            input stream of document with arbitrary mime type.
     * @param mimeType
     *            the mime type of the <code>sourceInputStream</code>.
     * @return the converted input stream.
     * @throws UnconvertableSourceException
     *             if the <code>sourceInputStream</code> can not be converted.
     */
    InputStream convertStream(InputStream sourceInputStream, String mimeType) throws UnconvertableSourceException;

}
