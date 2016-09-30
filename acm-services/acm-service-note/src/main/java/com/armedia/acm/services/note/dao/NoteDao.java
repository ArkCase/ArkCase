package com.armedia.acm.services.note.dao;


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.note.model.Note;
import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class NoteDao extends AcmAbstractDao<Note>
{
    Logger LOG = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    protected Class<Note> getPersistenceClass()
    {
        return Note.class;
    }

    public List<Note> listNotes(String type, Long parentId, String parentType)
    {
        Preconditions.checkNotNull(type, "Note type cannot be null");
        Preconditions.checkNotNull(parentId, "Parent Id cannot be null");
        Preconditions.checkNotNull(parentType, "Parent type cannot be null");

        Query note = getEntityManager().createQuery(
                "SELECT note " +
                        "FROM Note note " +
                        "WHERE note.parentId = :parentId AND " +
                        "note.parentType  = :parentType AND " +
                        "note.type = :type ORDER BY note.created DESC");

        note.setParameter("type", type);
        note.setParameter("parentType", parentType.toUpperCase());
        note.setParameter("parentId", parentId);

        List<Note> notes = (List<Note>) note.getResultList();
        if (null == notes)
        {
            notes = new ArrayList();
        }
        return notes;
    }
    
    public List<Note> listNotesPage(String type, Long parentId, String parentType, int start, int n, String s)
    {
        Preconditions.checkNotNull(type, "Note type cannot be null");
        Preconditions.checkNotNull(parentId, "Parent Id cannot be null");
        Preconditions.checkNotNull(parentType, "Parent type cannot be null");
        Preconditions.checkNotNull(start, "Start cannot be null");
        Preconditions.checkNotNull(n, "N cannot be null");
        Preconditions.checkNotNull(s, "S cannot be null");

        String sortBy = "created";
        String sortDirection = "DESC";
        
        if (!"".equals(s.trim()))
        {
            String[] sArray = s.split(" ");
            if (sArray.length == 1)
            {
                sortBy = sArray[0];
            }
            else if (sArray.length == 2)
            {
                sortBy = sArray[0];
                sortDirection = sArray[1];
            }
        }
        
        Query note = getEntityManager().createQuery(
                "SELECT note " +
                        "FROM Note note " +
                        "WHERE note.parentId = :parentId AND " +
                        "note.parentType  = :parentType AND " +
                        "note.type = :type ORDER BY note." + sortBy + " " + sortDirection);

        note.setParameter("type", type);
        note.setParameter("parentType", parentType.toUpperCase());
        note.setParameter("parentId", parentId);
        note.setFirstResult(start);
        note.setMaxResults(n);
        

        List<Note> notes = (List<Note>) note.getResultList();
        if (null == notes)
        {
            notes = new ArrayList();
        }
        return notes;
    }
    
    public int countAll(String type, Long parentId, String parentType)
    {
        String queryText = "SELECT COUNT(note) " +
                "FROM Note note " +
                "WHERE note.parentId = :parentId AND " +
                "note.parentType  = :parentType AND " +
                "note.type = :type";

        Query query = getEm().createQuery(queryText);
        query.setParameter("type", type);
        query.setParameter("parentType", parentType.toUpperCase());
        query.setParameter("parentId", parentId);

        Long count = 0L;
        
        try
        {
            count = (Long) query.getSingleResult();
        }
        catch (Exception e) 
        {
            LOG.debug("There are no any results.");
        }

        return count.intValue();
    }

    @Transactional
    public void deleteNoteById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT note " + "FROM Note note " +
                        "WHERE note.id = :noteId"
        );
        queryToDelete.setParameter("noteId", id);

        Note noteToBeDeleted = (Note) queryToDelete.getSingleResult();
        entityManager.remove(noteToBeDeleted);
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}


