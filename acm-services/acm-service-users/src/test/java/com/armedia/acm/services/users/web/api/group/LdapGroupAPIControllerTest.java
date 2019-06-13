package com.armedia.acm.services.users.web.api.group;

/*-
 * #%L
 * ACM Service: Users
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.service.AcmGroupEventPublisher;
import com.armedia.acm.services.users.service.group.GroupService;
import com.armedia.acm.services.users.service.group.LdapGroupService;
import com.armedia.acm.spring.SpringContextHolder;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class LdapGroupAPIControllerTest extends EasyMockSupport
{
    @Mock
    SpringContextHolder springContextHolder;
    @Mock
    LdapGroupService mockLdapGroupService;
    @InjectMocks
    @Spy
    LdapGroupAPIController ldapGroupAPIController;
    private Logger LOG = LogManager.getLogger(getClass());
    private MockMvc mockMvc;
    @Mock
    private GroupService mockGroupService;
    @Mock
    private AcmGroupEventPublisher mockGroupEventPublisher;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(ldapGroupAPIController).build();
    }

    @Test
    public void removeLdapGroupTest() throws Exception
    {
        String directory = "armedia";
        AcmGroup group = new AcmGroup();

        group.setName("test-group");
        group.setDescription("test ldap group");

        AcmLdapAuthenticateConfig config = new AcmLdapAuthenticateConfig();
        config.setEnableEditingLdapUsers(true);

        mockBehaviour(group);
        when(springContextHolder.getAllBeansOfType(AcmLdapAuthenticateConfig.class))
                .thenReturn(Collections.singletonMap("armedia_authenticate", config));

        MvcResult result = mockMvc
                .perform(delete("/api/v1/ldap/" + directory + "/groups/" + group.getName())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LOG.info("Results: {}", result.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        verify(mockLdapGroupService, times(1)).deleteLdapGroup(anyString(), anyString());
        verifyAll();

    }

    private void mockBehaviour(AcmGroup group) throws Exception
    {
        when(mockLdapGroupService.deleteLdapGroup(anyString(), anyString())).thenReturn(group);
    }
}
