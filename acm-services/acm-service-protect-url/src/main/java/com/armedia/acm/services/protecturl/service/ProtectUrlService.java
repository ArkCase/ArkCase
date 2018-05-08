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

import com.armedia.acm.services.protecturl.model.ProtectedUrl;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for manipulating ProtectedUrl's
 * <p>
 * Created by nebojsha on 27.07.2016.
 */
public interface ProtectUrlService
{
    /**
     * generates protected url object for given url and save that object to database
     *
     * @param realUrl
     * @return protected url object
     */
    ProtectedUrl protectUrl(String realUrl);

    /**
     * generates protected url object for given url and save that object to database
     *
     * @param realUrl
     * @param validFrom
     * @param validTo
     * @return protected url object
     */
    ProtectedUrl protectUrl(String realUrl, LocalDateTime validFrom, LocalDateTime validTo);

    /**
     * retrieves saved protected url for given obfuscatedUrl as attribute. If not found returns null.
     *
     * @param obfuscatedUrl
     *            String obfuscatedUrl
     * @return ProtectedUrl if found, otherwise null.
     */
    ProtectedUrl getProtectedUrl(String obfuscatedUrl);

    /**
     * retrieves saved list of protected url for given originalUrl as attribute
     *
     * @param originalUrl
     *            String obfuscatedUrl
     * @return List<ProtectedUrl>
     */
    List<ProtectedUrl> getProtectedUrlByOriginalUrl(String originalUrl);

    /**
     * removes from database expired urls, i.e. ones that have value for validTo, and that value is before today(now)
     */
    @Transactional
    void removeExpired();
}
