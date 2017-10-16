package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.plugins.dashboard.model.userPreference.PreferredWidgetsDto;
import com.armedia.acm.plugins.dashboard.service.UserPreferenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class GetUserPreferredWidgetsByModuleTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetUserPreferredWidgetsByModule unit;

    private UserPreferenceService mockUserPreferenceService;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockUserPreferenceService = createMock(UserPreferenceService.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new GetUserPreferredWidgetsByModule();

        unit.setUserPreferenceService(mockUserPreferenceService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void getPreferredWidgets() throws Exception
    {
        String userId = "marjan";
        String moduleName = "moduleName";

        List<String> widgetList = new ArrayList<>();
        widgetList.add("newWidget");

        PreferredWidgetsDto preferredWidgetsDto = new PreferredWidgetsDto();
        preferredWidgetsDto.setPreferredWidgets(widgetList);
        preferredWidgetsDto.setModuleName(moduleName);

        expect(mockUserPreferenceService.getPreferredWidgetsByUserAndModule(userId, moduleName)).andReturn(preferredWidgetsDto);


        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("marjan").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dashboard/widgets/preferred/moduleName")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: " + json);

        PreferredWidgetsDto fromJson = new ObjectMapper().readValue(json, PreferredWidgetsDto.class);

        assertNotNull(fromJson);
        assertEquals(preferredWidgetsDto.getModuleName(), fromJson.getModuleName());
        assertEquals(preferredWidgetsDto.getPreferredWidgets().get(0), fromJson.getPreferredWidgets().get(0));
    }

}
