package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import com.armedia.acm.plugins.ecm.service.AcmFileTypesService;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final String PROP_FORM_NAME_TPL = "%s.name";
    private final String PROP_FORM_TYPE_TPL = "%s.type";
    private final String PROP_FORM_MODE_TPL = "%s.mode";




    private String propertiesLocation;
    private List<String> propertyFiles;
    private String acmFormsAcmPropertiesFile;
    private String acmFormsPlainPropertiesFile;

    @Override
    public Set<String> getFileTypes() throws AcmFileTypesException {
        try {
            Set<String> fileTypes = new HashSet();
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
            return fileTypes;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't get file types from properties files", e);
            }
            throw new AcmFileTypesException("Can't get file types from properties files", e);
        }
    }

    @Override
    public Set<String> getForms() throws AcmFileTypesException {
        Set<String> forms = new HashSet();
        forms.addAll(getFormsFromAcmFormsPropertiesFile());
        forms.addAll(getFormsFromPlainFormsPropertiesFile());
        return forms;
    }

    private Set<String> getFormsFromAcmFormsPropertiesFile() throws AcmFileTypesException {
        try {
            Set<String> forms = new HashSet();
            // Load information about form types from acm-forms-plain.properties file
            Properties formsProps = new Properties();
            formsProps.load(FileUtils.openInputStream(new File(propertiesLocation + acmFormsAcmPropertiesFile)));

            for (Object propKey : formsProps.keySet()) {
                int dotIndex = ((String) propKey).indexOf('.');
                if (dotIndex > 0) {
                    String propName = ((String) propKey).substring(0, dotIndex);
                    // Be sure that selected properties are related to forms
                    // Forms properties have name, type and mode properties.
                    if (!forms.contains(propName) && isFormProperty(propName, formsProps)) {
                        forms.add(propName);
                    }
                }
            }
            return forms;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't get forms info from properties files", e);
            }
            throw new AcmFileTypesException("Can't get forms info from properties files", e);
        }
    }

    private Set<String> getFormsFromPlainFormsPropertiesFile() throws AcmFileTypesException {
        try {
            Set<String> forms = new HashSet<>();
            // Load information about form types from acm-forms-plain.properties file
            Properties formsProps = new Properties();
            formsProps.load(FileUtils.openInputStream(new File(propertiesLocation + acmFormsPlainPropertiesFile)));

            for (Object propKey : formsProps.keySet()) {
                int dotIndex = ((String) propKey).indexOf('.');
                if (dotIndex > 0) {
                    String propName = ((String) propKey).substring(0, dotIndex);
                    if (!forms.contains(propName)) {
                        forms.add(propName);
                    }
                }

            }
            return forms;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't get forms info from properties files", e);
            }
            throw new AcmFileTypesException("Can't get forms info from properties files", e);
        }
    }


    private boolean isFormProperty(String propName, Properties props) {
        boolean result =
                props.containsKey(String.format(PROP_FORM_NAME_TPL, propName))
                        && props.containsKey(String.format(PROP_FORM_MODE_TPL, propName))
                        && props.containsKey(String.format(PROP_FORM_TYPE_TPL, propName));
        return result;
    }

    public void setPropertyFiles(List<String> propertyFiles) {
        this.propertyFiles = propertyFiles;
    }

    public void setPropertiesLocation(String propertiesLocation) {
        this.propertiesLocation = propertiesLocation;
    }

    public void setAcmFormsAcmPropertiesFile(String acmFormsAcmPropertiesFile) {
        this.acmFormsAcmPropertiesFile = acmFormsAcmPropertiesFile;
    }

    public void setAcmFormsPlainPropertiesFile(String acmFormsPlainPropertiesFile) {
        this.acmFormsPlainPropertiesFile = acmFormsPlainPropertiesFile;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
        this.propertyFileManager = propertyFileManager;
    }
}
