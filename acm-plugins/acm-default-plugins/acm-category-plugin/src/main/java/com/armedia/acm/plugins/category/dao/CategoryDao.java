package com.armedia.acm.plugins.category.dao;

import static com.armedia.acm.plugins.category.model.Category.FIND_CHILDREN;
import static com.armedia.acm.plugins.category.model.Category.FIND_ROOT_CATEGORIES;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.category.model.Category;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 11, 2017
 *
 */
@Transactional
public class CategoryDao extends AcmAbstractDao<Category>
{

    public List<Category> findAllWithoutParent()
    {
        return getEm().createNamedQuery(FIND_ROOT_CATEGORIES, Category.class).getResultList();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.data.AcmAbstractDao#getPersistenceClass()
     */
    @Override
    protected Class<Category> getPersistenceClass()
    {
        return Category.class;
    }

    /**
     * @param id
     * @return
     */
    public List<Category> getChildren(Long id)
    {
        return getEm().createNamedQuery(FIND_CHILDREN, Category.class).setParameter("parentId", id).getResultList();
    }

}
