package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.profile.model.UserOrgConstants;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserRootFolderAccessUpdateExecutor implements AcmDataUpdateExecutor
{
    private final Logger log = LogManager.getLogger(getClass());

    private EcmFileParticipantService fileParticipantService;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private AcmContainerDao containerDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public String getUpdateId()
    {
        return "user-root-folder-access-update";
    }

    @Transactional
    @Override
    public void execute()
    {
        auditPropertyEntityAdapter.setUserId(AcmDataUpdateService.DATA_UPDATE_MODIFIER);

        // do not update file and folder participants if the document ACL feature is disabled
        if (!getArkPermissionEvaluator().isEnableDocumentACL())
        {
            return;
        }

        try
        {
            // since this code is run via a executor, there is no authenticated user, so we need to specify the user to
            // be used for CMIS connections. Some changes can trigger Mule flows.
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, "DATA_UPDATE");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, "localhost");

            // find all user folders and set the user as participant allowed to write
            List<AcmContainer> containers = getContainerDao().findByObjectType(UserOrgConstants.OBJECT_TYPE);

            for (AcmContainer container : containers)
            {
                // get a copy of the original participants
                List<AcmParticipant> participants = container.getFolder().getParticipants().stream().collect(Collectors.toList());

                AcmParticipant participant = new AcmParticipant();
                participant.setParticipantLdapId(container.getCreator());
                participant.setParticipantType("write");
                participant.setReplaceChildrenParticipant(true);

                participants.add(participant);

                getFileParticipantService().setFolderParticipants(container.getFolder().getId(), participants);
            }

            log.info("Finished updating User's ROOT folder participants!");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

    public AcmContainerDao getContainerDao()
    {
        return containerDao;
    }

    public void setContainerDao(AcmContainerDao containerDao)
    {
        this.containerDao = containerDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
