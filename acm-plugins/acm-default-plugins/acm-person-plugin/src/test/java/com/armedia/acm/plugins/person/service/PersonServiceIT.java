package com.armedia.acm.plugins.person.service;

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
import static org.junit.Assert.assertNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-person-plugin-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-addressable-plugin.xml",
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
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-acm-email.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-test-quartz-scheduler.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-addressable-plugin.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-library-folder-watcher.xml" })
@TransactionConfiguration(defaultRollback = true)
public class PersonServiceIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    PersonService personService;

    @Autowired
    PersonDao personDao;

    @Autowired
    AuditPropertyEntityAdapter adapter;

    @Test
    public void addNewPersonTest()
    {
        Person person = new Person();
        String auth = "ann";
        adapter.setUserId(auth);

        person.setCompany("Company");
        person.setFamilyName("Family name");
        person.setGivenName("Name");
        person.setCreator("creator");

        personService.addPersonIdentification("key", "value", person);

        person = personDao.save(person);

        Person foundedPerson = personService.get(person.getId());

        assertNotNull(foundedPerson.getId());
        assertEquals(1, foundedPerson.getIdentifications().size());

        personDao.deletePersonById(person.getId());

        foundedPerson = personService.get(person.getId());
        assertNull(foundedPerson);
    }

}
