package com.armedia.acm.plugins.ecm.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

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
        "/spring/spring-library-ecm-plugin-test-mule.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-object-converter.xml" })
public class EcmFileVersionDaoIT
{

    @Autowired
    private EcmFileVersionDao ecmFileVersionDao;

    @Test
    public void getTotalSizeOfFiles()
    {
        assertNotNull(ecmFileVersionDao);
        assertNotNull(ecmFileVersionDao.getTotalSizeOfFiles(LocalDateTime.now()));
    }
}
