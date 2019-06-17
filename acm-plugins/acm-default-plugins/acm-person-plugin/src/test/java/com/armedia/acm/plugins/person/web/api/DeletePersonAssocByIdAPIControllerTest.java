package com.armedia.acm.plugins.person.web.api;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationEventPublisher;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-person-plugin-api-test.xml",
})

public class DeletePersonAssocByIdAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private DeletePersonAssocByIdAPIController unit;

    private PersonAssociationDao mockPersonAssociationDao;
    private Authentication mockAuthentication;
    private PersonAssociation mockPersonAssociation;
    private PersonAssociationEventPublisher mockPersonAssociationEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockPersonAssociationDao = createMock(PersonAssociationDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockPersonAssociation = createMock(PersonAssociation.class);
        mockPersonAssociationEventPublisher = createMock(PersonAssociationEventPublisher.class);

        unit = new DeletePersonAssocByIdAPIController();

        unit.setPersonAssociationDao(mockPersonAssociationDao);
        unit.setPersonAssociationDao(mockPersonAssociationDao);
        unit.setPersonAssociationEventPublisher(mockPersonAssociationEventPublisher);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void deletePersonAssociationById() throws Exception
    {
        Long personAssocId = 958L;

        expect(mockPersonAssociationDao.find(personAssocId)).andReturn(mockPersonAssociation);
        mockPersonAssociationEventPublisher.publishPersonAssociationDeletedEvent(mockPersonAssociation);
        expectLastCall();
        mockPersonAssociationDao.deletePersonAssociationById(personAssocId);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();
        MvcResult result = mockMvc.perform(
                delete("/api/v1/plugin/personAssociation/delete/{personAssocId}", personAssocId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

    }

    /*
     * @Test
     * public void deletePersonAssociationById_notFound() throws Exception {
     * Long personAssocId =958L;
     * mockPersonAssociationDao.deletePersonAssociationById(personAssocId);
     * // MVC test classes must call getName() somehow
     * expect(mockAuthentication.getName()).andReturn("user");
     * replayAll();
     * mockMvc.perform(
     * delete("/api/v1/plugin/personAssociation/delete/{personAssocId}", personAssocId)
     * .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
     * .principal(mockAuthentication));
     * verifyAll();
     * }
     */

}
