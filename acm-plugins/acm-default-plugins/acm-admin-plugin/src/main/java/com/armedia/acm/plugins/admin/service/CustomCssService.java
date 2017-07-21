package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.plugins.admin.exception.AcmCustomCssException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by admin on 6/11/15.
 */
public class CustomCssService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private String customCssFile;


    public String getFile()
    {
        String fileContent = "";
        try
        {
            File cssFile = new File(customCssFile);
            if (cssFile.exists())
            {
                fileContent = FileUtils.readFileToString(cssFile);
            }
        } catch (Exception e)
        {
            log.error("Can't get custom CSS file [{}]", customCssFile, e);
        }
        return fileContent;
    }

    public void updateFile(String cssText) throws AcmCustomCssException
    {
        try
        {
            File cssFile = new File(customCssFile);
            FileUtils.writeStringToFile(cssFile, cssText);

        } catch (Exception e)
        {
            log.error("Can't update custom CSS file [{}]", customCssFile, e);
            throw new AcmCustomCssException(String.format("Can't update custom CSS file %s", customCssFile), e);
        }
    }

    public void setCustomCssFile(String customCssFile)
    {
        this.customCssFile = customCssFile;
    }
}
