package com.armedia.acm.plugins.person;

import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
    "/spring/spring-library-person.xml",
    "/spring/spring-library-person-plugin-test.xml",
    "/spring/spring-library-mule-context-manager.xml",
    "/spring/spring-library-mule-context-manager.xml",
    "/spring/spring-library-activiti-actions.xml",
    "/spring/spring-library-activemq.xml",
    "/spring/spring-library-activiti-configuration.xml",
    "/spring/spring-library-folder-watcher.xml",
    "/spring/spring-library-cmis-configuration.xml",
    "/spring/spring-library-drools-monitor.xml",
    "/spring/spring-library-ecm-file.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class OrganizationIT {

    @Autowired
    private OrganizationDao organizationDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    @Transactional
    public void saveOrganization() throws Exception {
        Organization org = new Organization();

        org.setOrganizationType("sample");
        org.setOrganizationValue("tech net");
        org.setModifier("testModifier");
        org.setCreator("testCreator");
        org.setCreated(new Date());
        org.setModified(new Date());

        Organization saved = organizationDao.save(org);

        assertNotNull(saved.getOrganizationId());

        log.info("organization Id: " + saved.getOrganizationId());
    }
}
