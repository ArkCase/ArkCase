package com.armedia.acm.plugins.addressable.web.api;

import com.armedia.acm.plugins.addressable.service.EmailRegexConfig;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin", "/api/latest/plugin" })
public class EmailRegexAPIController {

    private EmailRegexConfig emailRegexConfig;

    @ResponseBody
    @RequestMapping(value = "/email/regex", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getEmailRegex() {
        return emailRegexConfig.getEmailRegex();
    }

    public EmailRegexConfig getEmailRegexConfig() {
        return emailRegexConfig;
    }

    public void setEmailRegexConfig(EmailRegexConfig emailRegexConfig) {
        this.emailRegexConfig = emailRegexConfig;
    }
}
