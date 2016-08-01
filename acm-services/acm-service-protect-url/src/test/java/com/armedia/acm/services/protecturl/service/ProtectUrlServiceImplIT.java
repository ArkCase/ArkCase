package com.armedia.acm.services.protecturl.service;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by nebojsha on 31.07.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-protect-url.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-protect-url-test.xml"
})
public class ProtectUrlServiceImplIT
{
    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ProtectUrlService protectUrlService;

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
}