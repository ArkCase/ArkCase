package com.armedia.acm.plugins.category.service;

import static com.armedia.acm.plugins.category.model.CategoryStatus.ACTIVATED;
import static com.armedia.acm.plugins.category.model.CategoryStatus.DEACTIVATED;
import static com.armedia.acm.plugins.category.model.CategoryStatus.DELETED;
import static java.util.Objects.isNull;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.category.dao.CategoryDao;
import com.armedia.acm.plugins.category.model.Category;
import com.armedia.acm.plugins.category.model.CategoryStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Category get(Long id) throws AcmObjectNotFoundException
    {
        return Optional.of(categoryDao.find(id))
                .orElseThrow(() -> new AcmObjectNotFoundException("Category", id, String.format("Category with id %n not found.", id)));
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
        if (checkNameCollision(category))
        {
            // throw an exception signaling category name collision.
        }
        return categoryDao.save(category);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.plugins.category.service.CategoryService#update(com.armedia.acm.plugins.category.model.Category)
     */
    @Override
    public Category update(Category category) throws AcmObjectNotFoundException
    {
        if (category == null || category.getId() == null)
        {
            // throw an exception if here?
        }

        // check first if exists? throw an exception if doesn't?
        Category existing = get(category.getId());

        log.debug("Updating Category with id [{}].", category.getId());
        if (!category.getName().equals(existing.getName()))
        {
            if (checkNameCollision(category))
            {
                log.warn("Name collision while trying to update [{}] Category with id [{}] to [{}].", existing.getName(), category.getId(),
                        category.getName());
                // throw an exception signaling category name collision.
            }
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

        return categoryDao.save(category);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#delete(java.lang.Long)
     */
    @Override
    public Category delete(Long id) throws AcmObjectNotFoundException
    {

        if (id == null)
        {
            // throw an exception if here?
        }

        Category category = get(id);

        category.setStatus(DELETED);
        log.debug("Deleting [{}] Category with id [{}].", category.getName(), category.getId());
        setChildrenStatus(category, DELETED);
        update(category);

        return category;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#activate(com.armedia.acm.plugins.category.model.
     * Category)
     */
    @Override
    public void activate(Category category) throws AcmObjectNotFoundException
    {
        if (category == null || category.getId() == null)
        {
            // throw an exception if here?
            return;
        }

        category = get(category.getId());
        category.setStatus(ACTIVATED);
        log.debug("Activating [{}] Category with id [{}].", category.getName(), category.getId());
        setChildrenStatus(category, ACTIVATED);
        update(category);
        activateAncestors(category);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#deactivate(com.armedia.acm.plugins.category.model.
     * Category)
     */
    @Override
    public void deactivate(Category category) throws AcmObjectNotFoundException
    {
        if (category == null || category.getId() == null)
        {
            // throw an exception if here?
            return;
        }

        category = get(category.getId());
        category.setStatus(DEACTIVATED);
        log.debug("Deactivating [{}] Category with id [{}].", category.getName(), category.getId());
        setChildrenStatus(category, DEACTIVATED);
        update(category);

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
    public Category getParent(Long id) throws AcmObjectNotFoundException
    {
        Category category = get(id);
        // return java.util.Optional if parent is null?
        // return Optional.ofNullable(category.getParent());
        return category.getParent();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.plugins.category.service.CategoryService#getChildren(java.lang.Long)
     */
    @Override
    public List<Category> getChildren(Long id) throws AcmObjectNotFoundException
    {
        Category parent = get(id);
        return parent.getChildren();
    }

    private boolean checkNameCollision(Category category)
    {
        List<Category> categories = isNull(category.getParent()) ? getRoot() : category.getParent().getChildren();
        return categories.stream().map(cat -> cat.getName()).noneMatch(name -> name.equalsIgnoreCase(category.getName()));
    }

    /**
     * @param category
     * @param status
     * @throws AcmObjectNotFoundException
     */
    private void setChildrenStatus(Category category, CategoryStatus status)
    {
        List<Category> children = category.getChildren();
        for (Category child : children)
        {
            child.setStatus(status);
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
     * @throws AcmObjectNotFoundException
     */
    private void activateAncestors(Category category) throws AcmObjectNotFoundException
    {
        Category parent = category.getParent();
        if (parent != null)
        {
            parent.setStatus(ACTIVATED);
            activateAncestors(parent);
        } else
        {
            // test if cascade on update will update all children, otherwise update on every level will be needed.
            update(category);
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
