package com.armedia.acm.services.protecturl.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;

import javax.persistence.Query;
import java.time.LocalDateTime;
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
     * @param obfuscatedUrl String  obfuscatedUrl
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
     * @param originalUrl String  originalUrl
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
        query.setParameter("givenDate", LocalDateTime.now());
        return query.executeUpdate();
    }
}
