package com.armedia.acm.plugins.person;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Organization;
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
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-plugin-test-mule.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-search.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class OrganizationIT
{

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
    public void saveOrganization() throws Exception
    {
        Organization org = new Organization();

        org.setOrganizationType("sample");
        org.setOrganizationValue("tech net");


        Identification i1 = new Identification();
        i1.setIdentificationIssuer("issuer1");
        i1.setIdentificationNumber("131312312");
        i1.setIdentificationType("VISA");
        i1.setIdentificationYearIssued(new Date());

        ContactMethod cm1 = new ContactMethod();
        cm1.setType("mobile");
        cm1.setValue("23123123");

        ContactMethod cm2 = new ContactMethod();
        cm2.setType("mobile");
        cm2.setValue("23123123");

        PostalAddress pa1 = new PostalAddress();
        pa1.setCity("Amaurot");
        pa1.setCountry("Utopia");
        pa1.setType("type");

        PostalAddress pa2 = new PostalAddress();
        pa2.setCity("Atlanta");
        pa2.setCountry("Georgia");
        pa2.setType("type");

        org.getIdentifications().add(i1);
        org.getContactMethods().add(cm1);
        org.getContactMethods().add(cm2);
        org.getAddresses().add(pa1);
        org.getAddresses().add(pa2);


        Long savedId = organizationDao.save(org).getOrganizationId();

        em.flush();

        assertNotNull(savedId);


        Organization saved = organizationDao.find(savedId);
        assertEquals(1, saved.getIdentifications().size());
        assertEquals(2, saved.getContactMethods().size());
        assertEquals(2, saved.getAddresses().size());

        assertEquals("com.armedia.acm.plugins.person.model.Organization", saved.getClassName());

        assertEquals("sample", org.getOrganizationType());


    }
}
