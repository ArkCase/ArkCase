package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmModuleConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Created by sergey.kolomiets on 6/26/15.
 */
public class ModuleConfigurationService implements ModuleConfigurationConstants {
    private Logger log = LoggerFactory.getLogger(getClass());
    private String appConfigPropertiesFile;



    public List<Map<String, String>> retrieveModules() throws AcmModuleConfigurationException {
        try {
            Properties props = new Properties();
            props.load(FileUtils.openInputStream(new File(appConfigPropertiesFile)));

            // Get only navigator keys
            List<String> modulesIds = new ArrayList();
            for (Object keyIter : props.keySet()) {
                String  key = (String)keyIter;
                if (key.indexOf(PROP_NAVIGATOR) == 0) {
                    // Remove prefix
                    int beginIndex = key.indexOf(".");
                    if (beginIndex == -1) {
                        throw new AcmModuleConfigurationException(String.format("Wrong property name %s", (String)keyIter));
                    }
                    key = key.substring(beginIndex + 1);

                    // Remove suffix
                    int endIndex = key.indexOf(".");
                    if (beginIndex == -1) {
                        throw new AcmModuleConfigurationException(String.format("Wrong property name %s", (String)keyIter));
                    }
                    key = key.substring(0, endIndex);
                    if (!modulesIds.contains(key)) {
                        modulesIds.add(key);
                    }

                    if (!modulesIds.contains(key)) {
                        modulesIds.add(key);
                    }
                }
            }

            List<Map<String, String>> modulesInfos = new ArrayList();
            for (String moduleId : modulesIds) {
                Map<String, String> moduleInfo = new HashMap();
                moduleInfo.put(PROP_MODULE_ID, moduleId);

                String nameProperty = String.format(PROP_MODULE_NAME_TMPL, moduleId);
                moduleInfo.put(PROP_MODULE_NAME, props.getProperty(nameProperty, ""));

                String privilegeProperty = String.format(PROP_MODULE_PRIVILEGE_TMPL, moduleId);
                moduleInfo.put(PROP_MODULE_PRIVILEGE, props.getProperty(privilegeProperty, ""));

                modulesInfos.add(moduleInfo);
            }

            // Get only modules names
            return modulesInfos;

        } catch(Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't retrieve modules", e);
            }
            throw new AcmModuleConfigurationException("Can't retrieve modules", e);
        }
    }


    public void setAppConfigPropertiesFile(String appConfigPropertiesFile) {
        this.appConfigPropertiesFile = appConfigPropertiesFile;
    }
}
