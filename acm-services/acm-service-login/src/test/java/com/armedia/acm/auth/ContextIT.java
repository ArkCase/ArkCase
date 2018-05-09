package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Map;

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
                new String[] { "/spring/spring-library-child-context.xml" }, true, parentContext);

        providers = childContext.getBeansOfType(AuthenticationProvider.class);
        assertEquals(1, providers.size());
        assertEquals("authenticationProvider", providers.keySet().iterator().next());

        childContext.close();
    }
}
