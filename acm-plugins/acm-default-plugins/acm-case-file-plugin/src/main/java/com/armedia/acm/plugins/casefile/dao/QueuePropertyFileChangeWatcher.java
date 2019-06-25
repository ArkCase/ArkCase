package com.armedia.acm.plugins.casefile.dao;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.plugins.casefile.model.AcmQueue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by dmiller on 7/21/16.
 */
public class QueuePropertyFileChangeWatcher implements ApplicationListener<AbstractConfigurationFileEvent>
{
    // What should be the value of the user?
    private static final String QUEUE_CREATOR = "SYSTEM_USER";
    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmQueueDao acmQueueDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private PlatformTransactionManager txManager;

    private Properties queueNamesProperties;

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        if (isPropertyFileChange(abstractConfigurationFileEvent))
        {
            loadQueues(abstractConfigurationFileEvent.getConfigFile());
        }
    }

    private void loadQueues(File configFile)
    {

        try (FileInputStream fis = new FileInputStream(configFile))
        {
            log.debug("Loading queues from file {}", configFile.getCanonicalPath());

            Properties queueProperties = new Properties();
            queueProperties.load(fis);

            setQueueNamesProperties(queueProperties);

            loadQueues(queueProperties);
        }
        catch (IOException e)
        {
            log.error("Could not load queue names: {}", e.getMessage(), e);
        }
    }

    public void loadQueues(Properties queueProperties)
    {
        getAuditPropertyEntityAdapter().setUserId(QUEUE_CREATOR);

        List<AcmQueue> queues = getQueueNamesFromPropertiesFile(queueProperties);

        TransactionTemplate tmpl = new TransactionTemplate(txManager);

        tmpl.execute(new TransactionCallbackWithoutResult()
        {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                List<AcmQueue> storedQueues = getAcmQueueDao().findAll();

                // move display order values, so we don't get duplicates when setting the new values
                if (!storedQueues.isEmpty())
                {
                    int largestDisplayOrder = storedQueues.stream().max(Comparator.comparingInt(AcmQueue::getDisplayOrder)).get()
                            .getDisplayOrder();
                    for (int i = 0; i < storedQueues.size(); i++)
                    {
                        AcmQueue queue = storedQueues.get(i);
                        // hopefully won't have negative numbers
                        queue.setDisplayOrder(largestDisplayOrder + i + 1);
                        getAcmQueueDao().save(queue);
                    }
                    getAcmQueueDao().getEm().flush();
                }

                Map<String, AcmQueue> nameQueue = storedQueues.stream().collect(Collectors.toMap(AcmQueue::getName, Function.identity()));

                // set new display order values
                queues.stream().forEach(q -> {

                    if (nameQueue.containsKey(q.getName()))
                    {
                        AcmQueue queue = nameQueue.get(q.getName());
                        if (!queue.getDisplayOrder().equals(q.getDisplayOrder()))
                        {
                            queue.setDisplayOrder(q.getDisplayOrder());
                            getAcmQueueDao().save(queue);
                        }
                    }
                    else
                    {
                        getAcmQueueDao().save(q);
                    }

                });

            }
        });

    }

    protected List<AcmQueue> getQueueNamesFromPropertiesFile(Properties p)
    {
        TreeMap<String, String> orderedProperties = new TreeMap<>((key1, key2) -> {
            String order1 = key1.substring(key1.lastIndexOf('_') + 1);
            String order2 = key2.substring(key2.lastIndexOf('_') + 1);
            return order1.compareTo(order2);
        });

        p.entrySet().stream().forEach(e -> orderedProperties.put((String) e.getKey(), (String) e.getValue()));

        return orderedProperties.entrySet().stream().map(e -> {
            AcmQueue queue = new AcmQueue();
            String key = e.getKey();
            queue.setName(e.getValue());
            queue.setDisplayOrder(Integer.parseInt(key.substring(key.lastIndexOf('_') + 1)));
            return queue;
        }).collect(Collectors.toList());
    }

    private boolean isPropertyFileChange(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        return (abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent
                || abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent)
                && abstractConfigurationFileEvent.getConfigFile().getName().equals("queueNames.properties");
    }

    public AcmQueueDao getAcmQueueDao()
    {
        return acmQueueDao;
    }

    public void setAcmQueueDao(AcmQueueDao acmQueueDao)
    {
        this.acmQueueDao = acmQueueDao;
    }

    public PlatformTransactionManager getTxManager()
    {
        return txManager;
    }

    public void setTxManager(PlatformTransactionManager txManager)
    {
        this.txManager = txManager;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public Properties getQueueNamesProperties()
    {
        return queueNamesProperties;
    }

    public void setQueueNamesProperties(Properties queueNamesProperties)
    {
        this.queueNamesProperties = queueNamesProperties;
    }
}
