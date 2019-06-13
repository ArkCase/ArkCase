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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

/**
 * Created by nebojsha on 31.07.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-data-source.xml",
        "/spring/spring-library-protect-url.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-protect-url-test.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml"
})
public class ProtectUrlServiceImplIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    @Autowired
    ProtectUrlService protectUrlService;
    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Transactional
    @Test
    @Rollback
    public void protectUrlTest()
    {
        auditAdapter.setUserId("some_user");
        assertNotNull(protectUrlService);
        String realUrl = "/login";
        ProtectedUrl pu = protectUrlService.protectUrl(realUrl);
        entityManager.flush();
        assertEquals(pu.getOriginalUrl(), realUrl);
        assertNotNull(pu.getId());
        assertNotNull(pu.getObfuscatedUrl());
        log.info("created protectedUrl object: {}", pu);
    }

    @Transactional
    @Test
    @Rollback
    public void findByOriginalUrlTest()
    {
        auditAdapter.setUserId("some_user");
        assertNotNull(protectUrlService);
        String realUrl = "/" + UUID.randomUUID().toString();
        ProtectedUrl pu = protectUrlService.protectUrl(realUrl);
        entityManager.flush();
        List<ProtectedUrl> saved = protectUrlService.getProtectedUrlByOriginalUrl(realUrl);
        assertEquals(1, saved.size());
        assertEquals(pu.getOriginalUrl(), saved.get(0).getOriginalUrl());
        assertEquals(pu.getObfuscatedUrl(), saved.get(0).getObfuscatedUrl());
        assertEquals(pu.getId(), saved.get(0).getId());
    }
}
