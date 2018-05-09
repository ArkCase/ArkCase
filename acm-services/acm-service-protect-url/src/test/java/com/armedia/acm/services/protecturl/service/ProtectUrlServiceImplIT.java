package com.armedia.acm.services.protecturl.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.protecturl.model.ProtectedUrl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        "/spring/spring-library-protect-url-test.xml"
})
public class ProtectUrlServiceImplIT
{
    @Autowired
    ProtectUrlService protectUrlService;
    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());
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