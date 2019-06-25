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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.note.service.NoteEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/plugin/note", "/api/latest/plugin/note" })
public class SaveNoteAPIController
{

    private NoteDao noteDao;
    private NoteEventPublisher noteEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @PreAuthorize("hasPermission(#note.parentId, #note.parentType, 'addComment')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Note addNote(
            @RequestBody Note note, Authentication authentication, HttpSession httpSession) throws AcmUserActionFailedException
    {
        if (note != null)
        {
            log.info("Note ID : {}", note.getId());
        }
        else
        {
            throw new AcmUserActionFailedException("addNote", NoteConstants.OBJECT_TYPE, null,
                    "Could not save note, missing parent type and ID", new NullPointerException());
        }

        try
        {
            Note savedNote = getNoteDao().save(note);

            String noteEvent = note.getId() == null ? NoteConstants.NOTE_ADDED : NoteConstants.NOTE_UPDATED;
            if (savedNote.getType().equals(NoteConstants.NOTE_REJECT_COMMENT))
            {
                noteEvent = String.format("rejectcomment.%s", noteEvent);
            }
            publishNoteEvent(httpSession, savedNote, noteEvent, true);

            return savedNote;
        }
        catch (Exception e)
        {
            // Create a fake note to audit the failure.
            Note fakeNote = new Note();
            fakeNote.setId(note.getId());
            log.info("fake id : ()", fakeNote.getId());
            log.info("fake id 2: {}", note.getId());

            fakeNote.setParentId(note.getParentId());
            fakeNote.setParentType(note.getParentType());
            fakeNote.setParentTitle(note.getParentTitle());
            fakeNote.setNote(note.getNote());
            fakeNote.setCreator(note.getCreator());
            fakeNote.setCreated(note.getCreated());
            fakeNote.setType(NoteConstants.NOTE_GENERAL);
            fakeNote.setTag(note.getTag());

            String noteEvent = note.getId() == null ? NoteConstants.NOTE_ADDED : NoteConstants.NOTE_UPDATED;

            if (fakeNote.getType().equals(NoteConstants.NOTE_REJECT_COMMENT))
            {
                noteEvent = String.format("rejectcomment.%s", noteEvent);
            }

            publishNoteEvent(httpSession, fakeNote, noteEvent, false);
            throw new AcmUserActionFailedException("unable to add note from ", note.getParentType(), note.getParentId(), e.getMessage(), e);
        }
    }

    protected void publishNoteEvent(HttpSession httpSession, Note note, String eventType, boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        ApplicationNoteEvent event = new ApplicationNoteEvent(note, eventType, succeeded, ipAddress);
        getNoteEventPublisher().publishNoteEvent(event);
    }

    public NoteEventPublisher getNoteEventPublisher()
    {
        return noteEventPublisher;
    }

    public void setNoteEventPublisher(NoteEventPublisher noteEventPublisher)
    {
        this.noteEventPublisher = noteEventPublisher;
    }

    public NoteDao getNoteDao()
    {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }

}
