package com.armedia.acm.service.objectlock.transformer;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static org.junit.Assert.*;

/**
 * Created by nebojsha on 27.08.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-object-lock.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class AcmObjectLockToSolrTransformerIT {

    @Autowired
    AcmObjectLockToSolrTransformer transformer;

    @Test
    public void testIsAcmObjectTypeSupported() throws Exception {
        assertNotNull(transformer);
        assertNotNull(transformer.getDao());
        assertTrue(transformer.isAcmObjectTypeSupported(AcmObjectLock.class));
    }
}