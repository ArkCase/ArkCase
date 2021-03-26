package gov.foia.model.provider;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.plugins.task.model.AcmTask;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIATaskRequestModel;

public class FOIATaskRequestTemplateModelProvider implements TemplateModelProvider<FOIATaskRequestModel>
{

    private FOIARequestDao foiaRequestDao;

    @Override
    public FOIATaskRequestModel getModel(Object object)
    {
        AcmTask task = (AcmTask) object;
        FOIATaskRequestModel model = new FOIATaskRequestModel();

        if(task.getParentObjectId() != null)
        {
            FOIARequest request = foiaRequestDao.find(task.getParentObjectId());
            if(request != null)
            {
                model.setRequest(request);
            }
        }

        model.setTask(task);

        return model;
    }

    @Override
    public Class<FOIATaskRequestModel> getType()
    {
        return FOIATaskRequestModel.class;
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }
}
