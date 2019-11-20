package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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

import com.armedia.acm.spring.SpringContextHolder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-data-source-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class SpringEntityListenerConfigurerIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    @Autowired
    private TestInsertListener testInsertListener;

    @Autowired
    private TestUpdateListener testUpdateListener;

    @Autowired
    private TestDeleteListener testDeleteListener;

    @Autowired
    private SpringEntityListenerConfigurer springEntityListenerConfigurer;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringContextHolder springContextHolder;

    @Before
    public void setUp() throws Exception
    {
        springEntityListenerConfigurer.setSpringContextHolder(springContextHolder);
    }

    @Test
    public void findListenersOnApplicationContextInjection()
    {
        assertNotNull(applicationContext);

        assertNotNull(springEntityListenerConfigurer.getSpringContextHolder());

        assertEquals(1, springEntityListenerConfigurer.findBeforeInsertListeners().size());
        assertEquals(testInsertListener, springEntityListenerConfigurer.findBeforeInsertListeners().iterator().next());

        assertEquals(1, springEntityListenerConfigurer.findBeforeUpdateListeners().size());
        assertEquals(testUpdateListener, springEntityListenerConfigurer.findBeforeUpdateListeners().iterator().next());

        assertEquals(1, springEntityListenerConfigurer.findBeforeDeleteListeners().size());
        assertEquals(testDeleteListener, springEntityListenerConfigurer.findBeforeDeleteListeners().iterator().next());
    }

    @Test
    public void generateAdaptersForListeners()
    {
        assertEquals(3, springEntityListenerConfigurer.generateAdaptersForListeners().size());
    }

}
