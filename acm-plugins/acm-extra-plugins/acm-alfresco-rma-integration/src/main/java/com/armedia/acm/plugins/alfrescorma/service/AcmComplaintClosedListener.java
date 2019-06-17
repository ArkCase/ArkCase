/**
 * 
 */
package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationManager;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaConfig;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintClosedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

/**
 * @author riste.tutureski
 *
 */
public class AcmComplaintClosedListener implements ApplicationListener<ComplaintClosedEvent>
{

    private transient Logger LOG = LogManager.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private AcmAuthenticationManager authenticationManager;

    @Override
    public void onApplicationEvent(ComplaintClosedEvent event)
    {
        AlfrescoRmaConfig rmaConfig = alfrescoRecordsService.getRmaConfig();

        boolean proceed = rmaConfig.getIntegrationEnabled() && rmaConfig.getDeclareRecordsOnComplaintClose();
        if (!proceed)
        {
            return;
        }

        if (!event.isSucceeded())
        {
            LOG.trace("Returning - closing complaint was not successful");
            return;
        }

        Complaint complaint = (Complaint) event.getSource();

        if (null != complaint)
        {

            String principal = event.getUserId();
            AcmAuthentication authentication;
            try
            {
                authentication = authenticationManager.getAcmAuthentication(
                        new UsernamePasswordAuthenticationToken(principal, principal));
            }
            catch (AuthenticationServiceException e)
            {
                authentication = new AcmAuthentication(Collections.emptySet(), principal, "",
                        true, principal);
            }
            getAlfrescoRecordsService().declareAllContainerFilesAsRecords(authentication, complaint.getContainer(),
                    event.getEventDate(), complaint.getComplaintNumber());
        }

    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

    public AcmAuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }

    public void setAuthenticationManager(AcmAuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }
}
