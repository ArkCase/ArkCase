package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-config-user-service-test-dummy-beans.xml"
})
public class LdapUserAPIControllerTest extends EasyMockSupport
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;
    private LdapUserAPIController unit;
    private Authentication mockAuthentication;
    private UserDao mockUserDao;
    private LdapUserService mockLdapUserService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        setUnit(new LdapUserAPIController());
        setMockMvc(MockMvcBuilders.standaloneSetup(getUnit()).setHandlerExceptionResolvers(getExceptionResolver()).build());
        setMockAuthentication(createMock(Authentication.class));
        setMockUserDao(createMock(UserDao.class));

    }

    @Test
    public void removeLdapUserTest() throws Exception
    {
        String directory = "directory";
        AcmUser user = new AcmUser();
        user.setUserId("test-user");
        user.setUserState("TEST");
        user.setFirstName("First Name");
        user.setLastName("Last Name");

        expect(getMockUserDao().findByUserId(user.getUserId())).andReturn(user.getUserId());

        replayAll();

        MvcResult result = getMockMvc().perform(
                delete("/api/v1/ldap/" + directory + +"/users/" + user.getUserId())
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(getMockAuthentication()))
                .andReturn();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    public MockMvc getMockMvc()
    {
        return mockMvc;
    }

    public void setMockMvc(MockMvc mockMvc)
    {
        this.mockMvc = mockMvc;
    }

    public LdapUserAPIController getUnit()
    {
        return unit;
    }

    public void setUnit(LdapUserAPIController unit)
    {
        this.unit = unit;
    }

    public Authentication getMockAuthentication()
    {
        return mockAuthentication;
    }

    public void setMockAuthentication(Authentication mockAuthentication)
    {
        this.mockAuthentication = mockAuthentication;
    }

    public ExceptionHandlerExceptionResolver getExceptionResolver()
    {
        return exceptionResolver;
    }

    public void setExceptionResolver(
            ExceptionHandlerExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
    }

    public UserDao getMockUserDao()
    {
        return mockUserDao;
    }

    public void setMockUserDao(UserDao mockUserDao)
    {
        this.mockUserDao = mockUserDao;
    }

    public LdapUserService getMockLdapUserService()
    {
        return mockLdapUserService;
    }

    public void setMockLdapUserService(LdapUserService mockLdapUserService)
    {
        this.mockLdapUserService = mockLdapUserService;
    }

}
