package com.armedia.acm.services.note.web.api;

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
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

import javax.persistence.QueryTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-note-plugin-test.xml"
})
public class ListAllNotesAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private NoteDao mockNoteDao;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private ListAllNotesAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        mockNoteDao = createMock(NoteDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new ListAllNotesAPIController();

        unit.setNoteDao(mockNoteDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void ListNote() throws Exception
    {
    	String type = "GENERAL";
        Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Note note = new Note();

        note.setId(700L);
        note.setCreator("testCreator");
        note.setCreated(new Date());
        note.setNote("Note");
        note.setType(type);
        note.setParentType(parentType);
        note.setParentId(parentId);

        List<Note> noteList = new ArrayList<>();
        noteList.add(note);


        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockNoteDao.listNotes(type, parentId, parentType)).andReturn(noteList);
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/note/{parentType}/{parentId}", parentType, parentId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Note> fromReturnedNoteList = mapper.readValue(
                returned,
                mapper.getTypeFactory().constructParametricType(List.class, Note.class));

        assertNotNull(fromReturnedNoteList);
        assertEquals(fromReturnedNoteList.size(),1);
        assertEquals(fromReturnedNoteList.get(0).getType(), type);
        assertEquals(fromReturnedNoteList.get(0).getParentId(), parentId);
        assertEquals(fromReturnedNoteList.get(0).getParentType(), parentType);

        log.info("note size : ", fromReturnedNoteList.size());
        log.info("note : ", fromReturnedNoteList.get(0).getNote());
    }

    @Test
    public void listNote_exception() throws Exception
    {
    	String type = "GENERAL";
        Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Note note = new Note();

        note.setId(700L);
        note.setCreator("testCreator");
        note.setCreated(new Date());
        note.setNote("Note");
        note.setType(type);
        note.setParentType(parentType);
        note.setParentId(parentId);

        List<Note> noteList = new ArrayList<>();
        noteList.add(note);


        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockNoteDao.listNotes(type, parentId,parentType)).andThrow(new QueryTimeoutException("test exception"));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/note/{parentType}/{parentId}", parentType, parentId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
    }
}
