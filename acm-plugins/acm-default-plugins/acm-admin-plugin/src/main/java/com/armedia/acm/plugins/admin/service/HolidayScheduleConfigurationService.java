package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.model.HolidayScheduleConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HolidayScheduleConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());
    private Resource holidayScheduleFile;
    private ObjectConverter objectConverter;

    private FileWriter fileWriter = null;
    private FileReader fileReader = null;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void saveHolidaySchedule(HolidayScheduleConfiguration holidayScheduleConf)
    {
        String holidayScheduleConfigJson = Objects.nonNull(holidayScheduleConf)
                ? getObjectConverter().getJsonMarshaller().marshal(holidayScheduleConf)
                : "{}";

        try
        {
            log.info("Trying to write to config file: " + getHolidayScheduleFile().getFile().getAbsolutePath());
            lock.writeLock().lock();
            fileWriter = new FileWriter(getHolidayScheduleFile().getFile(), false);
            fileWriter.write(holidayScheduleConfigJson);
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

    public HolidayScheduleConfiguration getHolidaySchedule()
    {
        String currentLine;
        String holidayScheduleConfigJson = "";
        HolidayScheduleConfiguration holidayScheduleConfiguration = null;

        try
        {
            log.info("Trying to read from config file: " + getHolidayScheduleFile().getFile().getAbsolutePath());

            lock.readLock().lock();
            fileReader = new FileReader(getHolidayScheduleFile().getFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((currentLine = bufferedReader.readLine()) != null)
            {
                holidayScheduleConfigJson += currentLine;
            }

            fileReader.close();
            holidayScheduleConfiguration = getObjectConverter().getJsonUnmarshaller().unmarshall(holidayScheduleConfigJson,
                    HolidayScheduleConfiguration.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }

        return holidayScheduleConfiguration;

    }

    public Resource getHolidayScheduleFile()
    {
        return holidayScheduleFile;
    }

    public void setHolidayScheduleFile(Resource holidayScheduleFile)
    {
        this.holidayScheduleFile = holidayScheduleFile;
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
