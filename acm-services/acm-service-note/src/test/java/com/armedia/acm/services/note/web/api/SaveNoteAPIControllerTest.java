package com.armedia.acm.services.note.web.api;

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.service.NoteEventPublisher;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
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

import java.util.Date;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-note-plugin-test.xml"
})
public class SaveNoteAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private SaveNoteAPIController unit;
    private NoteEventPublisher mockEventPublisher;
    private Authentication mockAuthentication;


    private NoteDao mockNoteDao;
    private NoteEventPublisher mockNoteEventPublisher;


    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockNoteDao = createMock(NoteDao.class);
        mockNoteEventPublisher = createMock(NoteEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new SaveNoteAPIController();

        unit.setNoteDao(mockNoteDao);
        unit.setNoteEventPublisher(mockNoteEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit)
                .setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void addNote() throws Exception
    {
        Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Note incomingNote = new Note();

        incomingNote.setId(700L);
        incomingNote.setCreator("testCreator");
        incomingNote.setCreated(new Date());
        incomingNote.setNote("Note");
        incomingNote.setParentType(parentType);
        incomingNote.setParentId(parentId);


        Capture<Note> noteToSave = new Capture<>();
        Capture<ApplicationNoteEvent> capturedEvent = new Capture<>();

        expect(mockNoteDao.save(capture(noteToSave))).andReturn(incomingNote);
        mockNoteEventPublisher.publishNoteEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(incomingNote);

        log.debug("Input JSON: " + in);


        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/note")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        //log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(incomingNote.getId(), noteToSave.getValue().getId());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

    }
   
    @Test
    public void addNote_exception() throws Exception
    {
        Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Note incomingNote = new Note();

        incomingNote.setId(700L);
        incomingNote.setCreator("testCreator");
        incomingNote.setCreated(new Date());
        incomingNote.setNote("Note");
        incomingNote.setParentType(parentType);
        incomingNote.setParentId(parentId);


        Capture<Note> noteToSave = new Capture<>();
        Capture<ApplicationNoteEvent> capturedEvent = new Capture<>();

        expect(mockNoteDao.save(capture(noteToSave))).andThrow(new RuntimeException("testException"));
        mockNoteEventPublisher.publishNoteEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(incomingNote);

        log.debug("Input JSON: " + in);


        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/note")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        //log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(incomingNote.getId(), noteToSave.getValue().getId());
        //assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
}
