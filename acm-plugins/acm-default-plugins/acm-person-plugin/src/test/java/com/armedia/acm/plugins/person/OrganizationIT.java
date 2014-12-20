package com.armedia.acm.plugins.person;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
    "/spring/spring-library-person.xml",
    "/spring/spring-library-person-plugin-test-mule.xml",
    "/spring/spring-library-context-holder.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class OrganizationIT {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Autowired
    private OrganizationDao organizationDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void saveOrganization() throws Exception {
        Organization org = new Organization();

        org.setOrganizationType("sample");
        org.setOrganizationValue("tech net");

        Organization saved = organizationDao.save(org);

        em.flush();

        assertNotNull(saved.getOrganizationId());

        log.info("organization Id: " + saved.getOrganizationId());
    }
}
