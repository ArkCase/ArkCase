/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.model;

/*-
 * #%L
 * ACM Forms: Plain Configuration
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

import com.armedia.acm.form.plainconfiguration.model.xml.UrlParameterItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name = FrevvoFormConstants.ELEMENT_KEY_PREFIX
        + FrevvoFormName.PLAIN_CONFIGURATION, namespace = FrevvoFormNamespace.PLAIN_CONFIGURATION_NAMESPACE)
public class PlainConfigurationForm
{

    private String key;
    private String formId;
    private List<String> formOptions;
    private String name;
    private String type;
    private String applicationId;
    private String applicationName;
    private String mode;
    private String target;
    private List<String> targetOptions;
    private String description;
    private List<UrlParameterItem> urlParameters;
    private List<String> requiredUrlParemeters;
    private List<String> objectPropertiesOptions;
    private String url;

    @XmlElement(name = "key")
    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    @XmlElement(name = "formId")
    public String getFormId()
    {
        return formId;
    }

    public void setFormId(String formId)
    {
        this.formId = formId;
    }

    @XmlTransient
    public List<String> getFormOptions()
    {
        return formOptions;
    }

    public void setFormOptions(List<String> formOptions)
    {
        this.formOptions = formOptions;
    }

    @XmlElement(name = "name")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElement(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlElement(name = "applicationId")
    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId(String applicationId)
    {
        this.applicationId = applicationId;
    }

    @XmlElement(name = "applicationName")
    public String getApplicationName()
    {
        return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    @XmlElement(name = "formMode")
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XmlElement(name = "target")
    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    @XmlTransient
    public List<String> getTargetOptions()
    {
        return targetOptions;
    }

    public void setTargetOptions(List<String> targetOptions)
    {
        this.targetOptions = targetOptions;
    }

    @XmlElement(name = "description")
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @XmlElement(name = "urlParametersItem")
    public List<UrlParameterItem> getUrlParameters()
    {
        return urlParameters;
    }

    public void setUrlParameters(List<UrlParameterItem> urlParameters)
    {
        this.urlParameters = urlParameters;
    }

    @XmlTransient
    public List<String> getRequiredUrlParemeters()
    {
        return requiredUrlParemeters;
    }

    public void setRequiredUrlParemeters(List<String> requiredUrlParemeters)
    {
        this.requiredUrlParemeters = requiredUrlParemeters;
    }

    @XmlTransient
    public List<String> getObjectPropertiesOptions()
    {
        return objectPropertiesOptions;
    }

    public void setObjectPropertiesOptions(List<String> objectPropertiesOptions)
    {
        this.objectPropertiesOptions = objectPropertiesOptions;
    }

    @XmlTransient
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
