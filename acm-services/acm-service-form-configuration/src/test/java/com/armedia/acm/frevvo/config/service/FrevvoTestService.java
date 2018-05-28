package com.armedia.acm.frevvo.config.service;

/*-
 * #%L
 * ACM Service: Form Configuration
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

import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by armdev on 11/3/14.
 */
public class FrevvoTestService extends FrevvoFormAbstractService
{
    @Override
    public Object init()
    {
        return null;
    }

    @Override
    public Object get(String action)
    {
        return null;
    }

    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        return false;
    }

    @Override
    public String getFormName()
    {
        return "test";
    }

    @Override
    public Class<?> getFormClass()
    {
        // Implementation no needed so far
        return null;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        // Implementation no needed so far
        return null;
    }
}
