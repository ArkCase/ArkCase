package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.model.HolidayItem;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HolidayConfigurationService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Resource holidayFile;
    private ObjectConverter objectConverter;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void saveHolidayConfig(List<HolidayItem> holidays)
    {
        String holidayConfigJson = Objects.nonNull(holidays)
                ? getObjectConverter().getIndentedJsonMarshaller().marshal(holidays)
                : "[]";

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

    public List<HolidayItem> getHolidayConfiguration()
    {
        List<HolidayItem> holidayItemList = new ArrayList<>();

        try
        {
            log.info("Trying to read from config file: {}", getHolidayFile().getFile().getAbsolutePath());

            lock.readLock().lock();
            String holidayConfigJson = FileUtils.readFileToString(getHolidayFile().getFile());
            holidayItemList = getObjectConverter().getJsonUnmarshaller().unmarshallCollection(holidayConfigJson, List.class,
                    HolidayItem.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }

        return holidayItemList;
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
