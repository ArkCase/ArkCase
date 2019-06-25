package gov.foia.service;

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

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.config.lookups.model.NestedLookupEntry;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationGroupEmailSenderService
{
    private final String REQUEST_FORM_TYPE = "Request Form";

    private final transient Logger log = LogManager.getLogger(this.getClass());

    private CaseFileDao caseFileDao;
    private EcmFileDao ecmFileDao;
    private LookupDao lookupDao;
    private NotificationDao notificationDao;

    public void sendRequestEmailToNotificationGroup(Long caseId, String notificationGroupName, AcmUser acmUser,
            Authentication authentication) throws Exception
    {
        List<String> emailAddresses = null;

        CaseFile caseFile = getCaseFileDao().find(caseId);

        List<NestedLookupEntry> notificationLookup = (List<NestedLookupEntry>) getLookupDao().getLookupByName("notificationGroups")
                .getEntries();
        NestedLookupEntry notificationEntry = notificationLookup
                .stream()
                .filter(nestedLookupEntry -> nestedLookupEntry.getKey().equals(notificationGroupName))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(notificationEntry))
        {
            emailAddresses = notificationEntry
                    .getSubLookup()
                    .stream()
                    .map(StandardLookupEntry::getKey)
                    .collect(Collectors.toList());
        }

        AcmFolder requestFolder = caseFile.getContainer().getAttachmentFolder();

        if (Objects.isNull(emailAddresses) || emailAddresses.isEmpty() || Objects.isNull(requestFolder))
        {
            throw new Exception(String.format("Error in send Request Form Document to Notification Group [%s]", notificationGroupName));
        }
        else
        {
            List<EcmFile> requestFiles = getEcmFileDao().findByFolderId(requestFolder.getId());
            EcmFile requestFormFile = requestFiles
                    .stream()
                    .filter(ecmFile -> REQUEST_FORM_TYPE.equals(ecmFile.getFileType()))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(requestFormFile))
            {
                String subject = "Executive Email";

                log.info("Trying to send a Request Form email to Notification Group [%s]", notificationGroupName);

                List<EcmFileVersion> fileVersions = new ArrayList<>();
                fileVersions.add(requestFormFile.getVersions().get(requestFormFile.getVersions().size() - 1));

                Notification notification = new Notification();
                notification.setTemplateModelName("notificationGroup");
                notification.setAttachFiles(true);
                notification.setTitle(subject);
                notification.setFiles(fileVersions);
                notification.setParentType(caseFile.getObjectType());
                notification.setParentId(caseId);
                notification.setParentName(caseFile.getCaseNumber());
                notification.setParentTitle(caseFile.getDetails());
                notification.setUser(acmUser.getUserId());
                notification.setEmailAddresses(emailAddresses.stream().collect(Collectors.joining(",")));
                notificationDao.save(notification);

            }
            else
            {
                throw new FileNotFoundException("Form Data Document Not Found");
            }
        }
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }
}
