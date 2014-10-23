package com.armedia.acm.services.note.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.exception.AcmNoteException;
import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.service.NoteEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/plugin/note", "/api/latest/plugin/note" })
public class SaveNoteAPIController
{


    private NoteDao noteDao;
    private NoteEventPublisher noteEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Note addNote(
            @RequestBody Note note,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {

            if(note != null){
                log.info("Note ID" + note.getId());
                log.info("Note parentType" + note.getParentType());
                log.info("Note parentId" + note.getParentId());
                log.info("Note " + note.getNote());
                log.info("Note created" + note.getCreated());
                log.info("Note creator" + note.getCreated());
            }
        }

        try
        {
            if(note == null)
            {
                throw new AcmNoteException("Could not save note, missing parent type and ID");
            }

            // to db
            Note newNote = new Note();
            newNote.setId(note.getId());
            newNote.setParentId(note.getParentId());
            newNote.setParentType(note.getParentType());
            newNote.setNote(note.getNote());
            newNote.setCreator(note.getCreator());
            newNote.setCreated(note.getCreated());

            Note savedNote = getNoteDao().save(note);

            publishNoteEvent(httpSession, savedNote, true);

            return savedNote;
        }
        catch (Exception e)
        {
            // gen up a fake task so we can audit the failure
            Note fakeNote = new Note();
            fakeNote.setId(note.getId());
            log.info("fake id : " + fakeNote.getId());
            log.info("fake id 2: " + note.getId());

            fakeNote.setParentId(note.getParentId());
            fakeNote.setParentType(note.getParentType());
            fakeNote.setNote(note.getNote());
            fakeNote.setCreator(note.getCreator());
            fakeNote.setCreated(note.getCreated());

            publishNoteEvent(httpSession, fakeNote, false);
            throw new AcmUserActionFailedException("unable to add note from ", note.getParentType(), note.getParentId(), e.getMessage(), e);
        }
    }

    protected void publishNoteEvent(
            HttpSession httpSession,
            Note note,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        ApplicationNoteEvent event = new ApplicationNoteEvent(note, "note", succeeded, ipAddress);
        getNoteEventPublisher().publishNoteEvent(event);
    }

    public NoteEventPublisher getNoteEventPublisher() {
        return noteEventPublisher;
    }

    public void setNoteEventPublisher(
            NoteEventPublisher noteEventPublisher) {
        this.noteEventPublisher = noteEventPublisher;
    }
    public NoteDao getNoteDao() {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

}

