package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public class EmailTemplateConfiguration
{

    /**
     * {@link #emailPattern} should be a valid java.util.regex.Pattern pattern
     * 
     * @see Pattern
     */
    private String emailPattern;

    private List<String> objectTypes;

    private EmailSource source;

    private String templateName;

    private List<String> actions;

    /**
     * @return the emailPattern
     */
    public String getEmailPattern()
    {
        return emailPattern;
    }

    /**
     * @param emailPattern
     *            the emailPattern to set
     */
    public void setEmailPattern(String emailPattern)
    {
        this.emailPattern = emailPattern;
    }

    /**
     * @return the objectTypes
     */
    public List<String> getObjectTypes()
    {
        return objectTypes;
    }

    /**
     * @param objectTypes
     *            the objectTypes to set
     */
    public void setObjectTypes(List<String> objectTypes)
    {
        this.objectTypes = objectTypes;
    }

    /**
     * @return the source
     */
    public EmailSource getSource()
    {
        return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(EmailSource source)
    {
        this.source = source;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName()
    {
        return templateName;
    }

    /**
     * @param templateName
     *            the templateName to set
     */
    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    /**
     * @return the actions
     */
    public List<String> getActions()
    {
        return actions;
    }

    /**
     * @param actions
     *            the actions to set
     */
    public void setActions(List<String> actions)
    {
        this.actions = actions;
    }

}
