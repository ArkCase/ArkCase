package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
        "classpath:/spring-web-ms-outlook-plugin-api.xml"})
public class CreateContactItemAPIControllerTest extends EasyMockSupport {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    MockHttpSession session;
    @Autowired
    MockHttpServletRequest request;
    @Autowired
    CreateContactItemAPIController createContactItemAPIController;

    private Authentication mockAuthentication;

    private MockMvc mockMvc;
    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private OutlookService outlookService;
    private UserOrgDao userOrgDao;

    @Before
    public void setup() {
        outlookService = createMock(OutlookService.class);
        userOrgDao = createMock(UserOrgDao.class);
        mockAuthentication = createMock(Authentication.class);
        createContactItemAPIController.setUserOrgDao(userOrgDao);
        createContactItemAPIController.setOutlookService(outlookService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(createContactItemAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void testCreateContactItem() throws Exception {
        OutlookContactItem contactItem = new OutlookContactItem();
        contactItem.setDisplayName("John Doe");
        contactItem.setBody("Body");
        contactItem.setSubject("Subject");
        contactItem.setCompanyName("Armedia");
        contactItem.setEmailAddress1("john.doe@armedia.com");
        contactItem.setPrimaryTelephone("+55555656456");
        contactItem.setSurname("Doe");
        contactItem.setCompleteName("John Doe");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        String content = objectMapper.writeValueAsString(contactItem);

        expect(mockAuthentication.getName()).andReturn("user").times(2);
        OutlookDTO password = new OutlookDTO();
        password.setOutlookPassword("outlookPassword");
        expect(userOrgDao.retrieveOutlookPassword(mockAuthentication)).andReturn(password);
        AcmUser user = new AcmUser();
        user.setMail("test@armedia.com");
        session.setAttribute("acm_user", user);

        Capture<AcmOutlookUser> outlookUserCapture = new Capture<>();
        Capture<OutlookContactItem> contactItemCapture = new Capture<>();

        contactItem.setId("some_fake_id");
        expect(outlookService.createOutlookContactItem(capture(outlookUserCapture),
                eq(WellKnownFolderName.Contacts),
                capture(contactItemCapture))).
                andReturn(contactItem);
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/outlook/contacts")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();

        OutlookContactItem item = objectMapper.readValue(result.getResponse().getContentAsString(), OutlookContactItem.class);
        assertNotNull(item.getId());
    }
}