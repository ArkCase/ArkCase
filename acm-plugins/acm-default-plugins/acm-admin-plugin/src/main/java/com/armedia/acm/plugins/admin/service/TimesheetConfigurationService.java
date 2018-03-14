package com.armedia.acm.plugins.admin.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.model.TimesheetConfig;



public class TimesheetConfigurationService {

    private Logger log = LoggerFactory.getLogger(getClass());
    private Resource timesheetResource;
    private ObjectConverter objectConverter;

    private FileWriter fileWriter = null;
    private FileReader fileReader = null;
    private ReadWriteLock lock = new ReentrantReadWriteLock();


    public TimesheetConfig getConfig()
    {
        String currentLine;
        String timesheetConfigJson = "";
        TimesheetConfig timesheetConfig = null;

        try
        {
            log.info("Trying to read from config file: " + getTimesheetResource().getFile().getAbsolutePath());

            lock.readLock().lock();
            fileReader = new FileReader(getTimesheetResource().getFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((currentLine = bufferedReader.readLine()) != null)
            {
                timesheetConfigJson += currentLine;
            }

            fileReader.close();
            timesheetConfig = getObjectConverter().getJsonUnmarshaller().unmarshall(timesheetConfigJson, TimesheetConfig.class);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            lock.readLock().unlock();
        }

        return timesheetConfig;
    }

    public void saveConfig(TimesheetConfig config)
    {
        String timesheetConfigJson = Objects.nonNull(config) ? getObjectConverter().getJsonMarshaller().marshal(config) : "";

        try
        {
            log.info("Trying to write to config file: " + getTimesheetResource().getFile().getAbsolutePath());
            lock.writeLock().lock();
            fileWriter = new FileWriter(getTimesheetResource().getFile(), false);
            fileWriter.write(timesheetConfigJson);
            fileWriter.close();
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally {
            lock.writeLock().unlock();
        }
    }


    public Resource getTimesheetResource() {
        return timesheetResource;
    }

    public void setTimesheetResource(Resource timesheetResource) {
        this.timesheetResource = timesheetResource;
    }

    public ObjectConverter getObjectConverter() {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter) {
        this.objectConverter = objectConverter;
    }
}
