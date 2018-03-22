package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.transcribe.annotation.ConfigurationProperties;
import com.armedia.acm.services.transcribe.annotation.ConfigurationProperty;
import com.armedia.acm.services.transcribe.editor.BooleanEditor;
import com.armedia.acm.services.transcribe.editor.BigDecimalEditor;
import com.armedia.acm.services.transcribe.editor.ListEditor;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;
import com.armedia.acm.services.transcribe.model.ConfigurationActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
public class ConfigurationPropertiesService<T> implements ConfigurationService<T>
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private PropertyFileManager propertyFileManager;

    private Pattern systemVariablesMatcher = Pattern.compile("(\\$\\{.*?\\})");

    @Override
    public T get() throws GetConfigurationException
    {
        return read();
    }

    @Override
    public T save(T configuration) throws SaveConfigurationException
    {
        return write(configuration);
    }

    private T read() throws GetConfigurationException
    {
        LOG.debug("Retrieving configuration.");

        try
        {
            T configuration = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
            // Get class level annotation
            ConfigurationProperties configurationProperties = configuration.getClass().getAnnotation(ConfigurationProperties.class);

            // Replace system variables in the URL with actual values
            String path = buildPath(configurationProperties.path());

            // Create map with key-value pairs from the configuration object. We need the keys in the next step
            Map<String, Object> keyValuesMapFromObject = getKeyValuesMapFromObject(configuration, ConfigurationActionType.READ);

            // Create map with key-value paris from configuration file
            Map<String, Object> keyValuesMapFromProperties = getPropertyFileManager().loadMultiple(path, keyValuesMapFromObject.keySet().toArray(new String[0]));

            // Set values taken from configuration file to configuration object
            configuration = setKeyValuesToObject(keyValuesMapFromProperties, configuration);

            LOG.debug("Configuration retrieved. Configuration = [{}]", configuration);

            return configuration;
        }
        catch(Exception e)
        {
            LOG.error("Configuration was not retrieved successfully. REASON=[{}]", e.getMessage());
            throw new GetConfigurationException("Failed to retrieve Configuration.", e);
        }
    }

    private T write(T configuration) throws SaveConfigurationException
    {
        LOG.debug("Saving configuration.");

        try
        {
            // Get class level annotation
            ConfigurationProperties configurationProperties = configuration.getClass().getAnnotation(ConfigurationProperties.class);

            // Replace system variables in the URL with actual values
            String path = buildPath(configurationProperties.path());

            // Create map with key-value pairs from the configuration object
            Map<String, Object> keyValuesMapFromObject = getKeyValuesMapFromObject(configuration, ConfigurationActionType.WRITE);

            // Convert previous key-value map of objects to key-value map of strings. We need strings for every kind of object
            // to be able to save in the properties file
            Map<String, String> keyValuesMapAsStrings = keyValuesMapFromObject
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                            entry -> getValueSafe(entry.getValue())
                    ));

            // Save configuration in the properties file
            getPropertyFileManager().storeMultiple(keyValuesMapAsStrings, path, false);

            LOG.debug("Configuration saved. Configuration = [{}]", configuration);

            return configuration;
        }
        catch (Exception e)
        {
            LOG.error("Configuration was not saved successfully. REASON=[{}]", e.getMessage());
            throw new SaveConfigurationException("Failed to save Configuration.", e);
        }
    }

    private String getValueSafe(Object value)
    {
        return value != null ? convertTypeToString(value.getClass(), value) : "";
    }

    private Map<String, Object> getKeyValuesMapFromObject(T configuration, ConfigurationActionType action) throws Exception
    {
        // Get fields from configuration object using reflection
        Map<String, Object> keyValueMap = new HashMap<>();
        Field[] fields = configuration.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            // Take the field and take field level annotation
            Field field = fields[i];
            ConfigurationProperty configurationProperty = field.getAnnotation(ConfigurationProperty.class);

            // Get values that are set in the annotation
            String key = configurationProperty.key();
            boolean read = configurationProperty.read();
            boolean write = configurationProperty.write();

            // Because the field is "private", set accessible to true, get the value, and back accessible to previous state
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(configuration);
            field.setAccessible(accessible);

            // Depending on the action (read or write), and values set in the annotation, we should exclude
            // some of the fields
            switch (action)
            {
                case READ:
                    if (read)
                    {
                        keyValueMap.put(key, value);
                    }
                    break;

                case WRITE:
                    if (write)
                    {
                        keyValueMap.put(key, value);
                    }
                    break;
            }
        }

        return keyValueMap;
    }

    private T setKeyValuesToObject(Map<String, Object> keyValuesMap, T configuration)
    {
        if (keyValuesMap != null && configuration != null)
        {
            // Get fields from configuration object using reflection
            Field[] fields = configuration.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                // Take the field and take field level annotation
                Field field = fields[i];
                ConfigurationProperty configurationProperty = field.getAnnotation(ConfigurationProperty.class);

                // Get values that are set in the annotation
                String key = configurationProperty.key();
                // In case the value taken from the properties is not String, convert to String
                // This step might be not necessary, but I want to be sure that someone else will not
                // invoke this method with map that contains non String values
                String str = convertTypeToString(keyValuesMap.get(key).getClass(), keyValuesMap.get(key));
                Object value = convertStringToType(field.getType(), str);

                try
                {
                    // Because the field is "private", set accessible to true, set the value, and back accessible to previous state
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(configuration, value);
                    field.setAccessible(accessible);
                }
                catch (Exception e)
                {
                    LOG.warn("Cannot set VALUE=[{}] to FIELD=[{}]. [null] value will be used instead.", value, field);
                }
            }
        }

        return configuration;
    }

    private Object convertStringToType(Class<?> c, String value)
    {
        // Registering editors are per thread. Register them before finding appropriate editor
        registerEditors();
        PropertyEditor editor = PropertyEditorManager.findEditor(c);

        if (editor != null)
        {
            editor.setAsText(value);
            return editor.getValue();
        }

        return null;
    }

    private String convertTypeToString(Class<?> c, Object value)
    {
        // Registering editors are per thread. Register them before finding appropriate editor
        registerEditors();
        PropertyEditor editor = PropertyEditorManager.findEditor(c);

        if (editor != null)
        {
            editor.setValue(value);
            return editor.getAsText();
        }

        return "";
    }

    private String buildPath(String path)
    {
        LOG.debug("Building Configuration properties file path.");
        LOG.debug("Before: {}", path);

        if (path != null)
        {
            Matcher matcher = systemVariablesMatcher.matcher(path);
            while (matcher.find())
            {
                String match = matcher.group().trim();
                String systemVariableName = match.replace("${", "").replace("}", "");
                String systemVariableValue = System.getProperty(systemVariableName);
                path = path.replace(match, systemVariableValue);
            }
        }

        LOG.debug("After: {}", path);

        return path;
    }

    private void registerEditors()
    {
        PropertyEditorManager.registerEditor(List.class, ListEditor.class);
        PropertyEditorManager.registerEditor(ArrayList.class, ListEditor.class);
        PropertyEditorManager.registerEditor(boolean.class, BooleanEditor.class);
        PropertyEditorManager.registerEditor(Boolean.class, BooleanEditor.class);
        PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalEditor.class);
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }
}
