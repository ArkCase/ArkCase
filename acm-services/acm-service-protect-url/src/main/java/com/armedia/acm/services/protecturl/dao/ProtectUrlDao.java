package com.armedia.acm.services.protecturl.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;

import javax.persistence.Query;
import java.time.LocalDateTime;

/**
 * Created by nebojsha on 27.07.2016.
 */
public class ProtectUrlDao extends AcmAbstractDao<ProtectedUrl>
{
    @Override
    protected Class<ProtectedUrl> getPersistenceClass()
    {
        return ProtectedUrl.class;
    }

    public ProtectedUrl findByObfuscatedUrl(String obfuscatedUrl)
    {
        Query protectedUrlQuery = getEm().createQuery("SELECT pu FROM ProtectedUrl pu WHERE pu.obfuscatedUrl=:obfuscatedUrl");
        protectedUrlQuery.setParameter("obfuscatedUrl", obfuscatedUrl);
        return (ProtectedUrl) protectedUrlQuery.getSingleResult();
    }

    public int removeBeforeDate(LocalDateTime givenDate)
    {
        Query query = getEm().createQuery("DELETE FROM ProtectedUrl pu WHERE pu.validTo < :givenDate");
        query.setParameter("givenDate", givenDate);
        return query.executeUpdate();
    }
}
