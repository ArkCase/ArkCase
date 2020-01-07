package com.armedia.acm.plugins.billing.service;

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

import com.armedia.acm.core.AcmObjectNumber;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.model.AcmObjectOriginator;
import com.armedia.acm.plugins.person.model.ExtractPersonInfoUtils;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.model.BillingInvoiceRequest;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BillingInvoiceEmailSenderService<T extends AcmContainerEntity & AcmObjectOriginator & AcmObjectNumber>
{
    private final Logger log = LogManager.getLogger(this.getClass());
    private T parentObject;
    private BillingService billingService;
    private NotificationSender notificationSender;
    private NotificationDao notificationDao;
    private EcmFileService fileService;

    public void sendBillingInvoiceByEmail(BillingInvoiceRequest billingInvoiceRequest, AcmUser acmUser, Authentication authentication)
            throws Exception
    {
        String emailAddress = ExtractPersonInfoUtils.extractRequestorEmailAddress(parentObject.getAcmObjectOriginator().getPerson());
        BillingInvoice billingInvoice = getBillingService().getLatestBillingInvoice(billingInvoiceRequest.getParentObjectType(),
                billingInvoiceRequest.getParentObjectId());

        StringBuilder notificationTitle;
        notificationTitle = billingInvoiceRequest.getObjectSubtype() != null ?
                new StringBuilder(String.format(billingInvoiceRequest.getObjectSubtype())) :
                new StringBuilder(String.format(billingInvoice.getParentObjectType()));

        notificationTitle.append(": ");
        notificationTitle.append(parentObject.getAcmObjectNumber());
        notificationTitle.append(" Invoice");

        List<EcmFileVersion> notificationFiles = new ArrayList<>();
        List<Long> filesIds = new ArrayList<>();
        if (billingInvoice.getBillingInvoiceEcmFile() != null)
        {
            filesIds = Arrays.asList(billingInvoice.getBillingInvoiceEcmFile().getFileId());
        }
        EcmFile file;
        for (Long fileId : filesIds)
        {
            file = getFileService().findById(fileId);
            notificationFiles.add(file.getVersions().get(file.getVersions().size() - 1));
        }

        Notification notification = new Notification();
        notification.setTemplateModelName("billingInvoice");
        notification.setParentType(billingInvoice.getParentObjectType());
        notification.setParentId(billingInvoice.getParentObjectId());
        notification.setAttachFiles(true);
        notification.setFiles(notificationFiles);
        notification.setEmailAddresses(emailAddress);
        notification.setTitle(notificationTitle.toString());
        notification.setUser(acmUser.getUserId());
        getNotificationDao().save(notification);

    }

    public BillingService getBillingService()
    {
        return billingService;
    }

    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }

    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public void setParentObject(T parentObject) {
        this.parentObject = parentObject;
    }
}
