/**
 * 
 */
package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.form.plainconfiguration.model.PlainConfigurationForm;
import com.armedia.acm.form.plainconfiguration.service.PlainConfigurationFormFactory;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class DeletePlainFormAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());

    private PlainConfigurationFormFactory plainConfigurationFormFactory;
    private PropertyFileManager propertyFileManager;
    private String plainFormPropertiesLocation;

    @RequestMapping(value = "/plainforms/{key}/{target}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PlainConfigurationForm deletePlainForm(@PathVariable("key") String key, @PathVariable("target") String target,
            Authentication auth,
            HttpSession httpSession) throws Exception
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Remove plain form for key=" + key + " and target=" + target);
        }

        PlainConfigurationForm form = new PlainConfigurationForm();

        if (key != null && target != null)
        {
            String parameterKey = key + ".parameters." + target;
            String descriptionKey = key + ".description." + target;
            if (getPlainConfigurationFormFactory().getPlainFormProperties().getProperty(parameterKey) != null)
            {
                form = getPlainConfigurationFormFactory().getFormInfoFromProperties(key, target);

                if (form.getType() != null && !form.getType().isEmpty())
                {
                    LOG.debug("Removing form type = " + form.getType());
                    getPropertyFileManager().removeMultiple(Arrays.asList(parameterKey, descriptionKey), getPlainFormPropertiesLocation());
                }
            }

            // Check if there are are left plain forms for other targets. If no, remove general form information for all
            // targets
            List<String> targets = getPlainConfigurationFormFactory().getTargets();
            List<PlainConfigurationForm> forms = getPlainConfigurationFormFactory().getFormsForKeyAndTargets(key, targets);

            if (forms == null || forms.isEmpty())
            {
                LOG.debug("Removing generic plain form information for all targets and key=" + key);
                getPropertyFileManager().removeMultiple(getKeys(key), getPlainFormPropertiesLocation());
            }
        }

        return form;
    }

    private List<String> getKeys(String formKey)
    {
        List<String> keys = new ArrayList<>();

        keys.add(formKey + ".id");
        keys.add(formKey + ".name");
        keys.add(formKey + ".type");
        keys.add(formKey + ".application.id");
        keys.add(formKey + ".application.name");
        keys.add(formKey + ".mode");

        return keys;
    }

    public PlainConfigurationFormFactory getPlainConfigurationFormFactory()
    {
        return plainConfigurationFormFactory;
    }

    public void setPlainConfigurationFormFactory(
            PlainConfigurationFormFactory plainConfigurationFormFactory)
    {
        this.plainConfigurationFormFactory = plainConfigurationFormFactory;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getPlainFormPropertiesLocation()
    {
        return plainFormPropertiesLocation;
    }

    public void setPlainFormPropertiesLocation(String plainFormPropertiesLocation)
    {
        this.plainFormPropertiesLocation = plainFormPropertiesLocation;
    }

}
