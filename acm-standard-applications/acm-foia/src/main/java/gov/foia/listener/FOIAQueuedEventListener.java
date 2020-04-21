/**
 *
 */
package gov.foia.listener;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.QueuedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.time.LocalDateTime;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * @author mario.gjurcheski
 *
 */
public class FOIAQueuedEventListener implements ApplicationListener<QueuedEvent>
{
    private Logger log = LogManager.getLogger(getClass());

    private FOIARequestDao requestDao;

    @Override
    public void onApplicationEvent(QueuedEvent event)
    {
        CaseFile cf = (CaseFile) event.getSource();
        if ((cf.getQueue().getName().equals("Release")))
        {
            try
            {
                FOIARequest request = requestDao.find(cf.getId());
                request.setReleasedDate(LocalDateTime.now());
                log.info("Request has been released for object with objectId = [{}]", cf.getId());
            }
            catch (Exception e)
            {
                log.warn("Request has not been released for object with objectId = [{}]", cf.getId(), e);
            }
        }
    }

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }
}
