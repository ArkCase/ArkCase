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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import javax.persistence.QueryTimeoutException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-note-plugin-unit-test.xml"
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

    private Logger log = LogManager.getLogger(getClass());

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
    public void listNote() throws Exception
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
        assertEquals(fromReturnedNoteList.size(), 1);
        assertEquals(fromReturnedNoteList.get(0).getType(), type);
        assertEquals(fromReturnedNoteList.get(0).getParentId(), parentId);
        assertEquals(fromReturnedNoteList.get(0).getParentType(), parentType);
        assertEquals(fromReturnedNoteList.get(0).getObjectType(), NoteConstants.OBJECT_TYPE);

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

        expect(mockNoteDao.listNotes(type, parentId, parentType)).andThrow(new QueryTimeoutException("test exception"));
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

    @Test
    public void listPageNote() throws Exception
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

        expect(mockNoteDao.listNotesPage(type, parentId, parentType, 0, 10, "")).andReturn(noteList);
        expect(mockNoteDao.countAll(type, parentId, parentType)).andReturn(1);
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/note/{parentType}/{parentId}/page?start={start}&n={n}&s={s}", parentType, parentId, 0, 10, "")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        QueryResultPageWithTotalCount<Note> fromReturnedNoteList = mapper.readValue(
                returned,
                mapper.getTypeFactory().constructParametricType(QueryResultPageWithTotalCount.class, Note.class));

        assertNotNull(fromReturnedNoteList);
        assertEquals(fromReturnedNoteList.getTotalCount(), 1);
        assertEquals(fromReturnedNoteList.getResultPage().get(0).getType(), type);
        assertEquals(fromReturnedNoteList.getResultPage().get(0).getParentId(), parentId);
        assertEquals(fromReturnedNoteList.getResultPage().get(0).getParentType(), parentType);
        assertEquals(fromReturnedNoteList.getResultPage().get(0).getObjectType(), NoteConstants.OBJECT_TYPE);

        log.info("note size : ", fromReturnedNoteList.getTotalCount());
        log.info("note : ", fromReturnedNoteList.getResultPage().get(0).getNote());
    }

    @Test
    public void listPageNote_exception() throws Exception
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

        expect(mockNoteDao.listNotesPage(type, parentId, parentType, 0, 10, "")).andThrow(new QueryTimeoutException("test exception"));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/note/{parentType}/{parentId}/page?start={start}&n={n}&s={s}", parentType, parentId, 0, 10, "")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
    }
}
