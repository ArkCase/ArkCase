package com.armedia.acm.plugins.dashboard.dao;

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
