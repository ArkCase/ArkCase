package com.armedia.acm.plugins.complaint;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.services.participants.model.AcmParticipant;

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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring", locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-complaint-dao-test.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class CloseComplaintRequestDaoIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    @Autowired
    private CloseComplaintRequestDao requestDao;

    @Autowired
    private ComplaintDao complaintDao;

    @PersistenceContext
    private EntityManager entityManager;

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
    public void saveCloseComplaintRequest()
    {

        // MySQL needs the complaint to exist
        Complaint complaint = new Complaint();
        complaint.setComplaintTitle("Grateful Dead");
        complaint.setComplaintNumber("Grateful Dead");

        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId" + UUID.randomUUID().toString());
        folder.setName("The Band");
        complaint.getContainer().setFolder(folder);

        Complaint persisted = complaintDao.save(complaint);

        complaintDao.getEm().flush();

        CloseComplaintRequest ccr = new CloseComplaintRequest();
        ccr.setComplaintId(persisted.getComplaintId());
        ccr.setStatus("DRAFT");

        AcmParticipant reviewer = new AcmParticipant();
        reviewer.setParticipantType("approver");
        reviewer.setParticipantLdapId("jgarcia");

        ccr.getParticipants().add(reviewer);

        Disposition d = new Disposition();
        d.setDispositionType("add-to-existing-case");
        d.setExistingCaseNumber("12345678");

        ccr.setDisposition(d);

        CloseComplaintRequest saved = requestDao.save(ccr);

        entityManager.flush();

        log.info("CCR ID: " + saved.getId());

        CloseComplaintRequest found = requestDao.find(saved.getId());

        assertEquals(1, found.getParticipants().size());
    }

}
