package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import com.armedia.acm.plugins.ecm.service.AcmFileTypesService;
import com.armedia.acm.plugins.ecm.service.SupportsFileTypes;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by admin on 6/12/15.
 */
public class AcmFileTypesServiceImpl implements AcmFileTypesService
{
    private Logger log = LogManager.getLogger(getClass());
    private String propertiesLocation;
    private String acmFormsAcmPropertiesFile;
    private String acmFormsPlainPropertiesFile;
    private SpringContextHolder contextHolder;

    @Override
    public Set<String> getFileTypes()
    {
        Map<String, SupportsFileTypes> supportsFileTypesPlugins = contextHolder.getAllBeansOfType(SupportsFileTypes.class);
        return supportsFileTypesPlugins.entrySet().stream()
                .flatMap(it -> it.getValue().getFileTypes().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getForms() throws AcmFileTypesException
    {
        Set<String> forms = new HashSet<>();
        forms.addAll(getFormsFromAcmFormsPropertiesFile());
        forms.addAll(getFormsFromPlainFormsPropertiesFile());
        return forms;
    }

    private Set<String> getFormsFromAcmFormsPropertiesFile() throws AcmFileTypesException
    {
        try (InputStream propInputStream = FileUtils.openInputStream(new File(propertiesLocation + acmFormsAcmPropertiesFile)))
        {
            Set<String> forms = new HashSet<>();
            // Load information about form types from acm-forms.properties file
            Properties formsProps = new Properties();
            formsProps.load(propInputStream);

            for (Object propKey : formsProps.keySet())
            {
                int dotIndex = ((String) propKey).indexOf('.');
                if (dotIndex > 0)
                {
                    String propName = ((String) propKey).substring(0, dotIndex);
                    // Be sure that selected properties are related to forms
                    // Forms properties have name, type and mode properties.
                    if (!forms.contains(propName) && isFormProperty(propName, formsProps))
                    {
                        forms.add(propName);
                    }
                }
            }
            return forms;
        }
        catch (Exception e)
        {
            log.error("Can't get forms info from properties files", e);
            throw new AcmFileTypesException("Can't get forms info from properties files", e);
        }
    }

    private Set<String> getFormsFromPlainFormsPropertiesFile() throws AcmFileTypesException
    {
        try (InputStream propertyInputStream = FileUtils.openInputStream(new File(propertiesLocation + acmFormsPlainPropertiesFile)))
        {
            Set<String> forms = new HashSet<>();
            // Load information about form types from acm-forms-plain.properties file
            Properties formsProps = new Properties();
            formsProps.load(propertyInputStream);

            for (Object propKey : formsProps.keySet())
            {
                int dotIndex = ((String) propKey).indexOf('.');
                if (dotIndex > 0)
                {
                    String propName = ((String) propKey).substring(0, dotIndex);
                    if (!forms.contains(propName))
                    {
                        forms.add(propName);
                    }
                }

            }
            return forms;
        }
        catch (Exception e)
        {
            log.error("Can't get forms info from properties files", e);
            throw new AcmFileTypesException("Can't get forms info from properties files", e);
        }
    }

    private boolean isFormProperty(String propName, Properties props)
    {
        boolean result = props.containsKey(String.format(PROP_FORM_NAME_TPL, propName))
                && props.containsKey(String.format(PROP_FORM_MODE_TPL, propName))
                && props.containsKey(String.format(PROP_FORM_TYPE_TPL, propName));
        return result;
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }

    public void setPropertiesLocation(String propertiesLocation)
    {
        this.propertiesLocation = propertiesLocation;
    }

    public void setAcmFormsAcmPropertiesFile(String acmFormsAcmPropertiesFile)
    {
        this.acmFormsAcmPropertiesFile = acmFormsAcmPropertiesFile;
    }

    public void setAcmFormsPlainPropertiesFile(String acmFormsPlainPropertiesFile)
    {
        this.acmFormsPlainPropertiesFile = acmFormsPlainPropertiesFile;
    }
}
