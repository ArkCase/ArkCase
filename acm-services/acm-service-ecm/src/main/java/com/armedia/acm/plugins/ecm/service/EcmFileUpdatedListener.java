package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.exception.EcmFileLinkException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileUpdatedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class EcmFileUpdatedListener implements ApplicationListener<EcmFileUpdatedEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;

    @Override
    public void onApplicationEvent(EcmFileUpdatedEvent event)
    {
        if (event.isSucceeded())
        {

            EcmFile ecmFile = (EcmFile) event.getSource();
            getEcmFileService().checkAndSetDuplicatesByHash(ecmFile);
            try
            {
                getEcmFileService().updateFileLinks(ecmFile);
            }
            catch (AcmObjectNotFoundException e)
            {
                LOG.error("File links not updated: {}", e.getMessage(), e);
            }
            if (ecmFile.isLink())
            {
                try
                {
                    getEcmFileService().updateLinkTargetFile(ecmFile);
                }
                catch (EcmFileLinkException e)
                {
                    LOG.error("Link target file not updated: {}", e.getMessage(), e);
                }
            }
        }
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }
}
