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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/note", "/api/latest/plugin/note" })
public class ListAllNotesAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private NoteDao noteDao;

    @RequestMapping(value = "/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Note> findAllNotesInParentObject(
            @PathVariable("parentType") String parentType,
            @PathVariable("parentId") Long parentId,
            @RequestParam(value = "type", required = false, defaultValue = "GENERAL") String type)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmListObjectsFailedException
    {
        log.info("Finding all notes");
        if (parentId != null && parentType != null)
        {
            try
            {
                List<Note> noteList = getNoteDao().listNotes(type, parentId, parentType);
                log.debug("noteList size:{}", noteList.size());
                return noteList;
            }
            catch (PersistenceException e)
            {
                throw new AcmListObjectsFailedException(NoteConstants.OBJECT_TYPE, e.getMessage(), e);
            }
        }
        throw new AcmListObjectsFailedException(NoteConstants.OBJECT_TYPE, "wrong input", null);
    }

    @RequestMapping(value = "/{parentType}/{parentId}/page", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public QueryResultPageWithTotalCount<Note> findPageNotesInParentObject(
            @PathVariable("parentType") String parentType,
            @PathVariable("parentId") Long parentId,
            @RequestParam(value = "type", required = false, defaultValue = "GENERAL") String type,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false) String s)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmListObjectsFailedException
    {
        log.info("Finding all notes paged");
        if (parentId != null && parentType != null)
        {
            try
            {
                if (type.toUpperCase().equals("ALL"))
                {
                    // we will not filter by type
                    type = null;
                }
                List<Note> noteList = getNoteDao().listNotesPage(type, parentId, parentType, start, n, s);
                log.debug("noteList size:{}", noteList.size());
                int totalCount = getNoteDao().countAll(type, parentId, parentType);
                log.debug("total size:{}", totalCount);

                QueryResultPageWithTotalCount<Note> retval = new QueryResultPageWithTotalCount<>();
                retval.setStartRow(start);
                retval.setMaxRows(n);
                retval.setTotalCount(totalCount);
                retval.setResultPage(noteList);
                return retval;
            }
            catch (PersistenceException e)
            {
                throw new AcmListObjectsFailedException(NoteConstants.OBJECT_TYPE, e.getMessage(), e);
            }
        }
        throw new AcmListObjectsFailedException(NoteConstants.OBJECT_TYPE, "wrong input", null);
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
