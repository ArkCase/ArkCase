/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis;

import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.mule.module.cmis.exception.CMISConnectorConnectionException;
import org.mule.module.cmis.exception.CMISConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CMISFacadeAdaptor 
{

    private static Logger LOGGER = LoggerFactory.getLogger(CMISFacadeAdaptor.class);

    public static CMISFacade adapt(CMISFacade facade) 
    {
        return (CMISFacade) Proxy.newProxyInstance(CMISFacadeAdaptor.class.getClassLoader(),
               new Class[]{CMISFacade.class}, new MyInvocationHandler(facade));
    }

    private static class MyInvocationHandler implements InvocationHandler 
    {
        private final CMISFacade facade;

        private MyInvocationHandler(CMISFacade facade) 
        {
            this.facade = facade;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
        {
            if (LOGGER.isDebugEnabled()) 
            {
                LOGGER.debug("Invoked method {0} with arguments {1}", method.getName(), args);
            }
            
            try 
            {
                Object ret = method.invoke(facade, args);
                
                if (LOGGER.isDebugEnabled()) 
                {
                    LOGGER.debug("Returned method {0} with value {1}", ret);
                }
                return ret;
            } 
            catch (InvocationTargetException e) 
            {
                if (LOGGER.isWarnEnabled()) 
                {
                    LOGGER.warn("Method " + method.getName() + " thew " + e.getClass(), e);
                }

                Throwable cause = e.getCause();

                if (cause instanceof CmisConnectionException)
                {
                    throw new CMISConnectorConnectionException(e.getCause());
                }
                else if (cause instanceof CMISConnectorConnectionException ||
                        cause instanceof RuntimeException)
                {
                    throw e.getCause();
                }
                else
                {
                    throw new CMISConnectorException(cause);
                }
            }
        }
    }
}