package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import javax.persistence.PersistenceException;

import static org.easymock.EasyMock.*;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-person-plugin-test.xml"
})
public class DeletePersonByIdAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private DeletePersonByPersonIdAPIController unit;

    private PersonDao mockPersonDao;
    private PersonAssociationDao mockPersonAssociationDao;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockPersonDao = createMock(PersonDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new DeletePersonByPersonIdAPIController();

        unit.setPersonDao(mockPersonDao);
        unit.setPersonAssociationDao(mockPersonAssociationDao);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void deleteComplaintById() throws Exception
    {
   
        Long personId = 958L;
        
        /*
         * expect that only one person with a given id is deleted.
         */
        expect(mockPersonDao.deletePersonById(personId)).andReturn(1);
   
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/person/delete/{personId}", personId)
                        .accept(MediaType.parseMediaType("application/json"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

    }
    @Test
    public void deleteComplaintById_notFound() throws Exception
    {
        Long personId = 958L;

        expect(mockPersonDao.deletePersonById(personId)).andThrow(new PersistenceException());
        
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/person/delete/{personId}", personId)
                        .accept(MediaType.parseMediaType("application/json"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }

}
