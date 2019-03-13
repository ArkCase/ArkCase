package com.armedia.acm.form.casefile.model;

/*-
 * #%L
 * ACM Forms: Case File
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

import org.springframework.beans.factory.annotation.Value;

public class CaseFileFormConfig
{
    @Value("${active.case.form}")
    private String activeForm;

    @Value("${case_file.name}")
    private String formName;

    @Value("${case_file.application.id}")
    private String formAppId;

    @Value("${case_file.type}")
    private String formType;

    @Value("${case_file.application.id}")
    private String formMode;

    public String getActiveForm()
    {
        return activeForm;
    }

    public String getFormName()
    {
        return formName;
    }

    public String getFormAppId()
    {
        return formAppId;
    }

    public String getFormType()
    {
        return formType;
    }

    public String getFormMode()
    {
        return formMode;
    }
}
