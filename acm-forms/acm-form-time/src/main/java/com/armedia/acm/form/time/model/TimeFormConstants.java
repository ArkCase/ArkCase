/**
 * 
 */
package com.armedia.acm.form.time.model;

/*-
 * #%L
 * ACM Forms: Time
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

import com.armedia.acm.frevvo.config.FrevvoFormName;

/**
 * @author riste.tutureski
 *
 */
public interface TimeFormConstants
{

    /**
     * Submission name Save - we need to recognize when to save as draft
     */
    public final static String SAVE = "Save";

    /**
     * Submission name Submit - we need to recognize when to send for approval
     */
    public final static String SUBMIT = "Submit";

    /**
     * Days of week
     */
    public final static String SUNDAY = "SUNDAY";
    public final static String MONDAY = "MONDAY";
    public final static String TUESDAY = "TUESDAY";
    public final static String WEDNESDAY = "WEDNESDAY";
    public final static String THURSDAY = "THURSDAY";
    public final static String FRIDAY = "FRIDAY";
    public final static String SATURDAY = "SATURDAY";

    /**
     * Time types
     */
    public final static String CASE_FILE = FrevvoFormName.CASE_FILE.toUpperCase();
    public final static String COMPLAINT = FrevvoFormName.COMPLAINT.toUpperCase();
    public final static String OTHER = "OTHER";

    public final static String APPROVER_PRIVILEGE = "acm-timesheet-approve";
}
