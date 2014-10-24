package com.armedia.acm.services.note.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.note.dao.NoteDao;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;


@Controller
@RequestMapping({ "/api/v1/plugin/note", "/api/latest/plugin/note" })
public class DeleteNoteByIdAPIController {

    private NoteDao noteDao;
//MediaType.APPLICATION_JSON_VALUE
    private Logger log = LoggerFactory.getLogger(getClass());
    @RequestMapping(value = "/{noteId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteNoteById(
            @PathVariable("noteId") Long id

    ) throws AcmObjectNotFoundException, AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Finding note with ID: " + id);
        }
        if(id != null){
            try
            {
                JSONObject objectToReturnJSON = new JSONObject();
                getNoteDao().deleteNoteById(id);
                log.info("Deleting note by id '" + id + "'");
                log.debug("Note ID : " + id);

                objectToReturnJSON.put("deletedNoteId", id);

                String objectToReturn = "{\"deletedNoteId\":" + id + "}";
                objectToReturn = objectToReturnJSON.toString();

                return objectToReturn;
            }
            catch (PersistenceException e)
            {
                throw new AcmUserActionFailedException("Delete", "note", id, e.getMessage(), e);
            }
        }
        throw new AcmObjectNotFoundException ("Could not find note", id, "", null);
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




