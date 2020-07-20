package gov.foia.listener;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.portalgateway.service.PortalAdminService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import javax.jms.JMSException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import gov.foia.broker.FOIARequestFileBrokerClient;
import gov.foia.model.FOIAFile;

public class FOIAFileUpdatedEventListener implements ApplicationListener<EcmFileUpdatedEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private FOIARequestFileBrokerClient foiaRequestFileBrokerClient;
    private EcmFileService ecmFileService;
    private AcmParticipantService acmParticipantService;
    private CaseFileDao caseFileDao;
    private PortalAdminService portalAdminService;

    @Override
    public void onApplicationEvent(EcmFileUpdatedEvent ecmFileUpdatedEvent)
    {

        EcmFile updatedEcmFile;
        EcmFile oldEcmFile;

        updatedEcmFile = Objects.nonNull(ecmFileUpdatedEvent.getSource()) ? (EcmFile) ecmFileUpdatedEvent.getSource() : null;
        oldEcmFile = Objects.nonNull(ecmFileUpdatedEvent.getEventProperties())
                ? (EcmFile) ecmFileUpdatedEvent.getEventProperties().getOrDefault("oldEcmFile", null)
                : null;

        if (Objects.nonNull(updatedEcmFile) && Objects.nonNull(oldEcmFile))
        {
            if (updatedEcmFile instanceof FOIAFile && oldEcmFile instanceof FOIAFile)
            {
                FOIAFile updatedFoiaFile = (FOIAFile) updatedEcmFile;
                FOIAFile oldFoiaFile = (FOIAFile) oldEcmFile;
                if (oldFoiaFile.getPublicFlag() == false && updatedFoiaFile.getPublicFlag() == true)
                {
                    publishEcmFile(updatedFoiaFile);
                    addPortalUserAsReader(updatedFoiaFile);
                }

            }
        }
    }

    private void publishEcmFile(EcmFile ecmFile)
    {
        File tempFile = null;
        InputStream ecmFileInputStream = null;
        OutputStream ecmFileOutputStream = null;

        try
        {
            String currDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMdd-HHmmss-SSS"));
            tempFile = File.createTempFile(ecmFile.getFileName() + "-" + currDateTime, ecmFile.getFileActiveVersionNameExtension());

            ecmFileOutputStream = new FileOutputStream(tempFile);
            ecmFileInputStream = getEcmFileService().downloadAsInputStream(ecmFile.getId());

            IOUtils.copy(ecmFileInputStream, ecmFileOutputStream);

            Map<String, Object> fileProperties = new HashMap<>();

            fileProperties.put("fileId", ecmFile.getId());
            fileProperties.put("fileName", ecmFile.getFileName().concat(ecmFile.getFileActiveVersionNameExtension()));
            getFoiaRequestFileBrokerClient().sendFile(tempFile, fileProperties);
        }
        catch (AcmUserActionFailedException | IOException | JMSException e)
        {
            log.error("Could not publish Ecm File", e);
        }
        finally
        {
            IOUtils.closeQuietly(ecmFileInputStream);
            IOUtils.closeQuietly(ecmFileOutputStream);
            FileUtils.deleteQuietly(tempFile);
        }
    }

    private void addPortalUserAsReader(EcmFile ecmFile)
    {

        if (getPortalAdminService().listRegisteredPortals() != null && !getPortalAdminService().listRegisteredPortals().isEmpty())
        {
            AcmUser portalUser = getPortalAdminService().listRegisteredPortals().get(0).getUser();
            if (portalUser != null)
            {
                CaseFile caseFile = getCaseFileDao().find(ecmFile.getParentObjectId());
                try
                {
                    if (caseFile != null)
                    {
                        boolean isPortalUserParticipant = caseFile.getParticipants().stream()
                                .anyMatch(
                                        p -> ParticipantTypes.READER.equals(p.getParticipantType())
                                                && p.getParticipantLdapId().equals(portalUser.getUserId()));

                        if (!isPortalUserParticipant)
                        {
                            AcmParticipant readerParticipant = getAcmParticipantService().saveParticipant(portalUser.getUserId(),
                                    ParticipantTypes.READER, caseFile.getId(), caseFile.getObjectType());
                            caseFile.getParticipants().add(readerParticipant);
                            log.debug("Successfully set portal user as participant for case file: [{}]", caseFile.getId());
                        }
                    }
                }
                catch (AcmAccessControlException e)
                {
                    log.error("Unable to set portal user as participant for case file: [{}]", caseFile.getId());
                }
            }
        }
    }

    public FOIARequestFileBrokerClient getFoiaRequestFileBrokerClient()
    {
        return foiaRequestFileBrokerClient;
    }

    public void setFoiaRequestFileBrokerClient(FOIARequestFileBrokerClient foiaRequestFileBrokerClient)
    {
        this.foiaRequestFileBrokerClient = foiaRequestFileBrokerClient;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AcmParticipantService getAcmParticipantService()
    {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService)
    {
        this.acmParticipantService = acmParticipantService;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public PortalAdminService getPortalAdminService()
    {
        return portalAdminService;
    }

    public void setPortalAdminService(PortalAdminService portalAdminService)
    {
        this.portalAdminService = portalAdminService;
    }
}
