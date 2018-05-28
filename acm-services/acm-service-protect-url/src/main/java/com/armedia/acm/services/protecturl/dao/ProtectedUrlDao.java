package com.armedia.acm.services.protecturl.dao;

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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;

import javax.persistence.Query;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by nebojsha on 27.07.2016.
 */
public class ProtectedUrlDao extends AcmAbstractDao<ProtectedUrl>
{
    @Override
    protected Class<ProtectedUrl> getPersistenceClass()
    {
        return ProtectedUrl.class;
    }

    /**
     * returns ProtectedUrl if found for given parameter of obfuscatedUrl, if not found throws NoResultException
     *
     * @param obfuscatedUrl
     *            String obfuscatedUrl
     * @return instance of ProtectedUrl
     */
    public ProtectedUrl findByObfuscatedUrl(String obfuscatedUrl)
    {
        Query protectedUrlQuery = getEm().createQuery("SELECT pu FROM ProtectedUrl pu WHERE pu.obfuscatedUrl=:obfuscatedUrl");
        protectedUrlQuery.setParameter("obfuscatedUrl", obfuscatedUrl);
        return (ProtectedUrl) protectedUrlQuery.getSingleResult();
    }

    /**
     * returns list of ProtectedUrl if found for given parameter of originalUrl, if not found throws NoResultException
     *
     * @param originalUrl
     *            String originalUrl
     * @return instance of List<ProtectedUrl>
     */
    public List<ProtectedUrl> findByOriginalUrl(String originalUrl)
    {
        Query protectedUrlQuery = getEm().createQuery("SELECT pu FROM ProtectedUrl pu WHERE pu.originalUrl=:originalUrl");
        protectedUrlQuery.setParameter("originalUrl", originalUrl);
        return protectedUrlQuery.getResultList();
    }

    /**
     * removes from database expired urls, i.e. ones that have value for validTo, and that value is before today(now)
     */
    public int removeExpired()
    {
        Query query = getEm().createQuery("DELETE FROM ProtectedUrl pu WHERE pu.validTo < :givenDate");
        query.setParameter("givenDate", LocalDateTime.now(ZoneId.of("UTC")));
        return query.executeUpdate();
    }
}
