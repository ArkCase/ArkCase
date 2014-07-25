package com.armedia.acm.auth;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * This test verifies we can add and remove child contexts, and search for
 * authentication managers, according to the authentication design.
 */
public class ContextIT
{
    private AbstractApplicationContext parentContext;
    private AbstractApplicationContext childContext;

    @Before
    public void setUp() throws Exception
    {
        parentContext = new ClassPathXmlApplicationContext("/spring/spring-library-app-context.xml");
    }

    @Test
    public void addAndRemoveChildContext() throws Exception
    {
        String parentBean = parentContext.getBean("string-bean", String.class);
        assertNotNull(parentBean);

        Map<String, AuthenticationProvider> providers = parentContext.getBeansOfType(AuthenticationProvider.class);

        assertTrue(providers.isEmpty());

        childContext = new ClassPathXmlApplicationContext(
                new String[] {"/spring/spring-library-child-context.xml"}, true, parentContext);

        providers = childContext.getBeansOfType(AuthenticationProvider.class);
        assertEquals(1, providers.size());
        assertEquals("authenticationProvider", providers.keySet().iterator().next());

        childContext.close();
    }
}
