/**
 *
 */
package gov.foia.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileAddedEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Dec 5, 2016
 */
public class HoldedAndSuspendedRequestsDueDateUpdate implements ApplicationListener<AbstractConfigurationFileEvent>
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private final List<String> STATUSES = Arrays.asList("SUSPENDED", "HOLD");
    private boolean enabled;
    private FOIARequestDao requestDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {
        if (isPropertyFileChange(event))
        {
            File configFile = event.getConfigFile();
            try (FileInputStream fis = new FileInputStream(configFile))
            {
                log.debug("Loading configaration for {} from {} file.", getClass().getName(), configFile.getName());

                Properties billingQueuePurgerProperties = new Properties();
                billingQueuePurgerProperties.load(fis);

                enabled = Boolean.parseBoolean(billingQueuePurgerProperties.getProperty("holdedAndSuspendedRequestsDueDateUpdateEnabled"));

            }
            catch (IOException e)
            {
                log.error("Could not load configuration for {} from {} file.", getClass().getName(), configFile.getName(), e);
            }
        }
    }

    protected boolean isPropertyFileChange(AbstractConfigurationFileEvent abstractConfigurationFileEvent)
    {
        return (abstractConfigurationFileEvent instanceof ConfigurationFileAddedEvent
                || abstractConfigurationFileEvent instanceof ConfigurationFileChangedEvent)
                && abstractConfigurationFileEvent.getConfigFile().getName().equals("foia.properties");
    }

    public void updateDueDate()
    {
        if (!enabled)
        {
            return;
        }
        auditPropertyEntityAdapter.setUserId("DUE_DATE_UPDATER");
        List<FOIARequest> result = requestDao.getAllRequestsByStatus(STATUSES);
        for (FOIARequest request : result)
        {
            Date dueDate = request.getDueDate();
            LocalDateTime dueDateUpdated = dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
            request.setDueDate(Date.from(dueDateUpdated.atZone(ZoneId.systemDefault()).toInstant()));
            requestDao.save(request);
        }
    }

    /**
     * @param requestDao
     *            the requestDao to set
     */
    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }

    /**
     * @param auditPropertyEntityAdapter
     *            the auditPropertyEntityAdapter to set
     */
    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

}
