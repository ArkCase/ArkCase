package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.PermissionChangedDTO;
import com.armedia.acm.plugins.ecm.utils.EcmFileParticipantServiceHelper;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import java.util.Date;

/**
 * @author ivana.shekerova on 6/3/2019.
 */
public class ChangedParticipantToArkcaseListener implements MessageListener
{
    private final transient Logger log = LogManager.getLogger(getClass());
    private ObjectConverter objectConverter;
    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmFileParticipantServiceHelper ecmFileParticipantServiceHelper;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void onMessage(Message message)
    {
        if (message instanceof TextMessage)
        {
            try
            {
                String text = ((TextMessage) message).getText();
                PermissionChangedDTO permissionChanged = objectConverter.getJsonUnmarshaller().unmarshall(text, PermissionChangedDTO.class);
                AcmUser acmUser = getEcmFileParticipantServiceHelper().getExternalAuthenticationUtils()
                        .getArkcaseUserByLdapAttributeUserIdValue(permissionChanged.getUser());
                String participantLdapId = acmUser.getUserId();
                String permission = getFolderAndFilesUtils().getArkcasePropertyMapping(permissionChanged.getPermission());

                String modifier = getEcmFileParticipantServiceHelper().getExternalAuthenticationUtils()
                        .getArkcaseUserByLdapAttributeUserIdValue(permissionChanged.getModifier()).getUserId();

                auditPropertyEntityAdapter.setUserId(modifier);

                AcmFolder arkCaseFolder = getFolderAndFilesUtils().lookupArkCaseFolder(permissionChanged.getCmisObjectId());
                if (arkCaseFolder != null)
                {
                    if (permissionChanged.getMethod().equals("deletePermission"))
                    {
                        // remove the participant
                        boolean removed = arkCaseFolder.getParticipants()
                                .removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                                        && participant.getParticipantType().equals(permission));
                        if (removed)
                        {
                            // modify the instance to trigger the Solr transformers
                            arkCaseFolder.setModified(new Date());

                            // save folder with removed participants
                            getEcmFileParticipantServiceHelper().getFolderDao().save(arkCaseFolder);
                        }
                    }
                    else if (permissionChanged.getMethod().equals("setPermission"))
                    {
                        getEcmFileParticipantServiceHelper().setParticipantToFolderWithoutSendingEvent(arkCaseFolder, participantLdapId,
                                permission);
                    }
                }
                else
                {
                    EcmFile arkCaseFile = getFolderAndFilesUtils().lookupArkCaseFile(permissionChanged.getCmisObjectId());
                    if (arkCaseFile != null)
                    {
                        if (permissionChanged.getMethod().equals("deletePermission"))
                        {
                            boolean removed = arkCaseFile.getParticipants()
                                    .removeIf(participant -> participant.getParticipantLdapId().equals(participantLdapId)
                                            && participant.getParticipantType().equals(permission));
                            if (removed)
                            {
                                // modify the instance to trigger the Solr transformers
                                arkCaseFile.setModified(new Date());
                                // save file with removed participants
                                getEcmFileParticipantServiceHelper().getFileDao().save(arkCaseFile);
                            }
                        }
                        else if (permissionChanged.getMethod().equals("setPermission"))
                        {
                            getEcmFileParticipantServiceHelper().setParticipantToFileWithoutSendingEvent(arkCaseFile, participantLdapId,
                                    permission);
                        }
                    }
                    else
                    {
                        log.info("Can't find file or folder with id: {}", permissionChanged.getCmisObjectId());
                    }
                }
            }
            catch (JMSException e)
            {
                log.debug("Couldn't read message from jms.");
            }

        }
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public EcmFileParticipantServiceHelper getEcmFileParticipantServiceHelper()
    {
        return ecmFileParticipantServiceHelper;
    }

    public void setEcmFileParticipantServiceHelper(EcmFileParticipantServiceHelper ecmFileParticipantServiceHelper)
    {
        this.ecmFileParticipantServiceHelper = ecmFileParticipantServiceHelper;
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
