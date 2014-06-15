package com.armedia.acm.pluginmanager.model;


import org.junit.Test;
import org.springframework.http.HttpMethod;

import static org.junit.Assert.*;

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
