package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by manoj.dhungana on 7/23/2015.
 */

public class ListFileFolderByCategoryAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private EcmFileService mockEcmFileService;
    private ListFileFolderByCategoryAPIController unit;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUP() throws Exception
    {
        mockEcmFileService = createMock(EcmFileService.class);
        mockAuthentication = createMock(Authentication.class);
        mockHttpSession = new MockHttpSession();
        unit = new ListFileFolderByCategoryAPIController();
        unit.setEcmFileService(mockEcmFileService);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void listFileFolderByCategory_success() throws Exception
    {

        String category = "Correspondence";
        String parentObjectType = "CASE_FILE";
        Long parentObjectId = 234L;

        AcmCmisObjectList acmCmisObjectList = new AcmCmisObjectList();
        acmCmisObjectList.setCategory(category);
        acmCmisObjectList.setContainerObjectId(parentObjectId);
        acmCmisObjectList.setContainerObjectType(parentObjectType);

        AcmContainer container = new AcmContainer();
        container.setContainerObjectId(parentObjectId);
        container.setContainerObjectType(parentObjectType);
        container.setFolder(new AcmFolder());

        String url = "/api/v1/service/ecm/bycategory/" + parentObjectType + "/" + parentObjectId + "?category=Correspondence";
        log.info("Rest endpoint : " + url);


        expect(mockEcmFileService.getOrCreateContainer(parentObjectType, parentObjectId)).andReturn(container).atLeastOnce();
        expect(mockEcmFileService.listFileFolderByCategory(mockAuthentication, container, "name", "ASC", 0, 1000, category)).andReturn(acmCmisObjectList);
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get(url)
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: " + json);

        AcmCmisObjectList fromJson = new ObjectMapper().readValue(json, AcmCmisObjectList.class);

        assertNotNull(fromJson);
        assertEquals(category, fromJson.getCategory());
        assertEquals(parentObjectType, fromJson.getContainerObjectType());
        assertEquals(parentObjectId, fromJson.getContainerObjectId());
    }
}
