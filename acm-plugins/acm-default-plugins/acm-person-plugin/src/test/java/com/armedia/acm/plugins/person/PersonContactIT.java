package com.armedia.acm.plugins.person;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.dao.PersonContactDao;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.PersonContact;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-plugin-test.xml",
        "/spring/spring-library-person-plugin-test-mule.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-person-rules.xml",
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-acm-email.xml",
        "/spring/spring-test-quartz-scheduler.xml" })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class PersonContactIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }
    @Autowired
    private PersonContactDao personContactDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void savePersonContact() throws Exception
    {
        PersonContact pc = new PersonContact();
        pc.setAttention("Attention");
        pc.setCompanyName("Company name");
        pc.setPersonName("Person name");

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

        pc.getIdentifications().add(i1);
        pc.getContactMethods().add(cm1);
        pc.getContactMethods().add(cm2);
        pc.getAddresses().add(pa1);
        pc.getAddresses().add(pa2);

        Long savedId = personContactDao.save(pc).getId();

        em.flush();

        PersonContact saved = personContactDao.find(savedId);

        assertEquals("Attention", saved.getAttention());
        assertEquals("Company name", saved.getCompanyName());
        assertEquals("Person name", saved.getPersonName());

        assertEquals(1, saved.getIdentifications().size());
        assertEquals(2, saved.getContactMethods().size());
        assertEquals(2, saved.getAddresses().size());
        assertEquals("com.armedia.acm.plugins.person.model.PersonContact", saved.getClassName());

        assertNotNull(saved.getId());

    }

}
