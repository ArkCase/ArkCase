package com.armedia.acm.plugins.outlook.web.api;

import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
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

import java.util.Date;

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
public class CreateCalendarAppointmentAPIControllerTest extends EasyMockSupport {
    @Autowired
    WebApplicationContext wac;
    @Autowired
    MockHttpSession session;
    @Autowired
    MockHttpServletRequest request;
    @Autowired
    CreateCalendarAppointmentAPIController createCalendarAppointmentAPIController;

    private Authentication mockAuthentication;

    private MockMvc mockMvc;
    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private OutlookService outlookService;
    private UserOrgService userOrgService;

    @Before
    public void setup() {
        outlookService = createMock(OutlookService.class);
        userOrgService = createMock(UserOrgService.class);
        mockAuthentication = createMock(Authentication.class);
        createCalendarAppointmentAPIController.setUserOrgService(userOrgService);
        createCalendarAppointmentAPIController.setOutlookService(outlookService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(createCalendarAppointmentAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void testCreateCalendarAppointment() throws Exception {
        OutlookCalendarItem appointmentItem = new OutlookCalendarItem();
        appointmentItem.setBody("Body");
        appointmentItem.setSubject("Subject");
        appointmentItem.setAllDayEvent(false);

        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;//start tomorrow
        appointmentItem.setStartDate(new Date(tomorrow));
        appointmentItem.setEndDate(new Date(tomorrow + 1000 * 60 * 60));//start + 1 hour
        appointmentItem.setMeeting(false);
        appointmentItem.setRecurring(true);
        appointmentItem.setRecurringInterval(1);
        appointmentItem.setRecurringEndDate(new Date(tomorrow + 1000 * 60 * 60 * 48));//ends after 2 days

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        String content = objectMapper.writeValueAsString(appointmentItem);

        expect(mockAuthentication.getName()).andReturn("user").times(2);

        OutlookDTO password = new OutlookDTO();
        password.setOutlookPassword("outlookPassword");
        expect(userOrgService.retrieveOutlookPassword(mockAuthentication)).andReturn(password);
        AcmUser user = new AcmUser();
        user.setMail("test@armedia.com");
        session.setAttribute("acm_user", user);

        Capture<AcmOutlookUser> outlookUserCapture = new Capture<>();
        Capture<OutlookCalendarItem> apointmentItemCapture = new Capture<>();
        appointmentItem.setId("some_fake_id");
        expect(outlookService.createOutlookAppointment(capture(outlookUserCapture), eq(WellKnownFolderName.Calendar), capture(apointmentItemCapture))).andReturn(appointmentItem);

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/outlook/calendar")
                        .session(session)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();

        OutlookCalendarItem item = objectMapper.readValue(result.getResponse().getContentAsString(), OutlookCalendarItem.class);
        assertNotNull(item.getId());
    }
}