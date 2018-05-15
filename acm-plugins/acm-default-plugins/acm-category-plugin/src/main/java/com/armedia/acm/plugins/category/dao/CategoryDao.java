package com.armedia.acm.plugins.category.dao;

/*-
 * #%L
 * ACM Default Plugin: Categories
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

import static com.armedia.acm.plugins.category.model.Category.FIND_CHILDREN;
import static com.armedia.acm.plugins.category.model.Category.FIND_ROOT_CATEGORIES;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.category.model.Category;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 11, 2017
 *
 */
public class CategoryDao extends AcmAbstractDao<Category>
{

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Category find(Long id)
    {
        return getEm().find(getPersistenceClass(), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Category> findAllWithoutParent()
    {
        return getEm().createNamedQuery(FIND_ROOT_CATEGORIES, Category.class).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.AcmAbstractDao#getPersistenceClass()
     */
    @Override
    protected Class<Category> getPersistenceClass()
    {
        return Category.class;
    }

    // TODO: we might remove this method if Category.getChildren() method returns properly regardless of the fact that
    // Category instance is detached from
    // the EntityMeanger due to the way the AcmAbstractDao.find(Long id) method is implemented.
    /**
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Category> getChildren(Long id)
    {
        return getEm().createNamedQuery(FIND_CHILDREN, Category.class).setParameter("parentId", id).getResultList();
    }

}
