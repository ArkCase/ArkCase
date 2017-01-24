package com.armedia.acm.services.email.service;

import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by nebojsha.davidovikj on 1/20/2017.
 */
public class AcmProcessMailServiceImpl implements AcmProcessMailService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;

    /**
     * iterates through received email and extract attachments and uploads using EcmFileService
     *
     * @param message          received email
     * @param parentObjectId   parent object id which files belong to
     * @param parentObjectType parent object type which files belong to
     * @param containingFolder folder under which files will be uploaded, if null than root folder is used
     * @param auth             authentication
     */
    @Override
    public void extractAttachmentsAndUpload(Message message, Long parentObjectId, String parentObjectType, AcmFolder containingFolder, Authentication auth)
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        String bodyFileName = parentObjectId + "_" + parentObjectType + ".eml";
        File messageFile = new File(tempDir + File.separator + UUID.randomUUID().toString() + "_" + bodyFileName);

        try
        {
            try (OutputStream os = new FileOutputStream(messageFile))
            {
                message.writeTo(os);
            }

            //if no containing folder is provided than root folder is used
            AcmFolder folder = containingFolder != null ? containingFolder : acmFolderService.getRootFolder(parentObjectId, parentObjectType);

            //upload message body
            try (InputStream bodyIS = new FileInputStream(messageFile))
            {
                ecmFileService.upload(bodyFileName, "mail", "Document",
                        bodyIS, "message/rfc822", bodyFileName, auth,
                        folder.getCmisFolderId(), parentObjectType, parentObjectId);
                log.debug("email body uploaded as eml to object an folder [{}, {}, {}, {}]", bodyFileName, folder.getCmisFolderId(), parentObjectId, parentObjectType);
            } catch (Exception e)
            {
                //log error and process attachments
                log.error("email body WAS NOT uploaded as eml [{}, {}, {}, {}]", bodyFileName, folder.getCmisFolderId(), parentObjectId, parentObjectType, e);
            } finally
            {
                //delete temp file
                messageFile.delete();
            }


            //find attachments and upload
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++)
            {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
                        !StringUtils.isNotBlank(bodyPart.getFileName()))
                {
                    continue; // dealing with attachments only
                }

                try (InputStream attachmentIS = bodyPart.getInputStream())
                {
                    String fileName = bodyPart.getFileName();
                    ecmFileService.upload(fileName, "attachment", "Document",
                            attachmentIS, "message/rfc822", fileName, auth,
                            folder.getCmisFolderId(), parentObjectType, parentObjectId);
                    log.debug("email attachment uploaded [{}, {}, {}, {}]", fileName, folder.getCmisFolderId(), parentObjectId, parentObjectType);
                } catch (Exception e)
                {
                    //report error and try to process other attachments
                    log.error("failed to upload attachment [{}, {}, {}, {}]", bodyFileName, folder.getCmisFolderId(), parentObjectId, parentObjectType, e);
                }
            }
        } catch (Exception e)
        {
            throw new RuntimeException("Error extracting and uploading attachments of email.", e);
        }
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }
}
