package com.armedia.acm.objectonverter;

/*-
 * #%L
 * Tool Integrations: Object Converter
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

public interface DateFormats
{
    /**
     * These date formats for Frevvo are general formats that Frevvo recognize. If we are sending the formats like these
     * to Frevvo, we can user locale for changing the format.
     */
    public final String FREVVO_DATE_FORMAT = "yyyy-M-dd";
    public final String FREVVO_DATE_FORMAT_MARSHAL_UNMARSHAL = "yyyy-M-dd";

    public final String TASK_NAME_DATE_FORMAT = "yyyyMMdd";
    public final String TIMESHEET_DATE_FORMAT = "M/dd/yyyy";
    public final String WORKFLOW_DATE_FORMAT = "M/dd/yyyy";
    public final String CORRESPONDENCE_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * The date format SOLR expects. Any other date format causes SOLR to throw an exception.
     */
    public final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
}
