package com.armedia.acm.services.note.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.service.NoteEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/note", "/api/latest/plugin/note" })
public class ListAllNotesAPIController {

    private NoteDao noteDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/list/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Note> findAllNotes(
            @PathVariable("parentType") String parentType,
            @PathVariable("parentId") Long parentId
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmListObjectsFailedException {
        if (log.isInfoEnabled()) {
            log.info("Finding all notes");
        }
        if(parentId != null && parentType != null){
            try {
                List<Note> noteList = getNoteDao().listNotes(parentId, parentType);
                log.debug("noteList size " + noteList.size());
                return noteList;
            } catch (PersistenceException e) {
                throw new AcmListObjectsFailedException("p", e.getMessage(), e);
            }
        }
        throw new AcmListObjectsFailedException("wrong input", "user: ", null);
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