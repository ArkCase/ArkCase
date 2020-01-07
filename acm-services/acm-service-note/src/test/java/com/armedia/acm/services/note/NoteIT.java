package com.armedia.acm.services.note;

/*-
 * #%L
 * ACM Service: Note
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
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-note.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-note-plugin-test.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class NoteIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    @Autowired
    private NoteDao noteDao;

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
    public void saveNote() throws Exception
    {
        Note n = new Note();
        n.setCreator("testCreator");
        n.setCreated(new Date());
        n.setNote("testNote");
        n.setType("GENERAL");
        n.setParentId(234L);
        n.setParentType("COMPLAINT");
        Note saved = noteDao.save(n);
        assertNotNull(saved.getId());
        noteDao.deleteNoteById(saved.getId());

        log.info("Note ID: " + saved.getId());
    }

}
