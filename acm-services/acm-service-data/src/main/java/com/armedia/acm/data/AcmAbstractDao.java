package com.armedia.acm.data;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
        return saved;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public T find(Long id)
    {
        T found = em.find(getPersistenceClass(), id);
        return found;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> findAll()
    {
        TypedQuery<T> allRecords = em.createQuery("SELECT e FROM " + getPersistenceClass().getSimpleName() + " e", getPersistenceClass());
        List<T> retval = allRecords.getResultList();
        return retval;
    }

    /**
     * Retrieve all entities of a given type, sorted by particular column
     *
     * @param column
     *            column name (entity field name) to sort by
     * @return list of entities, sorted
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> findAllOrderBy(String column)
    {
        TypedQuery<T> allRecords = em.createQuery("SELECT e FROM " + getPersistenceClass().getSimpleName() + " e order by e." + column,
                getPersistenceClass());
        List<T> retval = allRecords.getResultList();
        return retval;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> findModifiedSince(Date lastModified, int startRow, int pageSize)
    {
        TypedQuery<T> sinceWhen = getEm().createQuery("SELECT e " + "FROM " + getPersistenceClass().getSimpleName() + " e "
                + "WHERE e.modified >= :lastModified " + "ORDER BY e.created", getPersistenceClass());
        sinceWhen.setParameter("lastModified", lastModified);
        sinceWhen.setFirstResult(startRow);
        sinceWhen.setMaxResults(pageSize);

        List<T> retval = sinceWhen.getResultList();
        return retval;
    }

    protected abstract Class<T> getPersistenceClass();

    /**
     * This method should be implemented under appropriate DAO. It should return OBJECT_TYPE
     *
     * @return
     */
    public String getSupportedObjectType()
    {
        return null;
    }

    public EntityManager getEm()
    {
        return em;
    }
}
