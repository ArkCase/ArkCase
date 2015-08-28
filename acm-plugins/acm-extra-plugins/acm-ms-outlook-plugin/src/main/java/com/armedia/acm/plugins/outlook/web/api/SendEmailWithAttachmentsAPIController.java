package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.dao.impl.ExchangeWebServicesOutlookDao;
import com.armedia.acm.service.outlook.exception.AcmOutlookSendEmailWithAttachmentsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.InputStream;

@Controller
@RequestMapping({"/api/v1/plugin/outlook/email", "/api/latest/plugin/outlook/email"})
public class SendEmailWithAttachmentsAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());
    private OutlookService outlookService;
    private UserOrgService userOrgService;
    private ExchangeWebServicesOutlookDao dao;
    private EcmFileService ecmFileService;

    @RequestMapping(value = "/withattachments",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailWithAttachmentsDTO createEmailWithAttachments(
            @RequestBody EmailWithAttachmentsDTO in,
            Authentication authentication,
            HttpSession session) throws AcmEncryptionException, AcmOutlookSendEmailWithAttachmentsFailedException, AcmUserActionFailedException {

        if(null == in){
            throw new AcmOutlookSendEmailWithAttachmentsFailedException("Could not create email message, invalid input : " + in);
        }
        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        OutlookDTO outlookDTO = getUserOrgService().retrieveOutlookPassword(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());

        try{
            getOutlookService().sendEmailWithAttachments(in,outlookUser);
        }
        catch(Exception e){
            throw new AcmOutlookSendEmailWithAttachmentsFailedException("Could not send emails with attachment,among other things check your request body. Exception message is : " + e.getMessage(), e);
        }

        return in;
    }

    public UserOrgService getUserOrgService() {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService) {
        this.userOrgService = userOrgService;
    }

    public OutlookService getOutlookService() {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService) {
        this.outlookService = outlookService;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }


    public ExchangeWebServicesOutlookDao getDao() {
        return dao;
    }

    public void setDao(ExchangeWebServicesOutlookDao dao) {
        this.dao = dao;
    }

}
