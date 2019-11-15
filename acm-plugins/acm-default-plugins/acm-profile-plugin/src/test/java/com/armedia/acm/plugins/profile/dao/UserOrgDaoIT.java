package com.armedia.acm.plugins.profile.dao;

/*-
 * #%L
 * ACM Default Plugin: Profile
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.service.outlook.dao.OutlookPasswordDao;
import com.armedia.acm.service.outlook.model.OutlookDTO;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-profile-plugin-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-convert-folder-service.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class UserOrgDaoIT extends EasyMockSupport
{

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    OutlookPasswordDao outlookPasswordDao;

    private Authentication authentication;

    @PersistenceContext
    private EntityManager em;

    private String userid;

    private String findFirstUserJpql = "SELECT o.user.userId FROM UserOrg o";

    @Before
    public void setUp()
    {
        authentication = createMock(Authentication.class);

        Query findFirstUserQuery = em.createQuery(findFirstUserJpql);
        findFirstUserQuery.setFirstResult(0);
        findFirstUserQuery.setMaxResults(1);

        List<String> users = findFirstUserQuery.getResultList();
        if (users != null && !users.isEmpty())
        {
            userid = users.get(0);
        }

    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testSaveAndRetriveOutlookPassword() throws Exception
    {
        // if no userid, then there are no user profiles in the system, and we can't test the encryption.
        // we won't actually create a new user profile in this test.
        if (userid == null)
        {
            return;
        }

        expect(authentication.getName()).andReturn(userid).atLeastOnce();
        replayAll();

        OutlookDTO in = new OutlookDTO();
        in.setOutlookPassword("Armedia123");
        outlookPasswordDao.saveOutlookPassword(authentication, in);

        OutlookDTO outlookDTO = outlookPasswordDao.retrieveOutlookPassword(authentication);
        assertNotNull(outlookDTO);
        assertEquals("Armedia123", outlookDTO.getOutlookPassword());

        verifyAll();
    }
}
