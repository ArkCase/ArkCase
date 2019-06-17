package com.armedia.acm.cmis;

/*-
 * #%L
 * Tool Integrations: CMIS Configuration
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

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class MuleCmisIT
{
    @Autowired
    private MuleContextManager muleContextManager;

    private Logger log = LogManager.getLogger(getClass());

    @Test
    public void createOrFindFolder() throws Exception
    {
        String path = "/Sites/acm/documentLibrary/Complaints/testComplaint";

        Map<String, Object> properties = new HashMap<>();
        properties.put("configRef", muleContextManager.getMuleContext().getRegistry().lookupObject("alfresco"));

        MuleMessage reply = muleContextManager.send("vm://createFolder.in", path, properties);

        log.info("Reply payload of type: " + reply.getPayload().getClass().getName());
    }
}
