package com.armedia.acm.services.protecturl.service;

import com.armedia.acm.services.protecturl.model.ProtectedUrl;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
     * @param obfuscatedUrl String obfuscatedUrl
     * @return ProtectedUrl if found, otherwise null.
     */
    ProtectedUrl getProtectUrl(String obfuscatedUrl);

    /**
     * removes from database expired urls, i.e. ones that have value for validTo, and that value is before today(now)
     *
     */
    @Transactional
    void removeExpired();
}
