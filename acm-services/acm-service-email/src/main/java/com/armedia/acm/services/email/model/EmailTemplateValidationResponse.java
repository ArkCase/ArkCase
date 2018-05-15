package com.armedia.acm.services.email.model;

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

/**
 * @author sasko.tanaskoski
 *
 */
public class EmailTemplateValidationResponse
{

    private boolean validTemplate = true;

    private String objectType;

    private String action;

    private String emailPattern;

    /**
     * @return the validTemplate
     */
    public boolean isValidTemplate()
    {
        return validTemplate;
    }

    /**
     * @param validTemplate
     *            the validTemplate to set
     */
    public void setValidTemplate(boolean validTemplate)
    {
        this.validTemplate = validTemplate;
    }

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(String action)
    {
        this.action = action;
    }

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

}
