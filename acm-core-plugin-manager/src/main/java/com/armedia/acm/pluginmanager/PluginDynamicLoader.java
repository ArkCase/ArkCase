package com.armedia.acm.pluginmanager;

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by dmiller on 3/12/14.
 */
public class PluginDynamicLoader implements ApplicationListener<AbstractConfigurationFileEvent>,
        ApplicationContextAware, ServletContextAware
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationContext applicationContext;
    private SpringContextHolder springContextHolder;
    private ServletContext servletContext;

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent fileEvent)
    {
        if ( fileEvent instanceof ConfigurationFileAddedEvent && fileEvent.getConfigFile().getName().endsWith(".jar"))
        {
            if ( log.isInfoEnabled() )
            {
                log.info("Detected jar file '" + fileEvent.getConfigFile().getName() + "'");

                try
                {
                    loadPlugin(fileEvent.getConfigFile());
                } catch (IOException e)
                {
                    log.error("could not load plugin: " + e.getMessage(), e);
                }
            }
        }
    }

    private void loadPlugin(File configFile) throws IOException
    {
        JarFile jarFile = null;
        try
        {
            jarFile =  new JarFile(configFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            List<String> springConfigs = new ArrayList<>();
            while ( entries.hasMoreElements() )
            {
                JarEntry jarEntry = entries.nextElement();
                if ( log.isDebugEnabled() )
                {
                    log.debug("Entry name: " + jarEntry.getName());
                }

                if ( jarEntry.getName().startsWith("spring/spring-web" ) ||
                        jarEntry.getName().startsWith("spring/spring-library" ))
                {
                    log.debug("... entry is a spring config.");
                    springConfigs.add("classpath*:" + jarEntry.getName());
                }
            }

            if ( ! springConfigs.isEmpty() )
            {
                String spec = "jar:file://" + configFile.getCanonicalPath() + "!/";
                URL[] urls = {  };
                PluginClassLoader pluginClassLoader = new PluginClassLoader(urls, getClass().getClassLoader());

                pluginClassLoader.addFile(configFile.getCanonicalPath());
                log.debug("loaded the jar into a classloader!");

//                ClassPathXmlApplicationContext applicationContext = loadClassPathXmlApplicationContext(springConfigs, pluginClassLoader);
                XmlWebApplicationContext applicationContext = loadXmlWebApplicationContext(springConfigs, pluginClassLoader);



                getSpringContextHolder().addContext(configFile.getName(), applicationContext);

//                WebApplicationContext wac = AcmWebAppInitializer.getDispatcherServlet().getWebApplicationContext();



//                RequestMappingHandlerMapping acmMapper = wac.getBean("acmRequestMapper", RequestMappingHandlerMapping.class);

                log.debug("searching for controllers...");
                RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
                mapping.setApplicationContext(applicationContext);
                mapping.setServletContext(getServletContext());
                mapping.afterPropertiesSet();

                DispatcherServlet ds = AcmWebAppInitializer.getDispatcherServlet();
                try
                {
                    Field mappings = ds.getClass().getDeclaredField("handlerMappings");
                    mappings.setAccessible(true);
                    int attempts = 0;
                    boolean found = false;
                    while ( attempts < 5 && !found )
                    {
                        attempts++;
                        List<HandlerMapping> dsMappings = (List<HandlerMapping>) mappings.get(ds);
                        if ( dsMappings == null )
                        {
                            log.debug("Waiting for Spring MVC to initialize");
                            Thread.sleep(5000);
                        }
                        else
                        {
                            dsMappings.add(mapping);
                            found = true;
                        }
                    }

                    log.debug("added mappings to dispatcher service!!!");
                }
                catch (NoSuchFieldException | IllegalAccessException | InterruptedException e )
                {
                    log.error("Could not set mappings: " + e.getMessage(), e);
                }
//                acmMapper.setApplicationContext(applicationContext);
//                acmMapper.afterPropertiesSet();
//                acmMapper.getHandlerMethods();

//                Map<String, HandlerMapping> handlerMap = wac.getBeansOfType(HandlerMapping.class);
//                log.info("# of handlers: " + handlerMap.size());
//                for ( Map.Entry<String, HandlerMapping> current : handlerMap.entrySet() )
//                {
//                    log.info("Handler '" + current.getKey() + "' is of type '" + current.getValue().getClass().getName() + "'" );
//                }



//                ApplicationContext applicationContext = loadXmlWebApplicationContext(springConfigs, pluginClassLoader);
                log.debug("loaded the spring beans!");


            }
        }
        finally
        {
            if ( jarFile != null )
            {
                jarFile.close();
            }
        }
    }

    private XmlWebApplicationContext loadXmlWebApplicationContext(List<String> springConfigs, PluginClassLoader pluginClassLoader)
    {
        springConfigs.add("/WEB-INF/spring/appServlet/servlet-context.xml");
        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setParent(this.applicationContext);
        applicationContext.setServletContext(getServletContext());
        applicationContext.setClassLoader(pluginClassLoader);
        applicationContext.setConfigLocations(springConfigs.toArray(new String[springConfigs.size()]));
        applicationContext.refresh();
        return applicationContext;
    }

    private ClassPathXmlApplicationContext loadClassPathXmlApplicationContext(List<String> springConfigs, PluginClassLoader pluginClassLoader)
    {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(this.applicationContext);
//                springConfigs.toArray(new String[springConfigs.size()]));
        applicationContext.setClassLoader(pluginClassLoader);
        springConfigs.add("/WEB-INF/spring/appServlet/servlet-context.xml");
        applicationContext.setConfigLocations(springConfigs.toArray(new String[springConfigs.size()]));

        applicationContext.refresh();
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    @Override
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;

        Enumeration<String> atts = servletContext.getAttributeNames();
        while ( atts.hasMoreElements() )
        {
            String s = atts.nextElement();
            log.info("Servlet context attribute name: " + s);
            Object att = servletContext.getAttribute(s);
            log.info("... attr is of type: " + ( att == null ? "null" : att.getClass().getName()));
        }
    }

    public ServletContext getServletContext()
    {
        return servletContext;
    }
}
