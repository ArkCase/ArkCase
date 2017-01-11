package com.armedia.acm.plugins.category.service;

import com.armedia.acm.plugins.category.model.Category;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 11, 2017
 *
 */
public interface CategoryService
{

    Category get(Long id);

    Category create(Category category);

    Category update(Category category);

    Category delete(Long id);

    void activate(Category category);

    void deactivate(Category category);

    List<Category> getRoot();

    Category getParent(Long id);

    List<Category> getChildren(Long id);

}
