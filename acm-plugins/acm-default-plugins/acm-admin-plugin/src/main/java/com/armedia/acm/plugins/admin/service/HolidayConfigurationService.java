package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.model.HolidayConfiguration;
import com.armedia.acm.plugins.admin.model.HolidayItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HolidayConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private Resource holidayFile;
    private ObjectConverter objectConverter;

    private FileWriter fileWriter = null;
    private FileReader fileReader = null;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void saveHolidayConfig(HolidayConfiguration holidayConf)
    {
        String holidayConfigJson = Objects.nonNull(holidayConf)
                ? getObjectConverter().getIndentedJsonMarshaller().marshal(holidayConf.getHolidays())
                : "[]";

        try
        {
            log.info("Trying to write to config file: " + getHolidayFile().getFile().getAbsolutePath());
            lock.writeLock().lock();
            fileWriter = new FileWriter(getHolidayFile().getFile(), false);
            fileWriter.write(holidayConfigJson);
            fileWriter.close();
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
        String currentLine;
        String holidayConfigJson = "";
        HolidayConfiguration holidayConfiguration = new HolidayConfiguration();
        List<HolidayItem> holidayItemList;

        try
        {
            log.info("Trying to read from config file: " + getHolidayFile().getFile().getAbsolutePath());

            lock.readLock().lock();
            fileReader = new FileReader(getHolidayFile().getFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((currentLine = bufferedReader.readLine()) != null)
            {
                holidayConfigJson += currentLine;
            }

            fileReader.close();
            holidayItemList = getObjectConverter().getJsonUnmarshaller().unmarshallCollection(holidayConfigJson, List.class,
                    HolidayItem.class);
            holidayConfiguration.setHolidays(holidayItemList);
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
