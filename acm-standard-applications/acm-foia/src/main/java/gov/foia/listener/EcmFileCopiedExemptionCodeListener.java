package gov.foia.listener;

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

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import gov.foia.model.FOIAFile;
import gov.foia.service.FOIAExemptionService;

public class EcmFileCopiedExemptionCodeListener implements ApplicationListener<EcmFileCopiedEvent>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private FOIAExemptionService foiaExemptionService;

    @Override
    public void onApplicationEvent(EcmFileCopiedEvent event)
    {
        if (event != null && event.isSucceeded() && event.getOriginal() != null)
        {
            EcmFile copy = (EcmFile) event.getSource();
            FOIAFile original = (FOIAFile) event.getOriginal();
            if (original != null && original.getExemptionCodes().size() > 0)
            {
                log.info("File with exemption codes has been copied to another folder");
                getFoiaExemptionService().copyFileWithExemptionCodes(original, copy);
            }

        }
    }

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }
}
