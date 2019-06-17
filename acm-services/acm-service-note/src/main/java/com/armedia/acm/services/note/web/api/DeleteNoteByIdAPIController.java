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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.ApplicationNoteEvent;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.service.NoteEventPublisher;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/plugin/note", "/api/latest/plugin/note" })
public class DeleteNoteByIdAPIController
{

    private NoteDao noteDao;
    private NoteEventPublisher noteEventPublisher;
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/{noteId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteNoteById(
            @PathVariable("noteId") Long id,
            HttpSession session

    ) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        log.info("Finding note with ID : [{}]", id);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        if (id != null)
        {
            Note note = getNoteDao().find(id);
            try
            {
                log.info("Deleting note by ID : [{}]", id);

                JSONObject objectToReturnJSON = new JSONObject();
                getNoteDao().deleteNoteById(id);

                ApplicationNoteEvent event = new ApplicationNoteEvent(note, "deleted", true, ipAddress);
                getNoteEventPublisher().publishNoteEvent(event);

                objectToReturnJSON.put("deletedNoteId", id);

                String objectToReturn;
                objectToReturn = objectToReturnJSON.toString();

                return objectToReturn;
            }
            catch (PersistenceException e)
            {
                ApplicationNoteEvent event = new ApplicationNoteEvent(note, "deleted", false, ipAddress);
                getNoteEventPublisher().publishNoteEvent(event);
                throw new AcmUserActionFailedException("Delete", "note", id, e.getMessage(), e);
            }
        }
        throw new AcmObjectNotFoundException("Note", id, "Could not find note", null);
    }

    public NoteDao getNoteDao()
    {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }

    public NoteEventPublisher getNoteEventPublisher()
    {
        return noteEventPublisher;
    }

    public void setNoteEventPublisher(NoteEventPublisher noteEventPublisher)
    {
        this.noteEventPublisher = noteEventPublisher;
    }
}
