package com.armedia.acm.services.users.web.api.group;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.AcmGroupEventPublisher;
import com.armedia.acm.services.users.service.group.LdapGroupService;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@RunWith(MockitoJUnitRunner.class)
public class LdapGroupAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;

    @Mock
    private AcmGroupDao mockGroupDao;

    @Mock
    private AcmGroupEventPublisher mockGroupEventPublisher;

    @Mock
    LdapGroupService mockLdapGroupService;

    @InjectMocks
    @Spy
    LdapGroupAPIController ldapGroupAPIController;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(ldapGroupAPIController).build();
        mockLdapGroupService.setGroupDao(mockGroupDao);
    }

    @Test
    public void removeLdapGroupTest() throws Exception
    {
        String directory = "armedia";
        AcmGroup group = new AcmGroup();

        group.setName("test-group");
        group.setDescription("test ldap group");

        mockBehaviour(group);

        MvcResult result = mockMvc.perform(
                delete("/api/v1/ldap/" + directory + "/groups/" + group.getName() + "/remove")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();


        LOG.info("Results: " + result.getResponse().getContentAsString());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        verify(mockLdapGroupService, times(1)).removeLdapGroup(anyString(), anyString());
        verifyAll();

    }

    private void mockBehaviour(AcmGroup group) throws AcmLdapActionFailedException, AcmAppErrorJsonMsg
    {
        when(mockLdapGroupService.getGroupDao()).thenReturn(mockGroupDao);
        when(mockGroupDao.findByGroupId(anyString())).thenReturn(group);
        when(mockLdapGroupService.removeLdapGroup(anyString(), anyString())).thenReturn(group);
        doNothing().when(ldapGroupAPIController).checkIfLdapManagementIsAllowed(anyString());
    }
}
