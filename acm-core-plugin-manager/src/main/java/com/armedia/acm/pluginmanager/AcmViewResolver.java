package com.armedia.acm.pluginmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Created by dmiller on 3/12/14.
 */
public class AcmViewResolver extends UrlBasedViewResolver
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception
    {
        AbstractUrlBasedView view = super.buildView(viewName);
        log.debug("Super view URL: " + view.getUrl());

        view = (AbstractUrlBasedView) BeanUtils.instantiateClass(getViewClass());
        Resource r = new ClassPathResource("/views/" + viewName + getSuffix());

        if ( log.isDebugEnabled() )
        {
            log.debug("View resource exists: " + r.exists());
            if ( r.exists() )
            {
                log.debug("View URL: " + r.getURL());
            }
        }
        view.setUrl(r.getURL().toString());
        String contentType = getContentType();
        if ( contentType != null )
        {
            view.setContentType(contentType);
        }
        view.setRequestContextAttribute(getRequestContextAttribute());
        view.setAttributesMap(getAttributesMap());
//        if ( this.exposePathVariables != null )
//        {
//            view.setExposePathVariables(exposePathVariables);
//        }
        view.setExposePathVariables(true);
        return view;
    }
}
