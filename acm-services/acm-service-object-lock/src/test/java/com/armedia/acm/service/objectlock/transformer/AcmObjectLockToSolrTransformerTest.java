package com.armedia.acm.service.objectlock.transformer;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by nebojsha on 27.08.2015.
 */
public class AcmObjectLockToSolrTransformerTest
{


    @Test
    public void testIsAcmObjectTypeSupported() throws Exception
    {
        AcmObjectLockToSolrTransformer unit = new AcmObjectLockToSolrTransformer();
        assertTrue(unit.isAcmObjectTypeSupported(AcmObjectLock.class));
    }
}