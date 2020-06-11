package com.armedia.acm.plugins.casefile.dao;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-case-file-queue-service.xml",
        "/spring/spring-library-case-file-rules.xml",
        "/spring/spring-library-case-file-dao-test.xml",
        "/spring/spring-library-user-tracker.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-search.xml",
        "/spring/test-case-file-context.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-case-plugin-test.xml",
        "/spring/spring-test-quartz-scheduler.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-integration-case-file-test.xml",
        "/spring/spring-library-service-data.xml"
})
@Rollback(true)
public class AcmQueueDaoIT
{

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    private AcmQueueDao acmQueueDao;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {

        auditAdapter.setUserId("auditUser");
    }

    @Test
    public void getQueues()
    {
        assertNotNull(acmQueueDao);

        acmQueueDao.findAll();
    }
}
