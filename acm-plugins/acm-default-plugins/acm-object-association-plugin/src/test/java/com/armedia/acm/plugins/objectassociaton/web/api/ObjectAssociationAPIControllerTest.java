package com.armedia.acm.plugins.objectassociaton.web.api;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    @InjectMocks
    ObjectAssociationAPIController objectAssociationAPIController = new ObjectAssociationAPIController();
    private Logger log = LogManager.getLogger(getClass());
    private MockMvc mockMvc;
    @InjectMocks
    private ObjectAssociationService objectAssociationService = new ObjectAssociationServiceImpl();
    @Mock
    private ObjectAssociationDao objectAssociationDao;
    @Mock
    private ObjectAssociationEventPublisher objectAssociationEventPublisher;
    @Mock
    private Authentication mockAuthentication;

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
