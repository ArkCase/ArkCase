package com.armedia.acm.services.authenticationtoken.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-authentication-token.xml"
})
public class AuthenticationTokenCacheTest
{
    @Autowired
    private EhCacheCacheManager cacheManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void cache() throws Exception
    {

        Cache cache = cacheManager.getCache("authentication_token_cache");

//        cache = cacheManager.getCache("testCache");

//        cache = new Cache("testCache", 0, false, false, 0, 2);
//        cache.initialise();

        assertNotNull(cache);

        log.debug("cache: " + cache);

        cache.put(500L, "element");

        Cache.ValueWrapper found = cache.get(500L);

        assertNotNull(found);
        assertEquals("element", found.get());

        Thread.sleep(3000);

        found = cache.get(500L);

        assertNull(found);




    }
}
