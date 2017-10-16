package com.armedia.acm.services.note.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.exception.AcmNoteException;
import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.note.service.NoteEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping({"/api/v1/plugin/note", "/api/latest/plugin/note"})
public class SaveNoteAPIController
{

    private NoteDao noteDao;
    private NoteEventPublisher noteEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

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

        try
        {
            if (note == null)
            {
                throw new AcmNoteException("Could not save note, missing parent type and ID");
            }

            Note savedNote = getNoteDao().save(note);

            String noteEvent = note.getId() == null ? NoteConstants.NOTE_ADDED : NoteConstants.NOTE_UPDATED;
            if (savedNote.getType().equals(NoteConstants.NOTE_REJECT_COMMENT))
            {
                noteEvent = String.format("rejectcomment.%s", noteEvent);
            }
            publishNoteEvent(httpSession, savedNote, noteEvent, true);

            return savedNote;
        } catch (Exception e)
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

