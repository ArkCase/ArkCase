package com.armedia.acm.plugins.admin.web.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.armedia.acm.plugins.admin.exception.AcmLabelManagementException;
import com.armedia.acm.plugins.admin.model.ModuleConfig;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sergey on 2/12/16.
 */
public class LabelManagementService {
    private Logger log = LoggerFactory.getLogger(getClass());
    private List<String> languages;
    private String customResourcesLocation;
    private String customResourceFile;
    private String modulesLocation;
    private String moduleResourcesLocation;
    private String resourcesLocation;
    private String resourceFile;
    private String settingsFileLocation;


    /**
     * Return modules
     */
    public ArrayList<String> getModulesNames() {
        ArrayList<String> modulesNames = new ArrayList();
        ArrayList<ModuleConfig> modules = getModules();
        for (ModuleConfig moduleIter : modules) {
            modulesNames.add(moduleIter.getId());
        }

        return modulesNames;
    }

    /**
     * Return languages list.
     *
     * @return
     */
    public ArrayList<String> getLanguages() {
        // Languages list is stored in XML configuration
        return new ArrayList(languages);
    }

    /**
     * Return list of modules configuration
     *
     * @return
     */
    public ArrayList<ModuleConfig> getModules() {

        File modulesDir = new File(modulesLocation);

        File[] dirs = modulesDir.listFiles(File::isDirectory);

        ArrayList<ModuleConfig> modules = new ArrayList();

        for (File dirIter : dirs) {
            ModuleConfig module = new ModuleConfig();
            module.setId(dirIter.getName());
            module.setName(dirIter.getName());
            modules.add(module);
        }

        return modules;
    }

    /**
     * Return module's resource
     *
     * @param moduleId
     * @param lang
     * @param createIfAbsent
     * @return
     */
    public JSONObject getResource(String moduleId, String lang, boolean createIfAbsent) throws AcmLabelManagementException {
        String fileName = String.format(resourcesLocation + resourceFile, moduleId, lang);
        JSONObject resource = loadResource(fileName);

        // Try to create resource if required
        if (resource == null && createIfAbsent) {
            resource = updateResource(moduleId, lang);
        }

        return resource;
    }


    /**
     * Return module's resource string that contains current value, default value and description
     *
     * @param moduleId
     * @param lang
     * @return
     */
    public JSONObject getAdminResource(String moduleId, String lang) throws AcmLabelManagementException {
        // 1. Load module's resource file
        JSONObject moduleResource = normalizeResource(loadModuleResource(moduleId, lang));

        // 2. Load custom resource file
        JSONObject customResource = normalizeResource(loadCustomResource(moduleId, lang));

        // 3. Merge Module's and custom resources
        JSONObject adminResource = mergeAdminResouces(moduleResource, customResource);

        return adminResource;
    }


    /**
     * Return settings
     *
     * @param createIfAbsent
     * @return
     */
    public JSONObject getSettings(boolean createIfAbsent) throws AcmLabelManagementException {
        JSONObject settings = loadResource(settingsFileLocation);

        // Try to create resource if required
        if (settings == null && createIfAbsent) {
            // Create default settings
            JSONObject defaultSettings = getDefaultSettings();
            settings = updateSettings(defaultSettings);
        }

        return settings;
    }


    /**
     * Create default settings object
     *
     * @return
     */
    public JSONObject getDefaultSettings() {
        JSONObject defaultSettings = new JSONObject();

        // Get information about languages
        // Set first language in the list as default language
        defaultSettings.put("defaultLang", languages.get(0));
        return defaultSettings;
    }

    /**
     * Batch reset of modules
     *
     * @param modules
     * @param langs
     * @throws AcmLabelManagementException
     */
    public void reset(List<String> modules, List<String> langs) throws AcmLabelManagementException {
        for (String lang : langs) {
            for (String module : modules) {
                resetModule(module, lang);
            }
        }
    }

    /**
     * Remove module's custom resource
     *
     * @param moduleId
     * @param lang
     */
    public void resetModule(String moduleId, String lang) throws AcmLabelManagementException {
        String fileName = String.format(customResourcesLocation + customResourceFile, moduleId, lang);
        try {
            File resourceFile = new File(fileName);
            FileUtils.deleteQuietly(resourceFile);
            updateResource(moduleId, lang);
        } catch (Exception e) {
            String msg = String.format("Can't reset resource file %s", fileName);
            log.error(msg);
            throw new AcmLabelManagementException(msg);
        }
    }

    /**
     * Update resource file
     *
     * @param moduleId
     * @param lang
     * @return
     * @throws AcmLabelManagementException
     */
    public JSONObject updateResource(String moduleId, String lang) throws AcmLabelManagementException {
        // 1. Load module's resource file
        JSONObject moduleResource = normalizeResource(loadModuleResource(moduleId, lang));

        // 2. Load custom resource file
        JSONObject customResource = normalizeResource(loadCustomResource(moduleId, lang));

        // 3. Merge Module's and custom resources
        JSONObject resource = mergeResources(moduleResource, customResource);

        // 4. Save updated resource
        String fileName = String.format(resourcesLocation + resourceFile, moduleId, lang);
        try {
            File resourceFile = new File(fileName);
            FileUtils.writeStringToFile(resourceFile, resource.toString());
        } catch (Exception e) {
            String msg = String.format("Can't write resource into the file %s", fileName);
            log.error(msg);
            throw new AcmLabelManagementException(msg);
        }
        return resource;
    }

