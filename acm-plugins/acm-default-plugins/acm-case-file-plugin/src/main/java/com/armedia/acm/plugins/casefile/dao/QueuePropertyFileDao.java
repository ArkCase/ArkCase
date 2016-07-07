package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.plugins.casefile.model.AcmQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class QueuePropertyFileDao extends AcmQueueDao
{

    private ThreadLocal<Date> updateId = new ThreadLocal<Date>()
    {
        @Override
        public Date initialValue()
        {
            return new Date();
        }
    };

    private Properties queueNamesProperties;

    @Override
    public List<AcmQueue> findAllOrderBy(String column)
    {
        return getQueueNamesFromPropertiesFile();
    }

    @Override
    public List<AcmQueue> findModifiedSince(Date lastModified, int startRow, int pageSize)
    {
        if (!updateId.get().equals(lastModified))
        {
            updateId.set(lastModified);
            return getQueueNamesFromPropertiesFile();
        } else
        {
            return new ArrayList<AcmQueue>();
        }
    }

    private List<AcmQueue> getQueueNamesFromPropertiesFile()
    {
        TreeMap<String, String> orderedProperties = new TreeMap<>(new Comparator<String>()
        {
            @Override
            public int compare(String key1, String key2)
            {
                String order1 = key1.substring(key1.lastIndexOf('_') + 1);
                String order2 = key2.substring(key2.lastIndexOf('_') + 1);
                return order1.compareTo(order2);
            }
        });

        queueNamesProperties.entrySet().stream().forEach(e -> orderedProperties.put((String) e.getKey(), (String) e.getValue()));

        return orderedProperties.entrySet().stream().map(e -> {
            AcmQueue queue = new AcmQueue();
            String key = e.getKey();
            queue.setId(Long.parseLong(key.substring(key.lastIndexOf('_') + 1)));
            queue.setName(e.getValue());
            return queue;
        }).collect(Collectors.toList());
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
