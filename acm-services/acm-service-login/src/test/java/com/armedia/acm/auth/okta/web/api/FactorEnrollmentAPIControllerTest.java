package com.armedia.acm.auth.okta.web.api;

import com.armedia.acm.auth.okta.model.ProviderType;
import com.armedia.acm.auth.okta.model.factor.ActivateRequestDTO;
import com.armedia.acm.auth.okta.model.factor.Factor;
import com.armedia.acm.auth.okta.model.factor.FactorProfile;
import com.armedia.acm.auth.okta.model.factor.FactorType;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.services.FactorLifecycleService;
import com.armedia.acm.auth.okta.services.FactorService;
import com.armedia.acm.auth.okta.services.OktaUserService;
import com.armedia.acm.auth.okta.services.impl.FactorLifecycleServiceImpl;
import com.armedia.acm.auth.okta.services.impl.FactorServiceImpl;
import com.armedia.acm.auth.okta.services.impl.OktaUserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by joseph.mcgrady on 11/13/2017.
 */
public class FactorEnrollmentAPIControllerTest extends EasyMockSupport
{
    private FactorEnrollmentAPIController unit;
    private Factor expectedFactor;
    private FactorProfile expectedProfile;
    private OktaUser expectedUser;
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private Authentication mockAuthentication;
    private OktaUserService mockOktaUserService;
    private FactorService mockFactorService;
    private FactorLifecycleService mockFactorLifecycleService;

