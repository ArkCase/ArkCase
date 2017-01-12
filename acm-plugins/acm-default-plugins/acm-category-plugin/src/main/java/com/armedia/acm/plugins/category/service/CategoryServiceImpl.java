package com.armedia.acm.plugins.category.service;

import static com.armedia.acm.plugins.category.model.CategoryStatus.ACTIVATED;
import static com.armedia.acm.plugins.category.model.CategoryStatus.DEACTIVATED;
import static com.armedia.acm.plugins.category.model.CategoryStatus.DELETED;

import com.armedia.acm.plugins.category.dao.CategoryDao;
import com.armedia.acm.plugins.category.model.Category;
import com.armedia.acm.plugins.category.model.CategoryStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 11, 2017
 *
 */
@Transactional
public class CategoryServiceImpl implements CategoryService
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private CategoryDao categoryDao;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#get(java.lang.Long)
     */
    @Override
    public Category get(Long id)
    {
        // maybe it would be better to return java.util.Optional if there is no category for the given id?
        return categoryDao.find(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.plugins.category.service.CategoryService#create(com.armedia.acm.plugins.category.model.Category)
     */
    @Override
    public Category create(Category category)
    {
        // what happens if category with same name already exists?
        log.debug("Creating Category with name [{}].", category.getName());
        return categoryDao.save(category);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.plugins.category.service.CategoryService#update(com.armedia.acm.plugins.category.model.Category)
     */
    @Override
    public Category update(Category category)
    {
        if (category == null || category.getId() == null)
        {
            // throw an exception if here?
        }
        Category existing = get(category.getId());
        // check first if exists? throw an exception if doesn't?
        if (existing != null)
        {
            log.debug("Updating Category with id [{}].", category.getId());
            if (!category.getName().equals(existing.getName()))
            {
                log.debug("Updating 'name' property of [{}] Category with id [{}] to [{}].", existing.getName(), category.getId(),
                        category.getName());
                // audit edit category name changed
            }
            if (!category.getDescription().equals(existing.getDescription()))
            {
                log.debug("Updating 'description' property of [{}] Category with id [{}] from [{}] to [{}].", category.getName(),
                        category.getId(), existing.getDescription(), category.getDescription());
                // audit edit category description changed
            }
        }
        return categoryDao.save(category);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#delete(java.lang.Long)
     */
    @Override
    public Category delete(Long id)
    {
        // maybe it would be better to return java.util.Optional if there is no category for the given id?
        Category category = categoryDao.find(id);
        if (category != null)
        {
            // throw an exception if category doesn't exist?
            category.setStatus(DELETED);
            update(category);
            log.debug("Deleting [{}] Category with id [{}].", category.getName(), category.getId());
            setChildrenStatus(category, DELETED);
        }
        return category;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#activate(com.armedia.acm.plugins.category.model.
     * Category)
     */
    @Override
    public void activate(Category category)
    {
        if (category == null || category.getId() == null)
        {
            // throw an exception if here?
            return;
        }
        category = get(category.getId());
        if (category != null)
        {
            // throw an exception if category doesn't exist?
            category.setStatus(ACTIVATED);
            update(category);
            log.debug("Activating [{}] Category with id [{}].", category.getName(), category.getId());
            setChildrenStatus(category, ACTIVATED);
            activateAncestors(category);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#deactivate(com.armedia.acm.plugins.category.model.
     * Category)
     */
    @Override
    public void deactivate(Category category)
    {
        if (category == null || category.getId() == null)
        {
            // throw an exception if here?
            return;
        }
        category = get(category.getId());
        if (category != null)
        {
            // throw an exception if category doesn't exist?
            category.setStatus(DEACTIVATED);
            update(category);
            log.debug("Deactivating [{}] Category with id [{}].", category.getName(), category.getId());
            setChildrenStatus(category, DEACTIVATED);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#getRoot()
     */
    @Override
    public List<Category> getRoot()
    {
        return categoryDao.findAllWithoutParent();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#getParent(java.lang.Long)
     */
    @Override
    public Category getParent(Long id)
    {
        Category category = get(id);
        // return java.util.Optional if parent is null?
        return category.getParent();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#getChildren(java.lang.Long)
     */
    @Override
    public List<Category> getChildren(Long id)
    {
        // throw an exception if category for the given id does not exist?
        // do we retrieve only the first generation of children, or the children's children as well?
        return categoryDao.getChildren(id);
    }

    /**
     * @param category
     * @param status
     */
    private void setChildrenStatus(Category category, CategoryStatus status)
    {
        List<Category> children = getChildren(category.getId());
        for (Category child : children)
        {
            child.setStatus(status);
            child = update(child);
            log.debug("{} [{}] Category with id [{}].", getStatusVerb(status), child.getName(), child.getId());
            setChildrenStatus(child, status);
        }
    }

    /**
     * @param status
     * @return
     */
    private String getStatusVerb(CategoryStatus status)
    {
        switch (status)
        {
        case ACTIVATED:
            return "Activating";
        case DEACTIVATED:
            return "Deactivating";
        case DELETED:
            return "Deleting";
        default:
            return "";
        }
    }

    /**
     * @param category
     */
    private void activateAncestors(Category category)
    {
        Category parent = category.getParent();
        if (parent != null)
        {
            parent.setStatus(ACTIVATED);
            parent = update(parent);
            activateAncestors(parent);
        }
    }

    /**
     * @param categoryDao the categoryDao to set
     */
    public void setCategoryDao(CategoryDao categoryDao)
    {
        this.categoryDao = categoryDao;
    }

}
