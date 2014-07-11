package com.armedia.acm.data;


import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AcmAbstractDao<T>
{
    @PersistenceContext
    private EntityManager em;

    public T save(T toSave)
    {
        T saved = em.merge(toSave);
        em.persist(saved);
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T find(Class<? extends T> cls, Long id)
    {
        T found = em.find(cls, id);
        if ( found != null )
        {
            em.refresh(found);
        }

        return found;
    }

    public EntityManager getEm()
    {
        return em;
    }
}
