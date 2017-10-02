package com.armedia.acm.objectdiff.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.objectdiff.model.AcmCollectionChange;
import com.armedia.acm.objectdiff.model.AcmCollectionElementAdded;
import com.armedia.acm.objectdiff.model.AcmCollectionElementChange;
import com.armedia.acm.objectdiff.model.AcmCollectionElementModified;
import com.armedia.acm.objectdiff.model.AcmCollectionElementRemoved;
import com.armedia.acm.objectdiff.model.AcmDiffBeanConfiguration;
import com.armedia.acm.objectdiff.model.AcmObjectChange;
import com.armedia.acm.objectdiff.model.AcmObjectModified;
import com.armedia.acm.objectdiff.model.AcmObjectReplaced;
import com.armedia.acm.objectdiff.model.AcmValueChanged;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class for comparing two objects for changes. Produces diff tree of all changes that are found
 * Only primitive with their wrappers, Classes defined in the acmObjectDiffSettings.json are supported
 */
public class AcmObjectDiffUtils
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, AcmDiffBeanConfiguration> configurationMap = new HashMap<>();

    /**
     * checks for two objects which are defined in acmObjectDiffSettings.json, if their ID's matches or not
     *
     * @param oldObj Old Object
     * @param newObj New Object
     * @return true if ID's matches
     */
    private boolean idMatches(Object oldObj, Object newObj)
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

    /**
     * Create object change which can be: AcmObjectModified, AcmObjectReplaced
     * <p>
     * AcmObjectModified if created if bean is defined in the config file
     * AcmObjectReplaced is created if ID of oldObject differs from ID of new Object or one of them is null
     *
     * @param oldObj Old object
     * @param newObj New object
     * @return AcmObjectChange or null if there is no change
     */
    public AcmObjectChange compareObjects(Object oldObj, Object newObj)
    {
        return compareObjects(null, null, oldObj, newObj);
    }

    /**
     * Create object change which can be: AcmObjectModified, AcmObjectReplaced
     * <p>
     * AcmObjectModified if created if bean is defined in the config file
     * AcmObjectReplaced is created if ID of oldObject differs from ID of new Object or one of them is null
     *
     * @param parentPath path of the parent object - can be null
     * @param property   if applicable, name of the field in the parent object
     * @param oldObj     Old object
     * @param newObj     New object
     * @return AcmObjectChange or null if there is no change
     */
    private AcmObjectChange compareObjects(String parentPath, String property, Object oldObj, Object newObj)
    {
        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());

        if (oldObj == null && newObj == null)
        {
            //they are same
            return null;
        }
        if (oldObj == null || newObj == null)
        {
            AcmObjectReplaced acmObjectReplaced = new AcmObjectReplaced(parentPath + "." + property, property);
            updateChangeForAcmObjectInfo(oldObj != null ? oldObj : newObj, acmObjectReplaced);
            acmObjectReplaced.setOldValue(oldObj);
            acmObjectReplaced.setNewValue(newObj);
            return acmObjectReplaced;
        }

        //check if objects are same types
        if (!oldObj.getClass().equals(newObj.getClass()))
        {
            throw new IllegalArgumentException(String.format("not same type of object! got: oldObj=%s, newObj=%s", oldObj.getClass().getName(), newObj.getClass().getName()));
        }


        AcmObjectModified acmObjectModified = new AcmObjectModified();
        if (property != null)
        {
            acmObjectModified.setProperty(property);
            acmObjectModified.setPath(parentPath + "." + property);
        } else
        {
            acmObjectModified.setPath(cfg.getName());
        }
        updateChangeForAcmObjectInfo(oldObj, acmObjectModified);

        if (idMatches(oldObj, newObj))
        {
            Class clazz = oldObj.getClass();
            for (String fieldName : cfg.getIncludeFields())
            {
                Field field = FieldUtils.getField(clazz, fieldName, true);
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
                        //doesn't matter which value is used for class descriptor
                        validValueClass = oldValue != null ? oldValue.getClass() : newValue.getClass();
                    }

                    if (ClassUtils.isPrimitiveOrWrapper(validValueClass) || validValueClass.isAssignableFrom(String.class))
                    {
                        if (ObjectUtils.compare(Comparable.class.cast(oldValue), Comparable.class.cast(newValue)) != 0)
                        {
                            AcmValueChanged valueChanged = createValueChange(acmObjectModified.getPath(), fieldName, oldValue, newValue);
                            acmObjectModified.addChange(valueChanged);
                        }
                    } else if (oldValue instanceof Collection)
                    {
                        AcmCollectionChange collectionChange = compareCollections(acmObjectModified.getPath(), fieldName, Collection.class.cast(oldValue), Collection.class.cast(newValue));
                        if (collectionChange != null && !collectionChange.getChanges().isEmpty())
                        {
                            acmObjectModified.addChange(collectionChange);
                        }
                    } else if (configurationMap.containsKey(validValueClass.getName()))
                    {
                        AcmObjectChange objectChange = compareObjects(acmObjectModified.getPath(), fieldName, oldValue, newValue);
                        if (objectChange != null)
                        {
                            acmObjectModified.addChange(objectChange);
                        }
                    }
                } catch (IllegalAccessException e)
                {
                    log.warn("field [{}] not accessible of class [{}].", fieldName, clazz.getName());
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

    /**
     * Creates Value change, if two wrappers of primitives or String are different
     *
     * @param parentPath path of the parent object
     * @param fieldName  name of the field in the parent object
     * @param oldValue   old value
     * @param newValue   new value
     * @return
     */
    private AcmValueChanged createValueChange(String parentPath, String fieldName, Object oldValue, Object newValue)
    {
        AcmValueChanged valueChange = new AcmValueChanged(parentPath + "." + fieldName, fieldName);
        valueChange.setOldValue(oldValue);
        valueChange.setNewValue(newValue);
        return valueChange;
    }

    /**
     * AcmCollectionChange is created if some element in new Collection is added, removed or modified
     *
     * @param oldCollection Old collection
     * @param newCollection New Collection
     * @return AcmCollectionChange
     */
    public AcmCollectionChange compareCollections(Collection oldCollection, Collection newCollection)
    {
        return compareCollections(null, null, oldCollection, newCollection);
    }

    /**
     * AcmCollectionChange is created if some element in new Collection is added, removed or modified
     *
     * @param parentPath    path of the parent object
     * @param property      field name in the parent object
     * @param oldCollection Old collection
     * @param newCollection New Collection
     * @return AcmCollectionChange
     */
    private AcmCollectionChange compareCollections(String parentPath, String property, Collection oldCollection, Collection newCollection)
    {
        if (oldCollection instanceof List && oldCollection instanceof List)
        {
            return createListChange(parentPath, property, List.class.cast(oldCollection), List.class.cast(newCollection));
        } else if (oldCollection instanceof Map && oldCollection instanceof Map)
        {
            return createMapChange(parentPath, property, Map.class.cast(oldCollection), Map.class.cast(newCollection));
        }
        //TODO create handling for additional implementations of Collection
        return null;
    }

    private AcmCollectionChange createMapChange(String path, String property, Map oldMap, Map newMap)
    {
        log.error("createMapChange - Not implemented yet!!!!");
        //TODO implementation
        return null;
    }

    /**
     * Implementation of the CollectionChange
     * AcmCollectionChange is created if some element in new List is added, removed or modified
     *
     * @param parentPath path of the parent object
     * @param property   field name in the parent object
     * @param oldList    Old collection
     * @param newList    New Collection
     * @return AcmCollectionChange
     */
    private AcmCollectionChange createListChange(String parentPath, String property, List oldList, List newList)
    {
        AcmCollectionChange acmListChange = new AcmCollectionChange(parentPath + "." + property, property);
        for (Object oldObj : oldList)
        {
            boolean found = false;
            for (Object newObj : newList)
            {
                if (idMatches(oldObj, newObj))
                {
                    found = true;
                    AcmObjectChange change = compareObjects(acmListChange.getPath(), null, oldObj, newObj);

                    if (change != null && change instanceof AcmObjectModified)
                    {
                        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());
                        change.setPath(acmListChange.getPath() + "." + cfg.getName());
                        updateChangeForAcmObjectInfo(oldObj, change);
                        acmListChange.addChange(new AcmCollectionElementModified((AcmObjectModified) change));
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

    /**
     * update objectId and objectType for AcmObjectChange
     *
     * @param obj    object which is affected
     * @param change existing AcmObjectChange which needs to be updated
     */
    private void updateChangeForAcmObjectInfo(Object obj, AcmObjectChange change)
    {
        if (obj instanceof AcmObject)
        {
            change.setAffectedObjectId(AcmObject.class.cast(obj).getId());
            change.setAffectedObjectType(AcmObject.class.cast(obj).getObjectType());

        } else
        {
            log.warn("Object [{}] is not AcmObject", obj);
        }
    }

    /**
     * update objectId and objectType for AcmCollectionElementChange
     *
     * @param obj    object which is affected
     * @param change existing AcmCollectionElementChange which needs to be updated
     */
    private void updateChangeForAcmCollectionObjectInfo(Object obj, AcmCollectionElementChange change)
    {
        if (obj instanceof AcmObject)
        {
            change.setAffectedObjectId(AcmObject.class.cast(obj).getId());
            change.setAffectedObjectType(AcmObject.class.cast(obj).getObjectType());
        } else
        {
            log.warn("Object [{}] is not AcmObject", obj);
        }
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
}
