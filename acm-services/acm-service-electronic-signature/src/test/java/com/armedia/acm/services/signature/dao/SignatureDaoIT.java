package com.armedia.acm.services.signature.dao;

/*-
 * #%L
 * ACM Service: Electronic Signature
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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.services.signature.BaseTestCase;
import com.armedia.acm.services.signature.model.Signature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-electronic-signature-dao.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-object-converter.xml"
})
@Rollback(true)
public class SignatureDaoIT extends BaseTestCase
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    private SignatureDao dao;

    private Logger log = LogManager.getLogger(getClass());

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
