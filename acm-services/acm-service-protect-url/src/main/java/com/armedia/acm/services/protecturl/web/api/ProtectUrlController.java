package com.armedia.acm.services.protecturl.web.api;

import com.armedia.acm.services.protecturl.exception.AcmExpiredException;
import com.armedia.acm.services.protecturl.exception.AcmNotAccessibleYet;
import com.armedia.acm.services.protecturl.exception.AcmUrlNotFoundException;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;
import com.armedia.acm.services.protecturl.service.ProtectUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

/**
 *
 * serves forwards into real url for given obfuscated url.
 *
 * Created by nebojsha on 28.07.2016.
 */
@Controller
public class ProtectUrlController
{
    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * ProtectUrlService instance
     */
    private ProtectUrlService protectUrlService;

    @RequestMapping({"/protected/{protectedUrl}"})
    public String forwardRequest(@PathVariable(value = "protectedUrl") String protectedUrlStr)
    {
        ProtectedUrl protectedUrl = protectUrlService.getProtectedUrl(protectedUrlStr);

        if (protectedUrl == null)
        {

            log.warn("protected url doesn't exists with following obfuscatedUrl: [{}]", protectedUrlStr);
            throw new AcmUrlNotFoundException("protected url doesn't exists with following obfuscatedUrl: [" + protectedUrlStr + "]");
        }

        log.debug("Trying to access protected url:[{}] with real url:[{}]", protectedUrlStr, protectedUrl.getOriginalUrl());
        LocalDateTime now = LocalDateTime.now();

        //verify that url has not being expired
        if (protectedUrl.getValidTo() != null && now.isAfter(protectedUrl.getValidTo()))
        {
            log.debug("url has expired. Today is [{}] but url has expired on: [{}]", now, protectedUrl.getValidTo());
            throw new AcmExpiredException("url has being expired.");
        }

        //verify that url can be accessed now
        if (now.isBefore(protectedUrl.getValidFrom()))
        {
            log.debug("url is not valid yet. Today is [{}] but will be valid on: [{}]", now, protectedUrl.getValidFrom());
            throw new AcmNotAccessibleYet("Url is not accessible yet.");
        }

        return "forward:" + protectedUrl.getOriginalUrl();
    }

    public void setProtectUrlService(ProtectUrlService protectUrlService)
    {
        this.protectUrlService = protectUrlService;
    }
}
