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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.service.NoteEventPublisher;

import org.easymock.Capture;
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

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-note-plugin-unit-test.xml"
})

public class DeleteNoteByIdAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private DeleteNoteByIdAPIController unit;
    private NoteEventPublisher mockNoteEventPublisher;

    private NoteDao mockNoteDao;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
    private Logger log = LogManager.getLogger(getClass());
    private Note mockNote;

    @Before
    public void setUp() throws Exception
    {
        mockNote = createMock(Note.class);
        mockNoteDao = createMock(NoteDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockNoteEventPublisher = createMock(NoteEventPublisher.class);

        unit = new DeleteNoteByIdAPIController();

        unit.setNoteDao(mockNoteDao);
        unit.setNoteEventPublisher(mockNoteEventPublisher);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void deleteNoteById() throws Exception
    {
        Long noteId = 700L;
        Long parentId = 800L;
        expect(mockNote.getId()).andReturn(noteId);
        expect(mockNote.getCreated()).andReturn(new Date());
        expect(mockNote.getModifier()).andReturn("user");
        expect(mockNote.getParentId()).andReturn(parentId);
        expect(mockNote.getParentType()).andReturn("PARENT_TYPE");

        expect(mockNoteDao.find(eq(noteId))).andReturn(mockNote);

        mockNoteDao.deleteNoteById(eq(noteId));
        expectLastCall();

        Capture<ApplicationNoteEvent> capturedEvent = newCapture();
        mockNoteEventPublisher.publishNoteEvent(capture(capturedEvent));
        expectLastCall();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();
        // .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))

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
        assertEquals(capturedEvent.getValue().getSource(), mockNote);

    }

    @Test
    public void deleteNoteById_notFound() throws Exception
    {

        Long noteId = 234L;
        Long parentId = 800L;
        expect(mockNote.getId()).andReturn(noteId);
        expect(mockNote.getCreated()).andReturn(new Date());
        expect(mockNote.getModifier()).andReturn("user");
        expect(mockNote.getParentId()).andReturn(parentId);
        expect(mockNote.getParentType()).andReturn("PARENT_TYPE");

        expect(mockNoteDao.find(eq(noteId))).andReturn(mockNote);

        mockNoteDao.deleteNoteById(eq(noteId));
        expectLastCall();

        Capture<ApplicationNoteEvent> capturedEvent = newCapture();
        mockNoteEventPublisher.publishNoteEvent(capture(capturedEvent));
        expectLastCall();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                delete("/api/latest/plugin/note/{noteId}", noteId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication));

        verifyAll();
    }
}
