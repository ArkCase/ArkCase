package com.armedia.acm.data;


import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    protected abstract Class<T> getPersistenceClass();

    public EntityManager getEm()
    {
        return em;
    }
}
