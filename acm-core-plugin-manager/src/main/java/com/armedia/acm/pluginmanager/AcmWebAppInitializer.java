package com.armedia.acm.pluginmanager;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by dmiller on 3/12/14.
 */
public class AcmWebAppInitializer implements WebApplicationInitializer
{
    private static DispatcherServlet dispatcherServlet;

    public static DispatcherServlet getDispatcherServlet()
    {
        return dispatcherServlet;
    }

    public static void setDispatcherServlet(DispatcherServlet dispatcherServlet)
    {
        AcmWebAppInitializer.dispatcherServlet = dispatcherServlet;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocations(new String[] { "/WEB-INF/spring/appServlet/servlet-context.xml", "classpath*:spring/spring-web-*.xml" });

        DispatcherServlet ds = new DispatcherServlet(appContext);
        setDispatcherServlet(ds);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", ds);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/app/*");
    }
}
