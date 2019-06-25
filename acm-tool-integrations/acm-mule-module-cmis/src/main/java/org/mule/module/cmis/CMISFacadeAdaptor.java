/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.cmis;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.mule.module.cmis.exception.CMISConnectorConnectionException;
import org.mule.module.cmis.exception.CMISConnectorException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CMISFacadeAdaptor
{

    private static Logger LOGGER = LogManager.getLogger(CMISFacadeAdaptor.class);

    public static CMISFacade adapt(CMISFacade facade)
    {
        return (CMISFacade) Proxy.newProxyInstance(CMISFacadeAdaptor.class.getClassLoader(),
                new Class[] { CMISFacade.class }, new MyInvocationHandler(facade));
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
