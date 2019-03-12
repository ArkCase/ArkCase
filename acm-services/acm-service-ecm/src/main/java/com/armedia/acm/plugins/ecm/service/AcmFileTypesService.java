package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;

import java.util.Set;

/**
 * Created by admin on 6/12/15.
 */
public interface AcmFileTypesService
{
    String PROP_FORM_NAME_TPL = "%s.name";
    String PROP_FORM_TYPE_TPL = "%s.type";
    String PROP_FORM_MODE_TPL = "%s.mode";

    Set<String> getFileTypes() throws AcmFileTypesException;

    Set<String> getForms() throws AcmFileTypesException;

}
