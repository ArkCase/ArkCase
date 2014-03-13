package com.armedia.acm.pluginmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * Created by dmiller on 3/12/14.
 */
public class PluginClassLoader extends URLClassLoader
{
    private Logger log = LoggerFactory.getLogger(getClass());

    public PluginClassLoader(URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
    }

    public PluginClassLoader(URL[] urls)
    {
        super(urls);
    }

    public PluginClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory)
    {
        super(urls, parent, factory);
    }

    public void addFile(String path) throws MalformedURLException
    {
        String urlPath = "jar:file:///" + path + "!/";
        urlPath = urlPath.replace("\\", "/");
        log.debug("adding file " + urlPath);
        addURL(new URL(urlPath));
    }
}
