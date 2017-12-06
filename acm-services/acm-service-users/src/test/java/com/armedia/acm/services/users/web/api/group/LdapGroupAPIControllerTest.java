package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@RunWith(MockitoJUnitRunner.class)
public class LdapGroupAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;

    @Mock
    private GroupService mockGroupService;

    @Mock
    private AcmGroupEventPublisher mockGroupEventPublisher;

    @Mock
    SpringContextHolder springContextHolder;

    @Mock
    LdapGroupService mockLdapGroupService;

    @InjectMocks
    @Spy
    LdapGroupAPIController ldapGroupAPIController;

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

    private void mockBehaviour(AcmGroup group) throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        when(mockGroupService.findByName(anyString())).thenReturn(group);
        when(mockLdapGroupService.deleteLdapGroup(anyString(), anyString())).thenReturn(group);
    }
}
