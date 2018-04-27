package com.armedia.acm.services.wopi.api;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.wopi.model.WopiUserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/api/latest/wopi")
public class WopiUserApiController
{
    private static final Logger log = LoggerFactory.getLogger(WopiUserApiController.class);

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WopiUserInfo getUserInfo(@RequestParam("acm_ticket") String token, HttpSession session)
    {
        log.info("Getting user info per token [{}]", token);
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        return new WopiUserInfo(user.getFullName(), user.getUserId(), user.getLang());
    }
}
