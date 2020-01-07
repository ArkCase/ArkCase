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
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import gov.foia.model.FoiaConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import gov.foia.model.FOIARequest;

/**
 * Service for notifications to the executive group.
 * 
 * @author bojan.milenkoski
 */
public class FOIAExecutiveGroupNotificationService
{
    private final Logger log = LogManager.getLogger(this.getClass());

    private CaseFileDao caseFileDao;
    private NotificationGroupEmailSenderService notificationGroupEmailSenderService;
    private UserDao userDao;
    private FoiaConfigurationService foiaConfigurationService;

    public void sendFulfillEmailNotification(Long requestId) throws Exception
    {
        FOIARequest request = (FOIARequest) caseFileDao.find(requestId);
        if(getFoiaConfigurationService().readConfiguration().getNotificationGroupsEnabled())
        {
            String notificationGroup = request.getNotificationGroup();

            if (request.getQueue().getName().equals("Fulfill") && request.getPreviousQueue().getName().equals("Intake")
                    && !StringUtils.isEmpty(notificationGroup)) {

                log.info(String.format("Sending email to notification group [%s]", notificationGroup));
                AcmUser user = userDao.findByUserId(request.getModifier());

                try {
                    getNotificationGroupEmailSenderService().sendRequestEmailToNotificationGroup(requestId, notificationGroup, user,
                            SecurityContextHolder.getContext().getAuthentication());
                } catch (Exception e) {
                    throw new Exception(String.format("Could not send Request Form Document to Notification Group [%s]", notificationGroup), e);
                }
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

    public NotificationGroupEmailSenderService getNotificationGroupEmailSenderService()
    {
        return notificationGroupEmailSenderService;
    }

    public void setNotificationGroupEmailSenderService(NotificationGroupEmailSenderService notificationGroupEmailSenderService)
    {
        this.notificationGroupEmailSenderService = notificationGroupEmailSenderService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public FoiaConfigurationService getFoiaConfigurationService()
    {
        return foiaConfigurationService;
    }

    public void setFoiaConfigurationService(FoiaConfigurationService foiaConfigurationService)
    {
        this.foiaConfigurationService = foiaConfigurationService;
    }


}
