package com.armedia.acm.report.config;

/*-
 * #%L
 * Tool Integrations: report Configuration
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

import java.util.Map;

public interface ReportUrl
{
    /**
     * Retrieve the form server url based on the report name.
     * 
     * @param formName
     * @return
     */
    public String getNewReportUrl(String reportName);

    /**
     * This method get all the report urls in the properties file and
     * return a sorted map.
     * 
     * @return
     */
    public Map<String, String> getNewReportUrlList();

}
