package com.armedia.acm.services.protecturl.service;

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

import com.armedia.acm.services.protecturl.dao.ProtectedUrlDao;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.NoResultException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for manipulating ProtectedUrl's
 * <p>
 * Created by nebojsha on 27.07.2016.
 */
public class ProtectUrlServiceImpl implements ProtectUrlService
{
    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    /**
     * ProtectedUrlDao instance.
     */
    private ProtectedUrlDao protectedUrlDao;

    /**
     * generates protected url object for given url and save that object to database
     *
     * @param realUrl
     * @return protected url object
     */
    @Override
    public ProtectedUrl protectUrl(String realUrl)
    {
        return protectUrl(realUrl, LocalDateTime.now(ZoneId.of("UTC")), null);
    }

    /**
     * generates protected url object for given url and save that object to database
     *
     * @param realUrl
     * @param validFrom
     * @param validTo
     * @return protected url object
     */
    @Override
    public ProtectedUrl protectUrl(String realUrl, LocalDateTime validFrom, LocalDateTime validTo)
    {
        Objects.requireNonNull(realUrl, "Url must not be null.");
        ProtectedUrl pUrl = new ProtectedUrl();
        UUID uuid = UUID.randomUUID();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        String obfuscatedUrlEncoded = base64Encoder.encodeToString(uuid.toString().getBytes());

        pUrl.setObfuscatedUrl(obfuscatedUrlEncoded);
        pUrl.setOriginalUrl(realUrl);
        pUrl.setValidFrom(validFrom);
        pUrl.setValidTo(validTo);

        // save to database and return created protected url object
        ProtectedUrl protectedUrl = protectedUrlDao.save(pUrl);
        log.debug("Created protected url: [{}]", protectedUrl);
        return protectedUrl;
    }

    /**
     * retrieves saved protected url for given obfuscatedUrl as attribute. If not found returns null.
     *
     * @param obfuscatedUrl
     *            String obfuscatedUrl
     * @return ProtectedUrl if found, otherwise null.
     */
    @Override
    public ProtectedUrl getProtectedUrl(String obfuscatedUrl)
    {
        Objects.requireNonNull(obfuscatedUrl, "Url must not be null.");
        try
        {
            return protectedUrlDao.findByObfuscatedUrl(obfuscatedUrl);
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    /**
     * retrieves saved list of protected url for given originalUrl as attribute
     *
     * @param originalUrl
     *            String obfuscatedUrl
     * @return List<ProtectedUrl>
     */
    @Override
    public List<ProtectedUrl> getProtectedUrlByOriginalUrl(String originalUrl)
    {
        Objects.requireNonNull(originalUrl, "Url must not be null.");
        return protectedUrlDao.findByOriginalUrl(originalUrl);
    }

    /**
     * removes from database expired urls, i.e. ones that have value for validTo, and that value is before today(now)
     */
    @Override
    public void removeExpired()
    {
        int removedCount = protectedUrlDao.removeExpired();
        log.debug("[{}] urls removed that has being expired.", removedCount);
    }

    public void setProtectedUrlDao(ProtectedUrlDao protectedUrlDao)
    {
        this.protectedUrlDao = protectedUrlDao;
    }
}
