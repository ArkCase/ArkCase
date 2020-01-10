package com.armedia.acm.services.note.web.api;

/*-
 * #%L
 * ACM Service: Note
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.note.service.NoteEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-note-plugin-unit-test.xml"
})
public class SaveNoteAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private SaveNoteAPIController unit;
    private Authentication mockAuthentication;

    private NoteDao mockNoteDao;
    private NoteEventPublisher mockNoteEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

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
        Note incomingNote = createNote(NoteConstants.NOTE_GENERAL);
        executeTest(incomingNote);
    }

    @Test
    public void addRejectComment() throws Exception
    {
        Note incomingNote = createNote(NoteConstants.NOTE_REJECT_COMMENT);
        executeTest(incomingNote);
    }

    public void executeTest(Note incomingNote) throws Exception
    {
        Capture<Note> noteToSave = EasyMock.newCapture();
        Capture<ApplicationNoteEvent> capturedEvent = EasyMock.newCapture();

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

        Capture<Note> noteToSave = EasyMock.newCapture();
        Capture<ApplicationNoteEvent> capturedEvent = EasyMock.newCapture();

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

        // log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(incomingNote.getId(), noteToSave.getValue().getId());
        // assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    public Note createNote(String noteType)
    {
        Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Note incomingNote = new Note();

        incomingNote.setId(700L);
        incomingNote.setCreator("testCreator");
        incomingNote.setAuthor("testCreator");
        incomingNote.setCreated(new Date());
        incomingNote.setNote("Note");
        incomingNote.setParentType(parentType);
        incomingNote.setParentId(parentId);
        incomingNote.setType(noteType);
        return incomingNote;
    }
}
