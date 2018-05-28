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

/**
 * Abstraction containing binary data with arbitrary mime type, extracted from an instances of type<code>CF</code>.
 * Instance of <code>BinaryDataProvider</code> is produced by an implementation of
 * <code>ObjectDataExtractingProcessor</code>. It is up to the service implementation to decide what data associated
 * with the <code>CF</code> instance will be extracted and added to the content ready for printing or other purposes,
 * for example filtering files included in predefined folders, specific versions and so on.
 *
 * @see ObjectDataExtractingProcessor
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 16, 2016
 *
 */
public interface BinaryDataProvider<CF>
{

    /**
     * @return generated file name to be included in the <code>Content-Disposition</code> response header.
     */
    String getFileName();

    /**
     * @return input stream containing the binary data from the generated document.
     */
    InputStream getContent();

    /**
     * Return the number of bytes of the underlying binary data to be served.
     *
     * @return
     */
    long getContentLength();

    /**
     * Cleans up any resources associated with the content.
     */
    void releaseContent();

}
