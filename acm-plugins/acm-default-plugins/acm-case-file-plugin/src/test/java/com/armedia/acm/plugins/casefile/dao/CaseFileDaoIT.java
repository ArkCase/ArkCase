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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Date;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-case-plugin-test.xml",
        "/spring/spring-library-case-file-dao-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-test-quartz-scheduler.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-integration-case-file-test.xml",
        "/spring/spring-library-folder-watcher.xml"
})
@Rollback(true)
public class CaseFileDaoIT
{

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
        System.setProperty("javax.net.ssl.trustStore", userHomePath + "/.arkcase/acm/private/arkcase.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
    }

    @Autowired
    private CaseFileDao caseFileDao;

    @Autowired
    private AcmObjectLockingManager acmObjectLockingManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Authentication authentication;

    @Before
    public void setUp()
    {
        authentication = new AcmAuthentication(null, null, null, true,
                "user", 0L);
        auditAdapter.setUserId("auditUser");
        entityManager.clear();
    }

    @Test
    @Transactional
    public void saveCaseFile()
    {
        assertNotNull(caseFileDao);
        assertNotNull(entityManager);

        CaseFile caseFile = new CaseFile();
        caseFile.setCaseNumber(UUID.randomUUID().toString());
        caseFile.setCaseType("caseType");
        caseFile.setStatus("status");
        caseFile.setTitle("title");
        caseFile.setRestricted(true);

        caseFile.setCreator("creator");

        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId");
        folder.setName("folderName");
        container.setFolder(folder);
        caseFile.setContainer(container);

        caseFile.setNextCourtDate(new Date());
        caseFile.setCourtroomName("courtroomName");
        caseFile.setResponsibleOrganization("responsibleOrganization");

        CaseFile saved = caseFileDao.save(caseFile);

        assertNotNull(saved.getId());
    }

    @Test
    @Transactional
    public void saveCaseFileWithLock() throws AcmObjectLockException
    {
        assertNotNull(caseFileDao);
        assertNotNull(entityManager);

        CaseFile caseFile = new CaseFile();
        caseFile.setCaseNumber(UUID.randomUUID().toString());
        caseFile.setCaseType("caseType");
        caseFile.setStatus("status");
        caseFile.setTitle("title");
        caseFile.setRestricted(true);

        caseFile.setCreator("creator");

        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId");
        folder.setName("folderName");
        container.setFolder(folder);
        caseFile.setContainer(container);

        CaseFile saved = caseFileDao.save(caseFile);

        assertNotNull(saved.getId());

        AcmObjectLock lock = acmObjectLockingManager.acquireObjectLock(saved.getId(), saved.getObjectType(), "OBJECT_LOCK", null, false,
                authentication.getName());

        assertNotNull(lock);

        assertNotNull(lock.getId());
        assertEquals(saved.getId(), lock.getObjectId());
        assertEquals(saved.getObjectType(), lock.getObjectType());

    }

    @Test
    @Transactional
    public void saveCaseQueue()
    {
        assertNotNull(caseFileDao);
        assertNotNull(entityManager);

        CaseFile caseFile = new CaseFile();
        caseFile.setCaseNumber(UUID.randomUUID().toString());
        caseFile.setCaseType("caseType");
        caseFile.setStatus("status");
        caseFile.setTitle("title");
        caseFile.setRestricted(true);

        caseFile.setCreator("creator");

        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId");
        folder.setName("folderName");
        container.setFolder(folder);
        caseFile.setContainer(container);

        AcmQueue queue = new AcmQueue();
        queue.setName("queueName");
        caseFile.setQueue(queue);

        CaseFile saved = caseFileDao.save(caseFile);

        saved = caseFileDao.find(saved.getId());

        assertNotNull(saved.getId());

        assertNotNull(saved.getQueue().getId());

        assertEquals("queueName", saved.getQueue().getName());

    }
}
