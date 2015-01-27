package com.armedia.acm.data;


import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public abstract class AcmAbstractDao<T>
{
    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRED)
    public T save(T toSave)
    {
        T saved = em.merge(toSave);
        em.persist(saved);
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T find(Long id)
    {
        T found = em.find(getPersistenceClass(), id);
        if ( found != null )
        {
            em.refresh(found);
            em.detach(found);
        }
        return found;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<T> findAll() {
        Query allRecords = em.createQuery("SELECT e FROM " + getPersistenceClass().getSimpleName()+" e");
        List<T> retval = allRecords.getResultList();
        if(retval!=null){
            for(T value: retval) {
                em.refresh(value);
                em.detach(value);
            }
        }
        return retval;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<T> findModifiedSince(Date lastModified, int startRow, int pageSize)
    {
        Query sinceWhen = getEm().createQuery(
                "SELECT e " +
                "FROM " + getPersistenceClass().getSimpleName() + " e " +
                "WHERE e.modified >= :lastModified " +
                "ORDER BY e.created");
        sinceWhen.setParameter("lastModified", lastModified);
        sinceWhen.setFirstResult(startRow);
        sinceWhen.setMaxResults(pageSize);

        List<T> retval = sinceWhen.getResultList();
        return retval;
    }


    protected abstract Class<T> getPersistenceClass();

    public EntityManager getEm()
    {
        return em;
    }
}
