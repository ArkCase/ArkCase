package com.armedia.acm.ocr.listener;

/*-
 * #%L
 * acm-ocr
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

import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.service.ArkCaseOCRService;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class EcmFileAddedListener implements ApplicationListener<EcmFileAddedEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private ArkCaseOCRService arkCaseOCRService;

    @Override
    public void onApplicationEvent(EcmFileAddedEvent event)
    {
        if (event != null && event.isSucceeded())
        {
            try
            {
                getArkCaseOCRService().create(event);
            }
            catch (CreateOCRException e)
            {
                LOG.warn("Creating OCR for file with ID=[{}] and VERSION_ID=[{}] is not executed. REASON=[{}]",
                        event.getSource().getFileId(), event.getSource().getActiveVersionTag(), e.getMessage());
            }
        }
    }

    public ArkCaseOCRService getArkCaseOCRService()
    {
        return arkCaseOCRService;
    }

    public void setArkCaseOCRService(ArkCaseOCRService arkCaseOCRService)
    {
        this.arkCaseOCRService = arkCaseOCRService;
    }
}
