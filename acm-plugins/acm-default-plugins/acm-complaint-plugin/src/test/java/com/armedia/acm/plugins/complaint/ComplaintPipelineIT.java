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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-admin.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-business-process.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml",
        "/spring/spring-library-case-file-dao.xml",
        "/spring/spring-library-case-file-events.xml",
        "/spring/spring-library-case-file-rules.xml",
        "/spring/spring-library-case-file-save.xml",
        "/spring/spring-library-complaint.xml",
        "/spring/spring-library-complaint-plugin-test.xml",
        "/spring/spring-library-complaint-plugin-test-mule.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-form-configurations.xml",
        "/spring/spring-library-forms-configuration.xml",
        "/spring/spring-library-functional-access-control.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ms-outlook-plugin.xml",
        "/spring/spring-library-note.xml",
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-pdf-utilities.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-rules.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-library-profile.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-convert-folder-service.xml"
})
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class ComplaintPipelineIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    private SaveComplaintTransaction saveComplaintTransaction;

    private ComplaintFactory complaintFactory = new ComplaintFactory();

    private Logger log = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    @Test
    @Transactional
    public void saveComplaintFlow() throws Exception
    {
        Complaint complaint = complaintFactory.complaint();
        complaint.setRestricted(true);
        complaint.setCreator("auditUser");

        // complaint number should be set by the flow
        complaint.setComplaintNumber(null);

        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "testUser");

        Complaint saved = saveComplaintTransaction.saveComplaint(complaint, auth);

        entityManager.flush();

        assertNotNull(saved.getComplaintId());
        assertNotNull(saved.getComplaintNumber());

        log.info("New complaint id: " + saved.getComplaintId());
        log.info("New complaint number: " + saved.getComplaintNumber());
    }
}
