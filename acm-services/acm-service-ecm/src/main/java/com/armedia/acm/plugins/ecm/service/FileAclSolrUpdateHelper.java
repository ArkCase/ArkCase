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

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.dataaccess.model.DataAccessControlConfig;
import com.armedia.acm.services.dataaccess.service.SearchAccessControlFields;
import com.armedia.acm.services.participants.model.AcmAssignedObject;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.FlushModeType;

import java.util.List;

public class FileAclSolrUpdateHelper
{
    private EcmFileDao ecmFileDao;
    private SearchAccessControlFields searchAccessControlFields;
    private DataAccessControlConfig dacConfig;

    public JSONArray buildFileAclUpdates(Long containerId, AcmAssignedObject assignedObject)
    {
        JSONArray updates = new JSONArray();
        if (!dacConfig.getEnableDocumentACL())
        {
            List<EcmFile> filesPerContainer = ecmFileDao.findForContainer(containerId, FlushModeType.COMMIT);
            filesPerContainer.forEach(it -> {
                JSONObject doc = searchAccessControlFields.buildParentAccessControlFieldsUpdate(assignedObject,
                        String.format("%s-%s", it.getId(), it.getObjectType()));
                updates.put(doc);
            });
        }

        return updates;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public DataAccessControlConfig getDacConfig()
    {
        return dacConfig;
    }

    public void setDacConfig(DataAccessControlConfig dacConfig)
    {
        this.dacConfig = dacConfig;
    }

    public SearchAccessControlFields getSearchAccessControlFields()
    {
        return searchAccessControlFields;
    }

    public void setSearchAccessControlFields(SearchAccessControlFields searchAccessControlFields)
    {
        this.searchAccessControlFields = searchAccessControlFields;
    }
}
