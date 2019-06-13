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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class GetPlainFormsAPIController
{

    private Logger LOG = LogManager.getLogger(getClass());

    private PlainConfigurationFormFactory plainConfigurationFormFactory;

    @RequestMapping(value = "/plainforms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PlainConfigurationForm> getAllPlainForms(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth,
            HttpSession httpSession) throws Exception
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Taking all plain forms.");
        }

        List<PlainConfigurationForm> plainForms = getPlainConfigurationFormFactory().convertFromProperties(null);

        if (plainForms != null)
        {
            try
            {
                // TODO: Finish paging and sorting ... add Comparator to the stream ...
                // For now avoid paging. UI should first support paging. After that, remove this line below
                maxRows = plainForms.size();
                return plainForms.stream()
                        .skip(startRow)
                        .limit(maxRows)
                        .collect(Collectors.toList());
            }
            catch (Exception e)
            {
                LOG.error("Cannot return requested page: start=" + startRow + ", n=" + maxRows, e);
            }
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/plainforms/{target}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PlainConfigurationForm> getPlainFormsForTarget(@PathVariable("target") String target,
            Authentication auth,
            HttpSession httpSession) throws Exception
    {
        if (LOG.isInfoEnabled())
        {
            LOG.info("Taking all plain forms for target=" + target);
        }

        List<PlainConfigurationForm> plainForms = getPlainConfigurationFormFactory().convertFromProperties(Arrays.asList(target));

        return plainForms;
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

}
