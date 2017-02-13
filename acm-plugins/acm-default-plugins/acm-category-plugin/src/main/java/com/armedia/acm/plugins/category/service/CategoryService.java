package com.armedia.acm.plugins.category.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.category.model.Category;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 11, 2017
 *
 */
public interface CategoryService
{

    Category get(Long id) throws AcmObjectNotFoundException;

    Category create(Category category) throws AcmCreateObjectFailedException;

    Category createSubcategory(Long parentId, Category category) throws AcmCreateObjectFailedException;

    Category update(Category category) throws AcmObjectNotFoundException, AcmUpdateObjectFailedException;

    Category delete(Long id) throws AcmObjectNotFoundException;

    void activate(Category category) throws AcmObjectNotFoundException;

    void deactivate(Category category) throws AcmObjectNotFoundException;

    List<Category> getRoot();

    Category getParent(Long id) throws AcmObjectNotFoundException;

    List<Category> getChildren(Long id) throws AcmObjectNotFoundException;

}
