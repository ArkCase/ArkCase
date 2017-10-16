package com.armedia.acm.objectdataprocessing;

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
     * @param objectIds IDs of the instances of <code>CF</code> whose data should be processed for producing the binary
     *            data available from the instance of <code>PD</code>.
     * @return an instance of <code>BinaryDataProvider</code> containing the binary data that was extracted and prepared
     *         for consumption.
     * @throws IOException if there was an error while processing the instances of <code>CF</code>.
     *
     * @see PD#getContent()
     */
    PD processObjects(List<Long> objectIds) throws IOException;

    /**
     * Allows for post processing of the print document before response is sent back. This might include raising events,
     * or adding additional pages to the resulting PDF for example.
     *
     * @param dataProvider the binary data provider that was produced by <code>processObjects</code> method.
     * @see ObjectDataExtractingProcessor#processObjects(List)
     */
    void postProcessDataProvider(PD dataProvider);

}
