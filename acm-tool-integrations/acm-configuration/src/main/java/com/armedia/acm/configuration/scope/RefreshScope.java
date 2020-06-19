package com.armedia.acm.configuration.scope;

/*-
 * #%L
 * configuration-scope
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class to register new custom scope which will provide all beans defined in this scope as {@link BeanWrapper}
 */
public class RefreshScope implements Scope, BeanFactoryPostProcessor
{
    private static final Logger logger = LogManager.getLogger(RefreshScope.class);
    private volatile static RefreshScope INSTANCE;
    private ConcurrentMap<String, BeanWrapper> cache = new ConcurrentHashMap<>();
    private static final String NAME = "refresh";
    private String id;

    public static RefreshScope getInstance()
    {
        if (INSTANCE == null)
        {
            initialize();
        }
        return INSTANCE;
    }

    private static synchronized void initialize()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new RefreshScope();
        }
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory)
    {
        BeanWrapper value = this.cache.get(name);
        if (value == null)
        {
            value = new BeanWrapper(name, objectFactory);
            this.cache.put(name, value);
        }
        return value.getBean();
    }

    @Override
    public Object remove(String name)
    {
        BeanWrapper value = this.cache.remove(name);
        if (value == null)
        {
            return null;
        }
        return value.getBean();
    }

    @Override
    public void registerDestructionCallback(String name, final Runnable callback)
    {
    }

    @Override
    public Object resolveContextualObject(String key)
    {
        Expression expression = parseExpression(key);
        if (expression == null)
        {
            return null;
        }
        return expression.getValue();
    }

    @Override
    public String getConversationId()
    {
        return NAME;
    }

    private Expression parseExpression(String input)
    {
        if (StringUtils.isEmpty(input))
        {
            return null;
        }
        ExpressionParser parser = new SpelExpressionParser();
        try
        {
            return parser.parseExpression(input);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Cannot parse expression: " + input, e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        beanFactory.registerScope(NAME, this);
        setSerializationId(beanFactory);
    }

    private void setSerializationId(ConfigurableListableBeanFactory beanFactory)
    {
        if (beanFactory instanceof DefaultListableBeanFactory)
        {
            String id = this.id;
            if (id == null)
            {
                String names = Arrays.stream(beanFactory.getBeanDefinitionNames()).sorted().toString();
                logger.debug("Generating bean factory id from names: [{}]", names);
                id = UUID.nameUUIDFromBytes(names.getBytes()).toString();
            }

            logger.info("BeanFactory id=[{}]", id);
            ((DefaultListableBeanFactory) beanFactory).setSerializationId(id);
        }
        else
        {
            logger.warn("BeanFactory was not a DefaultListableBeanFactory, scoped proxy beans cannot be serialized.");
        }

    }

    public void destroy()
    {
        this.cache.clear();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    private static class BeanWrapper
    {
        private Object bean;
        private final String name;
        private ObjectFactory<?> objectFactory;

        BeanWrapper(String name, ObjectFactory<?> objectFactory)
        {
            this.name = name;
            this.objectFactory = objectFactory;
        }

        public Object getBean()
        {
            if (this.bean == null)
            {
                synchronized (this.name)
                {
                    if (this.bean == null)
                    {
                        this.bean = objectFactory.getObject();
                    }
                }
            }
            return this.bean;
        }
    }
}