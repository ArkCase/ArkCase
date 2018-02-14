package com.armedia.acm.plugins.objectassociaton.web.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationEventPublisher;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nebojsha.davidovikj on 6/16/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ObjectAssociationAPIControllerTest implements HandlerExceptionResolver
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private MockMvc mockMvc;

    @InjectMocks
    private ObjectAssociationService objectAssociationService = new ObjectAssociationServiceImpl();

    @Mock
    private ObjectAssociationDao objectAssociationDao;

    @Mock
    private ObjectAssociationEventPublisher objectAssociationEventPublisher;

    @Mock
    private Authentication mockAuthentication;

    @InjectMocks
    ObjectAssociationAPIController objectAssociationAPIController = new ObjectAssociationAPIController();

    @Before
    public void setUp() throws Exception
    {
        objectAssociationAPIController.setObjectAssociationService(objectAssociationService);
        mockMvc = MockMvcBuilders.standaloneSetup(objectAssociationAPIController).setHandlerExceptionResolvers(this).build();
    }

    @Test
    public void createAssociation() throws Exception
    {
        assertNotNull(objectAssociationAPIController.getObjectAssociationService());

        String content = "{\"@id\":\"1\",\"parentId\":105,\"parentType\":\"PERSON\",\"parentClassName\":\"com.armedia.acm.plugins.person.model.Person\",\"targetId\":101,\"targetType\":\"PERSON\",\"targetClassName\":\"com.armedia.acm.plugins.person.model.Person\",\"associationType\":\"Sibling\",\"inverseAssociation\":{\"@id\":\"2\",\"parentId\":101,\"parentType\":\"PERSON\",\"parentClassName\":\"com.armedia.acm.plugins.person.model.Person\",\"targetId\":105,\"targetType\":\"PERSON\",\"targetClassName\":\"com.armedia.acm.plugins.person.model.Person\",\"associationType\":\"Sibling\",\"inverseAssociation\":{\"@ref\":\"1\"}}}";

        when(objectAssociationDao.save(anyObject())).then(invocationOnMock -> invocationOnMock.getArguments()[0]);

        MvcResult result = mockMvc.perform(
                post("/api/latest/service/objectassociations")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content).principal(mockAuthentication))
                .andExpect(status().isOk())
                .andReturn();

        log.info("Results: " + result.getResponse().getContentAsString());
        ObjectMapper om = new ObjectMapper();
        ObjectAssociation objectAssociation = om.readValue(result.getResponse().getContentAsByteArray(), ObjectAssociation.class);

        assertNotNull(objectAssociation.getInverseAssociation());
        ObjectAssociation inverseAssociation = objectAssociation.getInverseAssociation();
        assertEquals(objectAssociation.getTargetId(), inverseAssociation.getParentId());
        assertEquals(objectAssociation.getTargetType(), inverseAssociation.getParentType());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
        log.error("there is an error", ex);
        return null;
    }
}