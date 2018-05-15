package com.armedia.acm.plugins.task.model;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 11/10/14.
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class TaskOutcome implements Serializable
{
    private static final long serialVersionUID = 9212550688270421016L;

    private String name;
    private String description;
    private List<String> fieldsRequiredWhenOutcomeIsChosen = new ArrayList<>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<String> getFieldsRequiredWhenOutcomeIsChosen()
    {
        return fieldsRequiredWhenOutcomeIsChosen;
    }

    public void setFieldsRequiredWhenOutcomeIsChosen(List<String> fieldsRequiredWhenOutcomeIsChosen)
    {
        this.fieldsRequiredWhenOutcomeIsChosen = fieldsRequiredWhenOutcomeIsChosen;
    }
}
