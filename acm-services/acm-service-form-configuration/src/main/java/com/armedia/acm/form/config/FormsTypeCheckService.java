package com.armedia.acm.form.config;

/*-
 * #%L
 * ACM Service: Form Configuration
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class FormsTypeCheckService
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private FormsTypeManagementService formsTypeManagementService;

    public String getTypeOfForm()
    {
        String formsType = "";
        try
        {
            formsType = formsTypeManagementService.getProperty("formsType").get("formsType").toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
        }
        return formsType;
    }

    public void setFormsTypeManagementService(FormsTypeManagementService formsTypeManagementService)
    {
        this.formsTypeManagementService = formsTypeManagementService;
    }
}
