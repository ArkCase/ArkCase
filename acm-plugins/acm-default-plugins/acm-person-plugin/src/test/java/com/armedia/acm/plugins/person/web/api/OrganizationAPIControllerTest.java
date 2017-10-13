package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.service.OrganizationEventPublisher;
import com.armedia.acm.plugins.person.service.OrganizationService;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maksud.sharif on 6/1/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/spring-web-acm-web.xml", "classpath:/spring/spring-library-person-plugin-test.xml"})
public class OrganizationAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;
    private OrganizationEventPublisher mockOrganizationEventPublisher;
    private OrganizationDao mockOrganizationDao;
    private ExecuteSolrQuery mockExecuteSolrQuery;
    private OrganizationService mockOrganizationService;

    private OrganizationAPIController unit;

    @Before
    public void setUp() throws Exception
    {
        mockAuthentication = createMock(Authentication.class);
        mockOrganizationDao = createMock(OrganizationDao.class);
        mockHttpSession = new MockHttpSession();
        mockOrganizationEventPublisher = createMock(OrganizationEventPublisher.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        mockOrganizationService = createMock(OrganizationService.class);


        unit = new OrganizationAPIController();
        unit.setExecuteSolrQuery(mockExecuteSolrQuery);
        unit.setOrganizationEventPublisher(mockOrganizationEventPublisher);
        unit.setOrganizationService(mockOrganizationService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();

    }

    @Test
    public void upsertOrganizationNew() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String ipAddress = "192.168.56.1";
        String user = "USER";
        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        Organization body = new Organization();
        body.setOrganizationValue("Armedia LLC.");

        Capture<Organization> orgCapture = EasyMock.newCapture();
        Capture<Organization> orgCaptureOld = EasyMock.newCapture();

        expect(mockAuthentication.getName()).andReturn(user).anyTimes();
        expect(mockOrganizationService.saveOrganization(anyObject(Organization.class), eq(mockAuthentication), eq(ipAddress))).andReturn(body);
        mockOrganizationEventPublisher.publishOrganizationUpsertEvent(capture(orgCapture), capture(orgCaptureOld), eq(true), eq(true));
        expectLastCall();

        replayAll();

        MvcResult result = mockMvc.perform(post("/api/latest/plugin/organizations")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body))
                .principal(mockAuthentication)
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn();

        verifyAll();

        Organization retval = mapper.readValue(result.getResponse().getContentAsString(), Organization.class);
        assertEquals(body.getOrganizationValue(), retval.getOrganizationValue());
        assertEquals(body.getOrganizationValue(), orgCapture.getValue().getOrganizationValue());
    }

    @Test
    public void upsertOrganizationExisting() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String ipAddress = "192.168.56.1";
        String user = "USER";
        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        Organization body = new Organization();
        body.setOrganizationId(-1L);
        body.setOrganizationValue("Armedia LLC.");

        Capture<Organization> orgCapture = EasyMock.newCapture();
        Capture<Organization> orgCaptureOld = EasyMock.newCapture();
        expect(mockOrganizationService.getOrganization(body.getOrganizationId())).andReturn(body);
        expect(mockAuthentication.getName()).andReturn(user).anyTimes();
        expect(mockOrganizationService.saveOrganization(anyObject(Organization.class), eq(mockAuthentication), eq(ipAddress))).andReturn(body);
        mockOrganizationEventPublisher.publishOrganizationUpsertEvent(capture(orgCapture), capture(orgCaptureOld), eq(false), eq(true));
        expectLastCall();

        replayAll();

        MvcResult result = mockMvc.perform(post("/api/latest/plugin/organizations")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body))
                .principal(mockAuthentication)
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn();

        verifyAll();

        Organization retval = mapper.readValue(result.getResponse().getContentAsString(), Organization.class);
        assertEquals(body.getOrganizationValue(), retval.getOrganizationValue());
        assertEquals(body.getOrganizationValue(), orgCapture.getValue().getOrganizationValue());
        assertEquals(body.getOrganizationId(), orgCapture.getValue().getOrganizationId());

    }

}