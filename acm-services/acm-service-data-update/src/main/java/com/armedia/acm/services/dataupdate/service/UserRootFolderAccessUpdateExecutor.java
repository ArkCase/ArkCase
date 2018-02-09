package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.profile.model.UserOrgConstants;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserRootFolderAccessUpdateExecutor implements AcmDataUpdateExecutor
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileParticipantService fileParticipantService;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private AcmContainerDao containerDao;

    @Override
    public String getUpdateId()
    {
        return "user-root-folder-access-update";
    }

    @Transactional
    @Override
    public void execute()
    {
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
}
