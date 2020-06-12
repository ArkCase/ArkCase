package com.armedia.acm.plugins.ecm.dao;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

/**
 * Created by armdev on 4/22/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring", locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-add-file-camel.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-acm-email.xml",
        "/spring/spring-test-quartz-scheduler.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-ecm-file-sync.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-library-activemq.xml" })
@Rollback(true)
public class EcmFileDaoIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    private EcmFileDao ecmFileDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void saveEcmFile()
    {

        EcmFile file = new EcmFile();

        file.setFileActiveVersionMimeType("text/plain");
        file.setVersionSeriesId("cmisFileId");
        file.setFileActiveVersionNameExtension("fileNameExtension");
        file.setFileName("testFileName");

        EcmFileVersion version = new EcmFileVersion();
        version.setCmisObjectId("cmisObjectId");
        version.setVersionTag("versionTag");
        version.setVersionFileNameExtension("fileNameExtension");
        version.setVersionMimeType("mime/type");
        file.getVersions().add(version);

        file.setActiveVersionTag(version.getVersionTag());

        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId");
        folder.setName("folderName");

        AcmContainer container = new AcmContainer();
        container.setFolder(folder);
        container.setContainerObjectId(500L);
        container.setContainerObjectType("containerObjectType");
        container.setContainerObjectTitle("containerObjectTitle");

        file.setFolder(folder);
        file.setContainer(container);

        file = ecmFileDao.save(file);

        // flush() causes JPA to issue the SQL, so we will see any database exceptions from null constraints, etc.
        // but the transaction will still rollback, so we aren't popping test data into our db.
        entityManager.flush();

        log.info("File ID: " + file.getFileId());

        assertNotNull(file.getFileId());

    }

    @Test
    public void getFilesCount()
    {
        assertNotNull(ecmFileDao);
        assertNotNull(ecmFileDao.getFilesCount(LocalDateTime.now()));
    }
}
