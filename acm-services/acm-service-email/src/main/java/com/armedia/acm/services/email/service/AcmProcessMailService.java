package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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

import com.armedia.acm.plugins.ecm.model.AcmFolder;

import org.springframework.security.core.Authentication;

import javax.mail.Message;

/**
 * Service for handling received emails.
 *
 * Created by nebojsha.davidovikj on 1/20/2017.
 */
public interface AcmProcessMailService
{
    /**
     * iterates through received email and extract attachments and uploads using EcmFileService
     *
     * @param message
     *            received email
     * @param parentObjectId
     *            parent object id which files belong to
     * @param parentObjectType
     *            parent object type which files belong to
     * @param containingFolder
     *            folder under which files will be uploaded, if null than root folder is used
     * @param auth
     *            authentication
     */
    void extractAttachmentsAndUpload(Message message, Long parentObjectId, String parentObjectType, AcmFolder containingFolder,
            Authentication auth);
}
