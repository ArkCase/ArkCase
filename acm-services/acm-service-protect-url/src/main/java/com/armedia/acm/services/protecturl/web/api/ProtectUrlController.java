package com.armedia.acm.services.protecturl.web.api;

/*-
 * #%L
 * acm-protect-url
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

import com.armedia.acm.services.protecturl.exception.AcmExpiredException;
import com.armedia.acm.services.protecturl.exception.AcmNotAccessibleYet;
import com.armedia.acm.services.protecturl.exception.AcmUrlNotFoundException;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;
import com.armedia.acm.services.protecturl.service.ProtectUrlService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
    private Logger log = LogManager.getLogger(getClass());

    /**
     * ProtectUrlService instance
     */
    private ProtectUrlService protectUrlService;

    @RequestMapping({ "/protected/{protectedUrl}" })
    public String forwardRequest(@PathVariable(value = "protectedUrl") String protectedUrlStr)
    {
        ProtectedUrl protectedUrl = protectUrlService.getProtectedUrl(protectedUrlStr);

        if (protectedUrl == null)
        {

            log.warn("protected url doesn't exists with following obfuscatedUrl: [{}]", protectedUrlStr);
            throw new AcmUrlNotFoundException("protected url doesn't exists with following obfuscatedUrl: [" + protectedUrlStr + "]");
        }

        log.debug("Trying to access protected url:[{}] with real url:[{}]", protectedUrlStr, protectedUrl.getOriginalUrl());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        // verify that url has not being expired
        if (protectedUrl.getValidTo() != null && now.isAfter(protectedUrl.getValidTo()))
        {
            log.debug("url has expired. Today is [{}] but url has expired on: [{}]", now, protectedUrl.getValidTo());
            throw new AcmExpiredException("url has being expired.");
        }

        // verify that url can be accessed now
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
