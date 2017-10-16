package com.armedia.acm.services.email.service;

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
     * @param message          received email
     * @param parentObjectId   parent object id which files belong to
     * @param parentObjectType parent object type which files belong to
     * @param containingFolder folder under which files will be uploaded, if null than root folder is used
     * @param auth             authentication
     */
    void extractAttachmentsAndUpload(Message message, Long parentObjectId, String parentObjectType, AcmFolder containingFolder, Authentication auth);
}
