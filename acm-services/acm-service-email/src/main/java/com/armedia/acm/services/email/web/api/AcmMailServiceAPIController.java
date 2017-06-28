package com.armedia.acm.services.email.web.api;

import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 20, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/email/send", "/api/latest/service/email/send" })
public class AcmMailServiceAPIController
{

    @RequestMapping(value = "/withattachments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailWithAttachmentsDTO createEmailWithAttachments(@RequestBody EmailWithAttachmentsDTO in, Authentication authentication,
            HttpSession session)
    {
        return null;
    }

    @RequestMapping(value = "/withembeddedlinks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmailWithEmbeddedLinksResultDTO> createEmailWithEmbeddedLinks(@RequestBody EmailWithEmbeddedLinksDTO in,
            Authentication authentication, HttpSession session)
    {
        return null;
    }

    @RequestMapping(value = "/withattachmentsandlinks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailWithAttachmentsAndLinksDTO createEmailWithAttachments(@RequestBody EmailWithAttachmentsAndLinksDTO in,
            Authentication authentication, HttpSession session)
    {
        return null;
    }

}
