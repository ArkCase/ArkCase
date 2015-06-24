package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.CustomCssException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by admin on 6/11/15.
 */
public class CustomCssService {
    private Logger log = LoggerFactory.getLogger(getClass());

    private String customCssFile;


    public String getFile() {
        String fileContent = "";
        try {
            File cssFile = new File(customCssFile);
            if (cssFile.exists()) {
                fileContent = FileUtils.readFileToString(cssFile);
            }
        } catch(Exception e) {
            if (log.isErrorEnabled()){
                log.error(String.format("Can't get custom CSS file %s", customCssFile), e);
            }
        }
        return fileContent;
    }

    public void updateFile(String cssText) throws CustomCssException {
        try {
            File cssFile = new File(customCssFile);
            FileUtils.writeStringToFile(cssFile, cssText);

        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error(String.format("Can't update custom CSS file %s", customCssFile), e);
            }

            throw new CustomCssException(String.format("Can't update custom CSS file %s", customCssFile), e);
        }
    }

    public void setCustomCssFile(String customCssFile) {
        this.customCssFile = customCssFile;
    }
}
