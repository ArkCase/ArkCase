/*
 * Copyright (c) 2012, Harald Kuhr
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.twelvemonkeys.servlet.image;

/*-
 * #%L
 * ACM Service: Folder Converting Service
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

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Takes care of registering and de-registering local ImageIO plugins (service providers) for the servlet context.
 * <p/>
 * Registers all available plugins on {@code contextInitialized} event, using {@code ImageIO.scanForPlugins()}, to make
 * sure they are available to the current servlet context.
 * De-registers all plugins which have the {@link Thread#getContextClassLoader() current thread's context class loader}
 * as its class loader on {@code contextDestroyed} event, to avoid class/resource leak.
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: IIOProviderContextListener.java,v 1.0 14.02.12 21:53 haraldk Exp$
 * @see javax.imageio.ImageIO#scanForPlugins()
 */
public final class IIOProviderContextListener implements ServletContextListener
{

    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        // Registers all locally available IIO plugins.
        ImageIO.scanForPlugins();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {
        // De-register any locally registered IIO plugins. Relies on each web app having its own context class loader.
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final LocalFilter localFilter = new LocalFilter(Thread.currentThread().getContextClassLoader()); // scanForPlugins
                                                                                                         // uses context
                                                                                                         // class loader

        Iterator<Class<?>> categories = registry.getCategories();

        while (categories.hasNext())
        {
            Class<?> category = categories.next();
            Iterator<?> providers = registry.getServiceProviders(category, localFilter, false);

            // Copy the providers, as de-registering while iterating over providers will lead to
            // ConcurrentModificationExceptions.
            List<Object> providersCopy = new ArrayList<>();
            while (providers.hasNext())
            {
                providersCopy.add(providers.next());
            }

            for (Object provider : providersCopy)
            {
                registry.deregisterServiceProvider(provider);
                event.getServletContext().log(String.format("Unregistered locally installed provider class: %s", provider.getClass()));
            }
        }
    }

    static class LocalFilter implements ServiceRegistry.Filter
    {
        private final ClassLoader loader;

        public LocalFilter(ClassLoader loader)
        {
            this.loader = loader;
        }

        @Override
        public boolean filter(Object provider)
        {
            return provider.getClass().getClassLoader() == loader;
        }
    }
}
