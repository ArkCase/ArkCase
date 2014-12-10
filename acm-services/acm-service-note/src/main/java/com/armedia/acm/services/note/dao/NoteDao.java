package com.armedia.acm.services.note.dao;


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.note.model.Note;
import com.google.common.base.Preconditions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class NoteDao extends AcmAbstractDao<Note>
{

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

        List<Note> notes = ( List<Note> ) note.getResultList();
        if (null == notes) {
            notes = new ArrayList();
        }
        return notes;
    }

    @Transactional
    public void deleteNoteById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT note " +"FROM Note note " +
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


