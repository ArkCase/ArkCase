package com.armedia.acm.services.signature.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.services.signature.BaseTestCase;
import com.armedia.acm.services.signature.model.Signature;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"/spring/spring-library-data-source.xml",
        "/spring/spring-library-electronic-signature-dao.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml"
		})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class SignatureDaoIT extends BaseTestCase
{
    @Autowired
    private SignatureDao dao;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Test
    @Transactional
    public void saveSignature_findSignature() throws Exception
    {
    	Long objectId = 100L;
    	String objectType = "TASK";
    	String user = "user";
    	
        Signature signature = new Signature();
        signature.setObjectId(objectId);
        signature.setObjectType(objectType);
        signature.setSignedBy(user);

        Signature saved = dao.save(signature);
        assertNotNull(saved.getSignatureId());
        log.info("Saved Electronic Signature ID: " + saved.getSignatureId());

    }
    
    @Test
    @Transactional
    public void retrieveSignature_ObjectId_ObjectType() throws Exception
    {
    	Long objectId = 100L;
    	String objectType = "TASK";
    	
    	// just make sure generated sql is valid, won't find anything
        List<Signature> signatureList = dao.findByObjectIdObjectType(objectId, objectType);
        assertNotNull(signatureList);
    }
}

