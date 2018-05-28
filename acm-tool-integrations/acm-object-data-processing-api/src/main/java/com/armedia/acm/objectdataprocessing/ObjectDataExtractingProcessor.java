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

import java.io.IOException;
import java.util.List;

/**
 * Produces an instance of <code>BinaryDataProvider</code> for instances of <code>CF</code> type. It is up to the
 * implementation to decide what data will be extracted from the <code>CF</code> instances and will be included in the
 * resulting <code>InputStream</code> available from the resulting instance of <code>PD</code>.
 *
 * @see BinaryDataProvider#getContent()
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 16, 2016
 *
 */
public interface ObjectDataExtractingProcessor<CF, PD extends BinaryDataProvider<CF>>
{

    /**
     * @param objectIds
     *            IDs of the instances of <code>CF</code> whose data should be processed for producing the binary
     *            data available from the instance of <code>PD</code>.
     * @return an instance of <code>BinaryDataProvider</code> containing the binary data that was extracted and prepared
     *         for consumption.
     * @throws IOException
     *             if there was an error while processing the instances of <code>CF</code>.
     *
     * @see PD#getContent()
     */
    PD processObjects(List<Long> objectIds) throws IOException;

    /**
     * Allows for post processing of the print document before response is sent back. This might include raising events,
     * or adding additional pages to the resulting PDF for example.
     *
     * @param dataProvider
     *            the binary data provider that was produced by <code>processObjects</code> method.
     * @see ObjectDataExtractingProcessor#processObjects(List)
     */
    void postProcessDataProvider(PD dataProvider);

}
