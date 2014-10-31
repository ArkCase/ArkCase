package com.armedia.acm.services.note.web.api;

import com.armedia.acm.services.note.dao.NoteDao;
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-note-plugin-test.xml"
})

public class DeleteNoteByIdAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private DeleteNoteByIdAPIController unit;

    private NoteDao mockNoteDao;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {
        mockNoteDao = createMock(NoteDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new DeleteNoteByIdAPIController();

        unit.setNoteDao(mockNoteDao);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void deleteNoteById() throws Exception
    {
        /*Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Note note = new Note();

        note.setId(700L);
        note.setCreator("testCreator");
        note.setCreated(new Date());
        note.setNote("Note");
        note.setParentType(parentType);
        note.setParentId(parentId);
        Long noteId =note.getId();
        */
        Long noteId = 700L;
        mockNoteDao.deleteNoteById(noteId);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();
        //.accept(MediaType.parseMediaType("application/json;charset=UTF-8"))


        MvcResult result = mockMvc.perform(
                delete("/api/latest/plugin/note/{noteId}", noteId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
        log.info("log" + result.getResponse());
        log.info("log" + result);


        log.info("log" + result.getResponse().getStatus());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

    }

    /*@Test
    public void deleteNoteById_notFound() throws Exception {

        Long noteId =234L;

        mockNoteDao.deleteNoteById(noteId);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                delete("/api/latest/plugin/note/{noteId}", noteId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication));

        verifyAll();
    }*/
}
