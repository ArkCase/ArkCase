package gov.foia.service;

import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public abstract class AbstractEmailSenderService
{
    private transient final Logger log = LoggerFactory.getLogger(this.getClass());
    private AcmEmailSenderService emailSenderService;
    private String emailTemplate;

    public void sendEmailWithAttachment(List<String> emailAddresses,
                                        String subject,
                                        Map<String, Object> templateDataModel,
                                        List<Long> attachmentIds,
                                        AcmUser user,
                                        Authentication authentication) throws Exception
    {
        EmailWithAttachmentsDTO emailWithAttachmentsDTO = new EmailWithAttachmentsDTO();
        emailWithAttachmentsDTO.setSubject(subject);
        emailWithAttachmentsDTO.setHeader("");
        emailWithAttachmentsDTO.setFooter("");
        emailWithAttachmentsDTO.setBody(buildEmailBodyFromTemplate(templateDataModel));
        emailWithAttachmentsDTO.setAttachmentIds(attachmentIds);
        emailWithAttachmentsDTO.setEmailAddresses(emailAddresses);


        log.info(String.format("Sending an email with subject [%s]", subject));
        getEmailSenderService().sendEmailWithAttachments(emailWithAttachmentsDTO,authentication,user);
    }


    private String buildEmailBodyFromTemplate(Map<String, Object> model)
    {
        String emailTemplateUpdated = new String(getEmailTemplate());
        for(Map.Entry entry : model.entrySet())
        {
            emailTemplateUpdated = emailTemplateUpdated.replace("${model." + entry.getKey() + "}", entry.getValue() !=null ? entry.getValue().toString() : "");
        }
        return emailTemplateUpdated;
    }

    public AcmEmailSenderService getEmailSenderService()
    {
        return emailSenderService;
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public String getEmailTemplate()
    {
        return emailTemplate;
    }

    public void setEmailTemplate(Resource emailTemplate) throws IOException
    {
        try(DataInputStream resourceStream = new DataInputStream(emailTemplate.getInputStream()))
        {
            byte[] bytes = new byte[resourceStream.available()];
            resourceStream.readFully(bytes);
            this.emailTemplate = new String(bytes, Charset.forName("UTF-8"));
        }
    }
}
