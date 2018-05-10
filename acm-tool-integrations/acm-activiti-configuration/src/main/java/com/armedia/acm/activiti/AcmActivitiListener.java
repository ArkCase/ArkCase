package com.armedia.acm.activiti;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

import org.activiti.bpmn.model.ActivitiListener;

/**
 * This class is needed so we can define ActivitiListeners in Spring XML files. Spring keeps trying to
 * resolve what it thinks is a property placeholder (since Activiti uses "${...}" to indicate an Activiti
 * expression).
 *
 * So, just put the expression part (the part between ${ and }) in the Spring config file, and this
 * wrapper class adds the ${ and the }, so Activiti keeps working.
 */
public class AcmActivitiListener extends ActivitiListener
{
    @Override
    public String getImplementation()
    {
        return "${" + super.getImplementation() + "}";
    }
}
