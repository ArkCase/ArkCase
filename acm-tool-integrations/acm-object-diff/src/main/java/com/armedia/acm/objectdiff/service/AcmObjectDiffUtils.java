package com.armedia.acm.objectdiff.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.objectdiff.model.AcmCollectionChange;
import com.armedia.acm.objectdiff.model.AcmCollectionElementAdded;
import com.armedia.acm.objectdiff.model.AcmCollectionElementChange;
import com.armedia.acm.objectdiff.model.AcmCollectionElementModified;
import com.armedia.acm.objectdiff.model.AcmCollectionElementRemoved;
import com.armedia.acm.objectdiff.model.AcmDiffBeanConfiguration;
import com.armedia.acm.objectdiff.model.AcmObjectModified;
import com.armedia.acm.objectdiff.model.AcmValueChanged;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class AcmObjectDiffUtils
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, AcmDiffBeanConfiguration> configurationMap = new HashMap<>();

    public AcmValueChanged getAcmPropertyChange(String path, Object oldObj, Object updatedObj, String fieldName)
    {
        if (oldObj == null || updatedObj == null)
        {
            throw new IllegalArgumentException("oldObj or updated object must not be null");
        }
        if (!oldObj.getClass().equals(updatedObj.getClass()))
        {
            throw new IllegalArgumentException("not same type of object! got: oldObj=" + oldObj.getClass().getName() + ", updatedObj=" + updatedObj.getClass().getName());
        }
        Class clazz = oldObj.getClass();
        Field field = FieldUtils.getField(clazz, fieldName, true);
        try
        {
            if (field.getType().isPrimitive())
            {
                if (field.get(oldObj) != field.get(updatedObj))
                {
                    AcmValueChanged acmValueChanged = createValueChange(fieldName, field.get(oldObj), field.get(updatedObj), path);
                    return acmValueChanged;
                }
            } else if ((field.get(oldObj) instanceof Comparable))
            {
                if (ObjectUtils.compare(Comparable.class.cast(field.get(oldObj)), Comparable.class.cast(field.get(updatedObj))) != 0)
                {
                    AcmValueChanged acmValueChanged = createValueChange(fieldName, field.get(oldObj), field.get(updatedObj), path);
                    return acmValueChanged;
                }
            }
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public boolean idMatches(Object oldObj, Object newObj)
    {
        if (oldObj == null || newObj == null)
        {
            throw new IllegalArgumentException("oldObj or updated object must not be null");
        }
        if (!oldObj.getClass().equals(newObj.getClass()))
        {
            throw new IllegalArgumentException("not same type of object! got: oldObj=" + oldObj.getClass().getName() + ", newObj=" + newObj.getClass().getName());
        }

        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());

        if (cfg == null)
        {
            log.warn("Cfg is null for [{}]", oldObj);
            return false;
        }
        boolean sameObject = true;
        Class clazz = oldObj.getClass();
        for (String id : cfg.getId())
        {
            try
            {
                Field field = FieldUtils.getField(clazz, id, true);
                if (field == null)
                {
                    throw new IllegalArgumentException("field name=[" + id + "] not exist in " + oldObj.getClass().getName());
                }
                field.setAccessible(true);
                if (field.getType().isPrimitive())
                {
                    if (field.get(oldObj) != field.get(newObj))
                    {
                        sameObject = false;
                    }
                } else if (field.get(oldObj) instanceof Comparable)
                {
                    if (ObjectUtils.compare(Comparable.class.cast(field.get(oldObj)), Comparable.class.cast(field.get(newObj))) != 0)
                    {
                        sameObject = false;
                    }
                }
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

        }
        return sameObject;
    }

    public void setConfigurationMap(String jsonConfiguration)
    {
        ObjectMapper mapper = new ObjectMapper();
        //objects are stored in map for more efficient access
        Map<String, AcmDiffBeanConfiguration> configurationMap = new HashMap<>();
        try
        {
            List<AcmDiffBeanConfiguration> myObjects = mapper.readValue(jsonConfiguration, mapper.getTypeFactory().constructCollectionType(List.class, AcmDiffBeanConfiguration.class));
            for (AcmDiffBeanConfiguration cfg : myObjects)
            {
                configurationMap.put(cfg.getClassName(), cfg);
            }
            this.configurationMap = configurationMap;
        } catch (IOException e)
        {
            log.error("Unable to parse the configuration.", e);
        }
    }

    public AcmObjectModified getObjectChange(String property, Object oldObj, Object newObj)
    {
        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());

        AcmObjectModified acmObjectModified = new AcmObjectModified();
        if (oldObj == null && newObj == null)
        {
            //they are same
            return null;
        }

        acmObjectModified.setPath(cfg.getName());
        acmObjectModified.setProperty(property);
        updateChangeForAcmObjectInfo(oldObj, acmObjectModified);

        //check if objects are same types
        if (!oldObj.getClass().equals(newObj.getClass()))
        {
            throw new IllegalArgumentException(String.format("not same type of object! got: oldObj=%s, newObj=%s", oldObj.getClass().getName(), newObj.getClass().getName()));
        }

        if (idMatches(oldObj, newObj))
        {
            Class clazz = oldObj.getClass();
            for (String fieldName : cfg.getIncludeFields())
            {
                Field field = FieldUtils.getField(clazz, fieldName, true);
                if (field.getType().isPrimitive())
                {
                    try
                    {
                        if (field.get(oldObj) != field.get(newObj))
                        {
                            AcmValueChanged valueChanged = createValueChange(fieldName, field.get(oldObj), field.get(newObj), cfg.getName());
                            acmObjectModified.addChange(valueChanged);
                        }
                    } catch (IllegalAccessException e)
                    {
                        log.warn("field [{}] not accessible of class [{}].", fieldName, clazz.getName());
                    }
                } else
                {
                    try
                    {
                        Object oldValue = field.get(oldObj);
                        Object newValue = field.get(newObj);
                        //which object is not null
                        Class validValueClass;
                        if (oldValue == null && newValue == null)
                        {
                            //skip field comparision since both are null and are same
                            continue;
                        } else
                        {
                            //doesn't matter which value is used
                            validValueClass = oldValue != null ? oldValue.getClass() : newValue.getClass();
                        }

                        if (ClassUtils.isPrimitiveOrWrapper(validValueClass) || validValueClass.isAssignableFrom(String.class))
                        {
                            if (ObjectUtils.compare(Comparable.class.cast(oldValue), Comparable.class.cast(newValue)) != 0)
                            {
                                AcmValueChanged valueChanged = createValueChange(fieldName, oldValue, newValue, "");
                                acmObjectModified.addChange(valueChanged);
                            }
                        } else if (oldValue instanceof Collection)
                        {
                            AcmCollectionChange collectionChange = getCollectionChange(acmObjectModified.getPath(), fieldName, Collection.class.cast(oldValue), Collection.class.cast(newValue));
                            if (collectionChange != null && !collectionChange.getChanges().isEmpty())
                            {
                                acmObjectModified.addChange(collectionChange);
                            }
                        }
                    } catch (IllegalAccessException e)
                    {
                        log.warn("field [{}] not accessible of class [{}].", fieldName, clazz.getName());
                    }
                }
            }
        } else
        {
            log.error("ID don't matched");
        }
        if (acmObjectModified.getChanges().isEmpty())
        {
            return null;
        } else
        {
            return acmObjectModified;
        }
    }

    private AcmValueChanged createValueChange(String fieldName, Object oldValue, Object newValue, String path)
    {
        AcmValueChanged propertyChange = new AcmValueChanged(path, fieldName);
        propertyChange.setOldValue(oldValue);
        propertyChange.setNewValue(newValue);
        return propertyChange;
    }

    public AcmCollectionChange getCollectionChange(String path, String property, Collection oldCollection, Collection newCollection)
    {
        if (oldCollection instanceof List && oldCollection instanceof List)
        {
            return getListChange(path, property, List.class.cast(oldCollection), List.class.cast(newCollection));
        }
        return null;
    }

    private AcmCollectionChange getListChange(String path, String property, List oldList, List newList)
    {
        AcmCollectionChange acmListChange = new AcmCollectionChange(path, property);
        acmListChange.setPath(path + "." + property);
        for (Object oldObj : oldList)
        {
            boolean found = false;
            for (Object newObj : newList)
            {
                if (idMatches(oldObj, newObj))
                {
                    found = true;
                    AcmObjectModified change = getObjectChange(null, oldObj, newObj);

                    if (change != null)
                    {
                        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());
                        change.setPath(acmListChange.getPath() + "." + cfg.getName());
                        updateChangeForAcmObjectInfo(oldObj, change);
                        acmListChange.addChange(new AcmCollectionElementModified(change));
                    }
                }
            }
            if (!found)
            {
                AcmCollectionElementRemoved elementChange = new AcmCollectionElementRemoved(oldObj);
                AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());
                elementChange.setPath(acmListChange.getPath() + "." + cfg.getName());
                updateChangeForAcmCollectionObjectInfo(oldObj, elementChange);
                acmListChange.addChange(elementChange);
            }
        }

        for (Object newObj : newList)
        {
            boolean found = false;
            for (Object oldObj : oldList)
            {
                if (idMatches(oldObj, newObj))
                {
                    found = true;
                }
            }
            if (!found)
            {
                AcmCollectionElementAdded elementChange = new AcmCollectionElementAdded(newObj);
                AcmDiffBeanConfiguration cfg = configurationMap.get(newObj.getClass().getName());
                updateChangeForAcmCollectionObjectInfo(newObj, elementChange);
                elementChange.setPath(acmListChange.getPath() + "." + cfg.getName());
                acmListChange.addChange(elementChange);
            }
        }
        if (acmListChange.getChanges().isEmpty())
        {
            return null;
        } else
        {
            return acmListChange;
        }
    }

    private void updateChangeForAcmObjectInfo(Object obj, AcmObjectModified change)
    {
        if (obj instanceof AcmObjectModified)
        {
            change.setAffectedObjectId(AcmObject.class.cast(obj).getId());
            change.setAffectedObjectType(AcmObject.class.cast(obj).getObjectType());
        }
    }

    private void updateChangeForAcmCollectionObjectInfo(Object obj, AcmCollectionElementChange change)
    {
        if (obj instanceof AcmCollectionElementChange)
        {
            change.setAffectedObjectId(AcmObject.class.cast(obj).getId());
            change.setAffectedObjectType(AcmObject.class.cast(obj).getObjectType());
        }
    }
}
