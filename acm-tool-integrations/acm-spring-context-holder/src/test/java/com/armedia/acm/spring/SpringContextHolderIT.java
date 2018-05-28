package com.armedia.acm.spring;

/*-
 * #%L
 * Tool Integrations: Spring Child Context Holder
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-context-holder.xml" })
public class SpringContextHolderIT
{
    @Autowired
    private SpringContextHolder springContextHolder;

    @Test
    public void getAllBeansOfType() throws Exception
    {
        assertNotNull(springContextHolder);

        Map<String, SpringContextHolder> allHolders = springContextHolder.getAllBeansOfType(SpringContextHolder.class);
        assertTrue(!allHolders.isEmpty());
    }

    @Test
    public void addAndRemoveContext() throws Exception
    {
        Resource contextResource = new ClassPathResource("spring/spring-config-context-holder-test-context.xml");
        assertTrue(contextResource.exists());

        springContextHolder.addContextFromFile(contextResource.getFile());

        Map<String, String> allStrings = springContextHolder.getAllBeansOfType(String.class);
        assertTrue(!allStrings.isEmpty());

        springContextHolder.removeContext(contextResource.getFile().getName());

        allStrings = springContextHolder.getAllBeansOfType(String.class);
        assertTrue(allStrings.isEmpty());
    }

    @Test
    public void replaceContext() throws Exception
    {
        Resource contextResource1 = new ClassPathResource("spring/spring-config-context-holder-test-context.xml");
        assertTrue(contextResource1.exists());

        springContextHolder.addContextFromFile(contextResource1.getFile());

        Map<String, String> allStrings = springContextHolder.getAllBeansOfType(String.class);
        assertTrue(allStrings.size() == 1);
        assertTrue(allStrings.get("string-bean").equals("Grateful Dead"));

        Resource contextResource2 = new ClassPathResource("spring/spring-config-context-holder-test-context-v2.xml");
        assertTrue(contextResource2.exists());

        springContextHolder.replaceContextFromFile(contextResource2.getFile());

        allStrings = springContextHolder.getAllBeansOfType(String.class);
        assertTrue(allStrings.size() == 2);
        assertTrue(allStrings.get("string-bean").equals("Grateful Dead Alive"));
        assertTrue(allStrings.get("string-bean-2").equals("Grateful Dead 2"));
    }

}
