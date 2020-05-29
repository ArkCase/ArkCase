package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationRule;

import java.util.Date;

public interface NotificationService
{
    void run(Date lastRun);

    void runRule(Date lastRun, NotificationRule rule);

    Notification createNotification(String templateModel, String title, String parentType, Long parentId, String parentName,
                                    String parentTitle, String emailAddresses, String user);

    Notification createNotification(String templateModel, String title, String parentType, Long parentId, String parentName,
            String parentTitle, String emailAddresses, String user, String relatedUser);

    Notification createNotification(String templateModel, String title, String parentType, Long parentId, String parentName,
                                    String parentTitle, String emailAddresses, String user, String relatedUser, String note);

    Notification createNotification(String templateModel, String title, String parentType, Long parentId, String parentName,
            String parentTitle, Long relatedObjectId, String relatedObjectType, String relatedObjectName, String emailAddresses,
            String user, String relatedUser, String note);
}
