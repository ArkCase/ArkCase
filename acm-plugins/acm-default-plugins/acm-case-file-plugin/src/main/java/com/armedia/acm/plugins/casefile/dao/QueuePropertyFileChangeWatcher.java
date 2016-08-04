package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.plugins.casefile.model.AcmQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private AcmQueueDao acmQueueDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    // What should be the value of the user?
    private static final String QUEUE_CREATOR = "SYSTEM_USER";

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
        } catch (IOException e)
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

                Map<String, AcmQueue> nameQueue = storedQueues.stream().collect(Collectors.toMap(AcmQueue::getName, Function.identity()));

                queues.stream().forEach(q ->
                {

                    if (nameQueue.containsKey(q.getName()))
                    {
                        AcmQueue queue = nameQueue.get(q.getName());
                        if (!queue.getDisplayOrder().equals(q.getDisplayOrder()))
                        {
                            queue.setDisplayOrder(q.getDisplayOrder());
                            getAcmQueueDao().save(queue);
                        }
                    } else
                    {
                        getAcmQueueDao().save(q);
                    }

                });

            }
        });

    }

    protected List<AcmQueue> getQueueNamesFromPropertiesFile(Properties p)
    {
        TreeMap<String, String> orderedProperties = new TreeMap<>((key1, key2) ->
        {
            String order1 = key1.substring(key1.lastIndexOf('_') + 1);
            String order2 = key2.substring(key2.lastIndexOf('_') + 1);
            return order1.compareTo(order2);
        });

        p.entrySet().stream().forEach(e -> orderedProperties.put((String) e.getKey(), (String) e.getValue()));

        return orderedProperties.entrySet().stream().map(e ->
        {
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
