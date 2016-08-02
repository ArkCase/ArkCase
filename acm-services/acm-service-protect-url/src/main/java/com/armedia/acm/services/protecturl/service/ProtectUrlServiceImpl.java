package com.armedia.acm.services.protecturl.service;

import com.armedia.acm.services.protecturl.dao.ProtectedUrlDao;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
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
    private Logger log = LoggerFactory.getLogger(getClass());

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

        //save to database and return created protected url object
        return protectedUrlDao.save(pUrl);
    }

    /**
     * retrieves saved protected url for given obfuscatedUrl as attribute. If not found returns null.
     *
     * @param obfuscatedUrl String obfuscatedUrl
     * @return ProtectedUrl if found, otherwise null.
     */
    @Override
    public ProtectedUrl getProtectUrl(String obfuscatedUrl)
    {
        Objects.requireNonNull(obfuscatedUrl, "Url must not be null.");
        try
        {
            return protectedUrlDao.findByObfuscatedUrl(obfuscatedUrl);
        } catch (NoResultException e)
        {
            return null;
        }
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
