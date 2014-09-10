package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;

import static org.easymock.EasyMock.*;
import org.easymock.EasyMockSupport;
import static org.junit.Assert.assertEquals;
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
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:/spring/spring-web-acm-web.xml",
    "classpath:/spring/spring-library-person-plugin-test.xml",
    "/spring/spring-library-data-source.xml"
})
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
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
    public void setUp() throws Exception {
        mockPersonDao = createMock(PersonDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new DeletePersonByPersonIdAPIController();

        unit.setPersonDao(mockPersonDao);
        unit.setPersonAssociationDao(mockPersonAssociationDao);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    @Transactional
    public void deletePersonById() throws Exception 
    {                        
          Long personId =958L;         

        mockPersonDao.deletePersonById(personId);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
               delete("/api/v1/plugin/person/delete/{personId}", personId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
       
    }

    @Test
    @Transactional
    public void deletePersonById_notFound() throws Exception {
                        
          Long personId =958L;        
       
        mockPersonDao.deletePersonById(personId);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                delete("/api/v1/plugin/person/delete/{personId}", personId)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .principal(mockAuthentication));

        verifyAll();
    }

}
