package com.armedia.acm.plugins.dashboard.dao;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.dashboard.model.module.Module;

import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by marjan.stefanoski on 15.01.2016.
 */

public class ModuleDao extends AcmAbstractDao<Module>
{
    public Module getModuleByName(String moduleName) throws AcmObjectNotFoundException
    {

        String queryString = "SELECT m FROM Module m WHERE m.moduleName = :moduleName";
        TypedQuery<Module> query = getEm().createQuery(queryString, Module.class);
        query.setParameter("moduleName", moduleName);

        List<Module> results = query.getResultList();

        if (results.isEmpty())
        {
            throw new AcmObjectNotFoundException("User Preference", null, "Object not found", null);
        }

        return results.get(0);
    }

    @Override
    protected Class<Module> getPersistenceClass()
    {
        return Module.class;
    }
}
