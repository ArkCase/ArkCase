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

import com.armedia.acm.plugins.ecm.model.ProgressbarDetails;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

public class ProgressIndicatorService
{
    private Logger LOG = LogManager.getLogger(getClass());

    private Map<String, ProgressbarExecutor> progressBars = new HashMap<>();
    @Autowired
    private BeanFactory beanFactory;

    public void start(CountingInputStream inputStream, long size, Long id, String type, String name, String username,
            ProgressbarDetails progressbarDetails)
    {
        LOG.debug("Setup progressbar executor for file {}", name);
        ProgressbarExecutor executor = beanFactory.getBean(ProgressbarExecutor.class, progressbarDetails.getUuid(), username);

        LOG.debug("Setup all progressbar details needed, which later will be displayed on UI");
        executor.setProgressbarDetails(progressbarDetails);

        LOG.debug("Start the progressbar executor for file {}", name);
        executor.startProgress(inputStream, size, type, id, name);
        progressBars.put(executor.getID(), executor);
    }

    public void end(String uuid, boolean successful)
    {
        LOG.debug("Stop ProgressbarExecutor for the progressbar {}", progressBars.get(uuid));
        ProgressbarExecutor executor = progressBars.get(uuid);
        if (executor != null)
        {
            executor.stopProgress(successful);
            progressBars.remove(uuid);
        }
    }

    public ProgressbarExecutor getExecutor(String uuid)
    {
        return progressBars.get(uuid);
    }
}
