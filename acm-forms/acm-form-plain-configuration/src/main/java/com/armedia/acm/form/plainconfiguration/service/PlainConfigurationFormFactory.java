/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.service;

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

import com.armedia.acm.form.plainconfiguration.model.PlainConfigurationForm;
import com.armedia.acm.form.plainconfiguration.model.xml.UrlParameterItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoService;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.frevvo.forms.client.ApplicationEntry;
import com.frevvo.forms.client.FormTypeEntry;
import com.frevvo.forms.client.SchemaEntry;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author riste.tutureski
 *
 */
public class PlainConfigurationFormFactory
{

    private Logger LOG = LogManager.getLogger(getClass());
    private FrevvoService frevvoService;
    private Properties formProperties;
    private Properties plainFormProperties;
    private ObjectConverter objectConverter;

    public PlainConfigurationForm convertFromFormTypeEntry(FormTypeEntry formEntry, SchemaEntry schemaEntry,
            ApplicationEntry applicationEntry)
    {
        PlainConfigurationForm form = new PlainConfigurationForm();

        try
        {
            if (formEntry != null && schemaEntry != null)
            {
                form.setKey(getFrevvoService().getFormKey(schemaEntry));
                form.setFormId(formEntry.getId());
                form.setName(formEntry.getTitle().getPlainText());
                form.setType(getFrevvoService().getFormType(formEntry));
                form.setApplicationId(getFrevvoService().getFormApplicationId(formEntry));
                form.setApplicationName(applicationEntry.getTitle().getPlainText());
                form.setUrl(getFrevvoService().getFormUrl().getNewFormUrl(form.getKey(), true));
            }
        }
        catch (Exception e)
        {
            LOG.error("Cannot convert Frevvo Form Entry to Plain Configuration form.", e);
        }

        return form;
    }

    public List<PlainConfigurationForm> convertFromProperties(List<String> targets)
    {
        List<PlainConfigurationForm> plainForms = new ArrayList<PlainConfigurationForm>();

        if (getFormProperties() != null && getPlainFormProperties() != null)
        {
            if (targets == null)
            {
                targets = getTargets();
            }

            if (targets != null && targets.size() > 0)
            {
                for (Entry<Object, Object> entry : getPlainFormProperties().entrySet())
                {
                    String key = (String) entry.getKey();

                    if (key.endsWith(".id") && !key.endsWith("application.id"))
                    {
                        String formKey = getFormKey(key);

                        plainForms.addAll(getFormsForKeyAndTargets(formKey, targets));
                    }
                }
            }
        }

        return plainForms;
    }

    public String[] getKeyValueTargets()
    {
        String[] retval = null;
        String keyValuePairsTargets = getFormProperties().getProperty(FrevvoFormName.PLAIN_CONFIGURATION + ".targets", null);

        if (keyValuePairsTargets != null && !keyValuePairsTargets.isEmpty())
        {
            retval = keyValuePairsTargets.split(",");
        }

        return retval;
    }

    public List<String> getTargets()
    {
        List<String> targets = new ArrayList<String>();

        String[] keyValuePairsTargetsArray = getKeyValueTargets();

        try
        {
            if (keyValuePairsTargetsArray != null && keyValuePairsTargetsArray.length > 0)
            {
                for (int i = 0; i < keyValuePairsTargetsArray.length; i++)
                {
                    String target = getFormTarget(keyValuePairsTargetsArray[i]);

                    if (target != null)
                    {
                        targets.add(target);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.error("Cannot create list of targets.", e);
        }

        return targets;
    }

    private String getFormKey(String propertyKey)
    {
        if (propertyKey != null && !propertyKey.isEmpty())
        {
            String[] propertyKeyParts = propertyKey.split("\\.");

            if (propertyKeyParts != null && propertyKeyParts.length > 0)
            {
                return propertyKeyParts[0];
            }
        }

        return null;
    }

    private String getFormTarget(String target)
    {
        if (target != null && !target.isEmpty())
        {
            String[] targetParts = target.split("=");

            if (targetParts != null && targetParts.length > 0)
            {
                return targetParts[0];
            }
        }

        return null;
    }

    public List<PlainConfigurationForm> getFormsForKeyAndTargets(String formKey, List<String> targets)
    {
        List<PlainConfigurationForm> plainForms = new ArrayList<PlainConfigurationForm>();

        if (formKey != null && targets != null && targets.size() > 0)
        {
            for (String target : targets)
            {
                if (getPlainFormProperties().getProperty(formKey + ".parameters." + target) != null)
                {
                    PlainConfigurationForm plainForm = getFormInfoFromProperties(formKey, target);
                    plainForms.add(plainForm);
                }
            }
        }

        return plainForms;
    }

    public PlainConfigurationForm getFormInfoFromProperties(String formKey, String target)
    {
        PlainConfigurationForm form = new PlainConfigurationForm();

        form.setKey(formKey);
        form.setFormId(getPlainFormProperties().getProperty(formKey + ".id", null));
        form.setName(getPlainFormProperties().getProperty(formKey + ".name", null));
        form.setType(getPlainFormProperties().getProperty(formKey + ".type", null));
        form.setApplicationId(getPlainFormProperties().getProperty(formKey + ".application.id", null));
        form.setApplicationName(getPlainFormProperties().getProperty(formKey + ".application.name", null));
        form.setMode(getPlainFormProperties().getProperty(formKey + ".mode", null));
        form.setTarget(target);
        form.setDescription(getPlainFormProperties().getProperty(formKey + ".description." + target, null));
        form.setUrl(getFrevvoService().getFormUrl().getNewFormUrl(formKey, true));

        List<UrlParameterItem> urlParameters = null;

        String jsonParameters = getPlainFormProperties().getProperty(formKey + ".parameters." + target);
        if (jsonParameters != null && !jsonParameters.isEmpty())
        {
            try
            {
                urlParameters = getObjectConverter().getJsonUnmarshaller().unmarshallCollection(jsonParameters, List.class,
                        UrlParameterItem.class);
            }
            catch (Exception e)
            {
                LOG.error("Cannot parse JSON=" + jsonParameters, e);
            }
        }

        form.setUrlParameters(urlParameters);

        return form;
    }

    public FrevvoService getFrevvoService()
    {
        return frevvoService;
    }

    public void setFrevvoService(FrevvoService frevvoService)
    {
        this.frevvoService = frevvoService;
    }

    public Properties getFormProperties()
    {
        return formProperties;
    }

    public void setFormProperties(Properties formProperties)
    {
        this.formProperties = formProperties;
    }

    public Properties getPlainFormProperties()
    {
        return plainFormProperties;
    }

    public void setPlainFormProperties(Properties plainFormProperties)
    {
        this.plainFormProperties = plainFormProperties;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

}
