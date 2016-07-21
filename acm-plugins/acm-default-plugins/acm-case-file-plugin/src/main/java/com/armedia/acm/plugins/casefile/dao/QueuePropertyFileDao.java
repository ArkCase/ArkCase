package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class QueuePropertyFileDao extends AcmQueueDao implements InitializingBean
{

    // What should be the value of the user?
    private static final String QUEUE_CREATOR = "SYSTEM_USER";

    //private final Logger log = LoggerFactory.getLogger(getClass());

    private PlatformTransactionManager txManager;

    private Properties queueNamesProperties;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    public Properties getQueueNamesProperties()
    {
        return queueNamesProperties;
    }

    public void setQueueNamesProperties(Properties queueNamesProperties)
    {
        this.queueNamesProperties = queueNamesProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        getAuditPropertyEntityAdapter().setUserId(QUEUE_CREATOR);

        List<AcmQueue> queues = getQueueNamesFromPropertiesFile();

        TransactionTemplate tmpl = new TransactionTemplate(txManager);

        tmpl.execute(new TransactionCallbackWithoutResult()
        {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status)
            {
                List<AcmQueue> storedQueues = findAll();

                List<String> queueNames = storedQueues.stream().map(q -> q.getName()).collect(Collectors.toList());

                queues.stream().filter(q -> !queueNames.contains(q.getName())).forEach(q -> save(q));
            }
        });

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

        return orderedProperties.entrySet().stream().map(e ->
        {
            AcmQueue queue = new AcmQueue();
            String key = e.getKey();
            // queue.setId(Long.parseLong(key.substring(key.lastIndexOf('_') + 1)));
            queue.setName(e.getValue());
            queue.setDisplayOrder(Integer.parseInt(key.substring(key.lastIndexOf('_') + 1)));
            return queue;
        }).collect(Collectors.toList());
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
}
