package gov.foia.service;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.service.HolidayConfigurationService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import gov.foia.model.FOIAConstants;
import gov.foia.model.QueueTimeToComplete;

public class QueuesTimeToCompleteService
{

    private Resource queueConfigFile;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Logger log = LoggerFactory.getLogger(getClass());
    private HolidayConfigurationService holidayConfigurationService;

    private ObjectConverter objectConverter;

    public void saveTimeToComplete(QueueTimeToComplete queueTimeToComplete)
    {
        String queueConfigJson = Objects.nonNull(queueTimeToComplete)
                ? getObjectConverter().getIndentedJsonMarshaller().marshal(queueTimeToComplete)
                : "{}";
        try
        {
            log.info("Trying to write to config file: {}", getQueueConfigFile().getFile().getAbsolutePath());
            lock.writeLock().lock();
            FileUtils.writeStringToFile(getQueueConfigFile().getFile(), queueConfigJson);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.writeLock().unlock();
        }

    }

    public QueueTimeToComplete getTimeToComplete()
    {
        QueueTimeToComplete queueTimeToComplete = new QueueTimeToComplete();

        try
        {
            log.info("Trying to read from config file: {}", getQueueConfigFile().getFile().getAbsolutePath());

            lock.readLock().lock();
            String queueConfigJson = FileUtils.readFileToString(getQueueConfigFile().getFile());
            queueTimeToComplete = getObjectConverter().getJsonUnmarshaller().unmarshall(queueConfigJson, QueueTimeToComplete.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }

        return queueTimeToComplete;

    }

    public Date addWorkingDaysToDate(Date date, String requestType)
    {
        switch (requestType)
        {
        case FOIAConstants.NEW_REQUEST_TYPE:
            return getHolidayConfigurationService().addWorkingDaysToDate(new Date(),
                    getTimeToComplete().getRequest().getTotalTimeToComplete());
        case FOIAConstants.APPEAL_REQUEST_TYPE:
            return getHolidayConfigurationService().addWorkingDaysToDate(new Date(),
                    getTimeToComplete().getAppeal().getTotalTimeToComplete());
        default:
            throw new RuntimeException("Unknown FOIA request type: " + requestType);
        }
    }

    public Resource getQueueConfigFile()
    {
        return queueConfigFile;
    }

    public void setQueueConfigFile(Resource queueConfigFile)
    {
        this.queueConfigFile = queueConfigFile;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }
}
