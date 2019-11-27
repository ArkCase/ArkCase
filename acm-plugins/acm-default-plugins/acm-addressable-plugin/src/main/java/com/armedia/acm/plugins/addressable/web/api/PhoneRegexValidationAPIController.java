package com.armedia.acm.plugins.addressable.web.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.armedia.acm.plugins.addressable.service.PhoneRegexConfig;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/latest/plugin")
public class PhoneRegexValidationAPIController
{

    private PhoneRegexConfig phoneRegexConfig;

    @ResponseBody
    @RequestMapping(value = "/phone/regex", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getPhoneRegex()
    {
       return phoneRegexConfig.getPhoneRegex();
    }

    public PhoneRegexConfig getPhoneRegexConfig() {
        return phoneRegexConfig;
    }

    public void setPhoneRegexConfig(PhoneRegexConfig phoneRegexConfig) {
        this.phoneRegexConfig = phoneRegexConfig;
    }
}
