package com.armedia.acm.plugins.category.service;

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

    Category create(Long parentId, Category category) throws AcmCreateObjectFailedException;

    Category update(Category category) throws AcmObjectNotFoundException, AcmUpdateObjectFailedException;

    Category delete(Long id) throws AcmObjectNotFoundException;

    void activate(Long categoryId, boolean activateChildren) throws AcmObjectNotFoundException;

    void deactivate(Long categoryId) throws AcmObjectNotFoundException;

    List<Category> getRoot();

    Category getParent(Long id) throws AcmObjectNotFoundException;

    List<Category> getChildren(Long id) throws AcmObjectNotFoundException;

}
