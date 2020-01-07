package com.armedia.acm.plugins.ecm.state;

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
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalDate;

public class AcmFilesStateProvider implements StateOfModuleProvider
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileVersionDao ecmFileVersionDao;
    private EcmFileDao ecmFileDao;

    @Override
    public String getModuleName()
    {
        return "acm-files";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmFilesState acmFilesState = new AcmFilesState();
        try
        {
            acmFilesState.setNumberOfDocuments(ecmFileDao.getFilesCount(day.atTime(23, 59, 59)));
            acmFilesState.setSizeOfRepository(ecmFileVersionDao.getTotalSizeOfFiles(day.atTime(23, 59, 59)));
        }
        catch (Exception e)
        {
            log.error("Not able to provide files state.", e.getMessage());
            acmFilesState.addProperty("error", "Not able to provide status." + e.getMessage());
        }
        return acmFilesState;
    }

    public void setEcmFileVersionDao(EcmFileVersionDao ecmFileVersionDao)
    {
        this.ecmFileVersionDao = ecmFileVersionDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