    @Before
    public void setup()
    {
        expectedFactor = new Factor();
        expectedFactor.setId("2q89738dbnsdjvu83");
        expectedFactor.setFactorType(FactorType.EMAIL);
        expectedFactor.setProvider(ProviderType.OKTA);
        expectedProfile = new FactorProfile();
        expectedProfile.setEmail("test@armedia.com");
        expectedFactor.setProfile(expectedProfile);

        expectedUser = new OktaUser();
        expectedUser.setId("uy298hf238");

        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");
        mockAuthentication = createMock(Authentication.class);
        mockOktaUserService = createMock(OktaUserServiceImpl.class);
        mockFactorService = createMock(FactorServiceImpl.class);
        mockFactorLifecycleService = createMock(FactorLifecycleServiceImpl.class);

        unit = new FactorEnrollmentAPIController();
        unit.setFactorService(mockFactorService);
        unit.setFactorLifecycleService(mockFactorLifecycleService);
        unit.setOktaUserService(mockOktaUserService);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void findEnrolledFactorsTest() throws Exception
    {
        List<Factor> expectedList = new ArrayList<>();
        expectedList.add(expectedFactor);

        expect(mockAuthentication.getName()).andReturn(expectedUser.getId()).atLeastOnce();
        expect(mockOktaUserService.getUser(expectedUser.getId())).andReturn(expectedUser);
        expect(mockFactorService.listEnrolledFactors(expectedUser)).andReturn(expectedList);

        replayAll();
        MvcResult mvcResult = mockMvc.perform(
                get("/api/latest/plugin/okta/factor/enrollment")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
        ).andReturn();
        verifyAll();

        // Verifies the request succeeded with HTTP 200 and returned json data
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        // Tests list of factors returned in the response
        ObjectMapper objectMapper = new ObjectMapper();
        List<Factor> factors = Arrays.asList(objectMapper.readValue(response.getContentAsString(), Factor[].class));
        assertNotNull(factors);
        assertEquals(1, factors.size());
        Factor factor = factors.get(0);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void findAvailableFactorsTest() throws Exception
    {
        List<Factor> expectedList = new ArrayList<>();
        expectedList.add(expectedFactor);

        expect(mockAuthentication.getName()).andReturn(expectedUser.getId()).atLeastOnce();
        expect(mockOktaUserService.getUser(expectedUser.getId())).andReturn(expectedUser);
        expect(mockFactorService.listAvailableFactors(expectedUser)).andReturn(expectedList);

        replayAll();
        MvcResult mvcResult = mockMvc.perform(
                get("/api/latest/plugin/okta/factor/enrollment/available")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
        ).andReturn();
        verifyAll();

        // Verifies the request succeeded with HTTP 200 and returned json data
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        // Tests list of factors returned in the response
        ObjectMapper objectMapper = new ObjectMapper();
        List<Factor> factors = Arrays.asList(objectMapper.readValue(response.getContentAsString(), Factor[].class));
        assertNotNull(factors);
        assertEquals(1, factors.size());
        Factor factor = factors.get(0);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void enrollFactorTest() throws Exception
    {
        expect(mockAuthentication.getName()).andReturn(expectedUser.getId()).atLeastOnce();
        expect(mockOktaUserService.getUser(expectedUser.getId())).andReturn(expectedUser);
        expect(mockFactorLifecycleService.enroll(expectedFactor.getFactorType(), expectedFactor.getProvider(),
                expectedFactor.getProfile(),expectedUser)).andReturn(expectedFactor);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(expectedFactor);

        replayAll();
        MvcResult mvcResult = mockMvc.perform(
                post("/api/latest/plugin/okta/factor/enrollment")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
        ).andReturn();
        verifyAll();

        // Verifies the request succeeded with HTTP 200 and returned json data
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        // Tests factor returned in the response
        Factor factor = objectMapper.readValue(response.getContentAsString(), Factor.class);
        assertNotNull(factor);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void activateFactorTest() throws Exception
    {
        ActivateRequestDTO requestDTO = new ActivateRequestDTO();
        requestDTO.setFactorId(expectedFactor.getId());
        requestDTO.setActivationCode("872925");

        expect(mockAuthentication.getName()).andReturn(expectedUser.getId()).atLeastOnce();
        expect(mockOktaUserService.getUser(expectedUser.getId())).andReturn(expectedUser);
        expect(mockFactorLifecycleService.activate(requestDTO.getFactorId(), requestDTO.getActivationCode(), expectedUser)).andReturn(expectedFactor);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestDTO);

        replayAll();
        MvcResult mvcResult = mockMvc.perform(
                post("/api/latest/plugin/okta/factor/enrollment/activate")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
        ).andReturn();
        verifyAll();

        // Verifies the request succeeded with HTTP 200 and returned json data
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        // Tests factor returned in the response
        Factor factor = objectMapper.readValue(response.getContentAsString(), Factor.class);
        assertNotNull(factor);
        assertEquals(expectedFactor.getId(), factor.getId());
        assertEquals(expectedFactor.getFactorType(), factor.getFactorType());
        assertEquals(expectedFactor.getProvider(), factor.getProvider());
        FactorProfile profile = factor.getProfile();
        assertNotNull(profile);
        assertEquals(expectedProfile.getEmail(), profile.getEmail());
    }

    @Test
    public void resetFactorsTest() throws Exception
    {
        expect(mockAuthentication.getName()).andReturn(expectedUser.getId()).atLeastOnce();
        expect(mockOktaUserService.getUser(expectedUser.getId())).andReturn(expectedUser);
        mockFactorLifecycleService.resetFactors(expectedUser);
        EasyMock.expectLastCall();

        replayAll();
        MvcResult mvcResult = mockMvc.perform(
                post("/api/latest/plugin/okta/factor/enrollment/reset")
                        .accept(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
        ).andReturn();
        verifyAll();

        // Verifies the request succeeded with HTTP 200 and returned json data
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void deleteFactorTest() throws Exception
    {
        expect(mockAuthentication.getName()).andReturn(expectedUser.getId()).atLeastOnce();
        expect(mockOktaUserService.getUser(expectedUser.getId())).andReturn(expectedUser);
        mockFactorService.deleteFactor(expectedFactor.getId(), expectedUser);
        EasyMock.expectLastCall();

        replayAll();
        MvcResult mvcResult = mockMvc.perform(
                delete("/api/latest/plugin/okta/factor/enrollment?factorId={factorId}", expectedFactor.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .principal(mockAuthentication)
        ).andReturn();
        verifyAll();

        // Verifies the request succeeded with HTTP 200 and returned json data
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}