    /**
     * Update custom resource values
     *
     * @param moduleId
     * @param lang
     * @param newValue
     * @return
     * @throws AcmLabelManagementException
     */
    public JSONObject updateCustomResource(String moduleId, String lang, JSONObject newValue) throws AcmLabelManagementException {
        if (newValue == null
                || !(newValue.has("id") && newValue.has("description") && newValue.has("value"))) {
            throw new AcmLabelManagementException("Wrong format of custom resource");
        } else {
            String fileName = String.format(customResourcesLocation + customResourceFile, moduleId, lang);
            try {
                JSONObject customRes = loadCustomResource(moduleId, lang);
                if (customRes == null) {
                    customRes = new JSONObject();
                }
                String id = newValue.getString("id");
                customRes.put(id, newValue);
                File file = new File(fileName);
                FileUtils.writeStringToFile(file, customRes.toString(4));

                // Update also resource file
                updateResource(moduleId, lang);

                return customRes;
            } catch (Exception e) {
                String msg = String.format("Can't write resource into the file %s", fileName);
                log.error(msg);
                throw new AcmLabelManagementException(msg);
            }
        }
    }

    /**
     * Update Settings file
     *
     * @param objSettings
     * @return
     */
    public JSONObject updateSettings(JSONObject objSettings) throws AcmLabelManagementException {
        try {
            File file = FileUtils.getFile(settingsFileLocation);
            FileUtils.writeStringToFile(file, objSettings.toString());
        } catch (Exception e) {
            log.error(String.format("Can't write settings data in to the file %s", settingsFileLocation));
            throw new AcmLabelManagementException("Update settings error", e);
        }
        return objSettings;
    }

    /**
     * Convert resource json properties to next format: "prop1.prop2.prop3": "value"
     *
     * @param resource
     * @return
     */
    private JSONObject normalizeResource(JSONObject resource) {
        JSONObject res = new JSONObject();
        if (resource != null) {
            normalizeNode(resource, res, "");
        }
        return res;
    }


    /**
     * Recursive method to produce normalized resource string
     *
     * @param source
     * @param normalized
     * @param currentPath
     */
    private void normalizeNode(JSONObject source, JSONObject normalized, String currentPath) {
        Iterator<String> keys = source.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String propertyPath = (currentPath.isEmpty()) ? key : currentPath + '.' + key;
            Object jsonValue = source.get(key);
            if (jsonValue instanceof JSONObject) {
                normalizeNode((JSONObject) jsonValue, normalized, propertyPath);
            } else if (jsonValue instanceof String) {
                normalized.put(propertyPath, (String) jsonValue);
            }
        }
    }

    /**
     * Merge normalized module and custom resources to produce resource
     *
     * @param moduleRes
     * @param customRes
     * @return
     */
    private JSONObject mergeResources(JSONObject moduleRes, JSONObject customRes) {
        JSONObject res = new JSONObject();
        if (moduleRes != null) {
            Iterator<String> keys = moduleRes.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = moduleRes.getString(key);

                if (customRes != null) {
                    value = customRes.has(key + ".value") ? (String) customRes.get(key + ".value") : value;
                }

                res.put(key, value);
            }
        }

        return res;
    }

    /**
     * Merge normalized module and custom resources to produce admin resource
     *
     * @param moduleRes
     * @param customRes
     * @return
     */
    private JSONObject mergeAdminResouces(JSONObject moduleRes, JSONObject customRes) {
        JSONObject adminRes = new JSONObject();
        if (moduleRes != null) {
            Iterator<String> keys = moduleRes.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = moduleRes.getString(key);
                String defaultValue = value;
                String description = "";

                if (customRes != null) {
                    value = customRes.has(key + ".value") ? (String) customRes.get(key + ".value") : value;
                    description = customRes.has(key + ".value") ? (String) customRes.get(key + ".description") : "";
                }

                JSONObject node = new JSONObject();
                node.put("value", value);
                node.put("description", description);
                node.put("defaultValue", defaultValue);

                adminRes.put(key, node);
            }
        }

        return adminRes;
    }

    /**
     * Load custom resource
     *
     * @param moduleId
     * @param lang
     * @return
     */
    private JSONObject loadCustomResource(String moduleId, String lang) throws AcmLabelManagementException {
        String fileName = String.format(customResourcesLocation + customResourceFile, moduleId, lang);
        return loadResource(fileName);
    }


    /**
     * Load module's resource
     *
     * @param moduleId
     * @param lang
     * @return
     */
    private JSONObject loadModuleResource(String moduleId, String lang) throws AcmLabelManagementException {
        String fileName = String.format(moduleResourcesLocation, moduleId, lang);
        return loadResource(fileName);
    }

    /**
     * Load JSON resource file
     *
     * @param fileName
     * @return
     * @throws AcmLabelManagementException
     */
    private JSONObject loadResource(String fileName) throws AcmLabelManagementException {
        try {
            File file = FileUtils.getFile(fileName);
            String resource = FileUtils.readFileToString(file);
            return new JSONObject(resource);

        } catch (Exception e) {
            log.warn(String.format("Can't read resource file %s", fileName));
            return null;
        }

    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public void setCustomResourcesLocation(String customResourcesLocation) {
        this.customResourcesLocation = customResourcesLocation;
    }

    public void setModulesLocation(String modulesLocation) {
        this.modulesLocation = modulesLocation;
    }

    public void setCustomResourceFile(String customResourceFile) {
        this.customResourceFile = customResourceFile;
    }

    public void setModuleResourcesLocation(String moduleResourcesLocation) {
        this.moduleResourcesLocation = moduleResourcesLocation;
    }

    public void setResourcesLocation(String resourcesLocation) {
        this.resourcesLocation = resourcesLocation;
    }

    public void setResourceFile(String resourceFile) {
        this.resourceFile = resourceFile;
    }

    public void setSettingsFileLocation(String settingsFileLocation) {
        this.settingsFileLocation = settingsFileLocation;
    }
}
