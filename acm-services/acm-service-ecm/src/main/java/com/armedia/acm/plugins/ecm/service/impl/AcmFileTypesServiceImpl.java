package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import com.armedia.acm.plugins.ecm.service.AcmFileTypesService;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by admin on 6/12/15.
 */
public class AcmFileTypesServiceImpl implements AcmFileTypesService {
    private Logger log = LoggerFactory.getLogger(getClass());
    private PropertyFileManager propertyFileManager;

    private final String PROP_FILE_TYPES = "fileTypes";
    private final String PROP_TYPE = "type";
    private final String PROP_FORM = "form";
    private final String PROP_LABEL = "label";


    private String propertiesLocation;
    private List<String> propertyFiles;
    private String acmFormsPlainPropertiesFile;

    @Override
    public List<String> getFileTypes() throws AcmFileTypesException {
        try {
            List<String> fileTypes = new ArrayList();
            // Load properties file and get fileTypes property
            for (String fileName : propertyFiles) {
                String fileTypesStr = propertyFileManager.load(propertiesLocation + fileName, PROP_FILE_TYPES, "[]");
                JSONArray fileTypesArray = new JSONArray(fileTypesStr);
                for (int  i = 0; i < fileTypesArray.length(); i++) {
                    JSONObject fileTypeObj = fileTypesArray.getJSONObject(i);
                    String fileType = fileTypeObj.getString(PROP_TYPE);
                    if (!fileTypes.contains(fileType)) {
                        fileTypes.add(fileType);
                    }
                }
            }

            // Load information about form types from acm-forms-plain.properties file
            Properties formsProps = new Properties();
            formsProps.load(FileUtils.openInputStream(new File(propertiesLocation + acmFormsPlainPropertiesFile)));

            for (Object propKey: formsProps.keySet()) {
                int dotIndex = ((String)propKey).indexOf('.');
                if (dotIndex  > 0) {
                    String propName = ((String)propKey).substring(0, dotIndex);
                    if (!fileTypes.contains(propName)) {
                        fileTypes.add(propName);
                    }
                }

            }
            return fileTypes;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't get file types from properties files", e);
            }
            throw new AcmFileTypesException("Can't get file types from properties files", e);
        }
    }

    public void setPropertyFiles(List<String> propertyFiles) {
        this.propertyFiles = propertyFiles;
    }

    public void setPropertiesLocation(String propertiesLocation) {
        this.propertiesLocation = propertiesLocation;
    }

    public void setAcmFormsPlainPropertiesFile(String acmFormsPlainPropertiesFile) {
        this.acmFormsPlainPropertiesFile = acmFormsPlainPropertiesFile;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
        this.propertyFileManager = propertyFileManager;
    }
}
