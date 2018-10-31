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

import static gov.foia.model.FOIAConstants.EMAIL_RELEASE_BODY;
import static gov.foia.model.FOIAConstants.EMAIL_RELEASE_SUBJECT;
import static gov.foia.model.FOIAConstants.PORTAL_REQUEST_STATUS_RELATIVE_URL;
import static gov.foia.model.FOIARequestUtils.extractRequestorEmailAddress;

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.foia.model.FOIARequest;

/**
 * Created by teng.wang on 3/31/2017.
 */
public class ResponseFolderNotifyService
{
    private CaseFileDao caseFileDao;
    private FolderCompressor compressor;
    private ResponseFolderService responseFolderService;
    private NotificationSender notificationSender;
    private UserDao userDao;
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmApplication acmAppConfiguration;

    /**
     * @return the acmAppConfiguration
     */
    public AcmApplication getAcmAppConfiguration()
    {
        return acmAppConfiguration;
    }

    /**
     * @param acmAppConfiguration
     *            the acmAppConfiguration to set
     */
    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public void sendEmailNotification(Long requestId)
    {
        FOIARequest request = (FOIARequest) caseFileDao.find(requestId);
        String emailAddress = extractRequestorEmailAddress(request.getOriginator().getPerson());
        if (emailAddress != null)
        {
            Notification releaseNotifier = new Notification();
            releaseNotifier.setUserEmail(emailAddress);
            releaseNotifier.setTitle(String.format("%s %s", EMAIL_RELEASE_SUBJECT, request.getCaseNumber()));
            String link = String.format(PORTAL_REQUEST_STATUS_RELATIVE_URL, request.getCaseNumber());
            releaseNotifier.setNote(
                    String.format(EMAIL_RELEASE_BODY, request.getRequestType(), request.getCaseNumber(),
                            getAcmAppConfiguration().getBaseUrl() + link));
            getNotificationSender().send(releaseNotifier);
        }
    }

    public FolderCompressor getCompressor()
    {
        return compressor;
    }

    public void setCompressor(FolderCompressor compressor)
    {
        this.compressor = compressor;
    }

    /**
     * @return the responseFolderService
     */
    public ResponseFolderService getResponseFolderService()
    {
        return responseFolderService;
    }

    /**
     * @param responseFolderService
     *            the responseFolderService to set
     */
    public void setResponseFolderService(ResponseFolderService responseFolderService)
    {
        this.responseFolderService = responseFolderService;
    }

    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
