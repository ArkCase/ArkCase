package com.armedia.acm.pluginmanager.model;

/*-
 * #%L
 * ACM Service: Plugin Manager
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpMethod;

public class AcmPluginUrlPrivilegeTest
{
    @Test
    public void matches_exactMatch()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("url");

        boolean match = priv.matches("url", HttpMethod.GET.name());

        assertTrue(match);
    }

    @Test
    public void matches_caseInsensitiveMatch()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("url");

        boolean match = priv.matches("URL", HttpMethod.GET.name());

        assertTrue(match);
    }

    @Test
    public void matches_methodMismatch()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("url");

        boolean match = priv.matches("url", HttpMethod.DELETE.name());

        assertFalse(match);
    }

    @Test
    public void matches_urlMismatch()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("url");

        boolean match = priv.matches("anotherUrl", HttpMethod.GET.name());

        assertFalse(match);
    }

    @Test
    public void matches_urlAndMethodMismatch()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("url");

        boolean match = priv.matches("anotherUrl", HttpMethod.DELETE.name());

        assertFalse(match);
    }

    @Test
    public void matches_placeholderMatch()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("/url/{placeholder}");

        boolean match = priv.matches("/url/value", HttpMethod.GET.name());

        assertTrue(match);
    }

    @Test
    public void matches_placeholderVerbMismatch()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("/url/{placeholder}");

        boolean match = priv.matches("/url/value", HttpMethod.DELETE.name());

        assertFalse(match);
    }

    @Test
    public void matches_tooManyPlaceholders()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("/url/{placeholder}/{anotherPlaceholder}");

        boolean match = priv.matches("/url/value", HttpMethod.GET.name());

        assertFalse(match);
    }

    @Test
    public void matches_tooManyPathVariables()
    {
        AcmPluginUrlPrivilege priv = new AcmPluginUrlPrivilege();

        priv.setHttpMethod(HttpMethod.GET);
        priv.setUrl("/url/{placeholder}");

        boolean match = priv.matches("/url/value/anotherValue", HttpMethod.GET.name());

        assertFalse(match);
    }
}
