package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.model.HolidayConfiguration;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HolidayConfigurationService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Resource holidayFile;
    private ObjectConverter objectConverter;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void saveHolidayConfig(HolidayConfiguration holidayConfiguration)
    {
        String holidayConfigJson = Objects.nonNull(holidayConfiguration)
                ? getObjectConverter().getIndentedJsonMarshaller().marshal(holidayConfiguration)
                : "{}";

        try
        {
            log.info("Trying to write to config file: {}", getHolidayFile().getFile().getAbsolutePath());
            lock.writeLock().lock();
            FileUtils.writeStringToFile(getHolidayFile().getFile(), holidayConfigJson);
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

    public HolidayConfiguration getHolidayConfiguration()
    {
        HolidayConfiguration holidayConfiguration = new HolidayConfiguration();

        try
        {
            log.info("Trying to read from config file: {}", getHolidayFile().getFile().getAbsolutePath());

            lock.readLock().lock();
            String holidayConfigJson = FileUtils.readFileToString(getHolidayFile().getFile());
            holidayConfiguration = getObjectConverter().getJsonUnmarshaller().unmarshall(holidayConfigJson, HolidayConfiguration.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }

        return holidayConfiguration;
    }

    public Resource getHolidayFile()
    {
        return holidayFile;
    }

    public void setHolidayFile(Resource holidayFile)
    {
        this.holidayFile = holidayFile;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

}
