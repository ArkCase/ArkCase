package com.armedia.acm.objectdiff;

import com.armedia.acm.core.AcmObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class AcmObjectDiffUtils {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, AcmDiffBeanConfiguration> configurationMap = new HashMap<>();
    private String actionChanged = "changed";
    private String actionRemoved = "removed";
    private String actionAdded = "added";

    public AcmPropertyChange getAcmPropertyChange(String parent, Object oldObj, Object updatedObj, String fieldName, Long objectId, String objectType) {
        if (oldObj == null || updatedObj == null) {
            throw new IllegalArgumentException("oldObj or updated object must not be null");
        }
        if (!oldObj.getClass().equals(updatedObj.getClass())) {
            throw new IllegalArgumentException("not same type of object! got: oldObj=" + oldObj.getClass().getName() + ", updatedObj=" + updatedObj.getClass().getName());
        }
        Class clazz = oldObj.getClass();
        Field field = FieldUtils.getField(clazz, fieldName, true);
        try {
            if (field.getType().isPrimitive()) {
                if (field.get(oldObj) != field.get(updatedObj)) {
                    AcmPropertyChange acmPropertyChange = new AcmPropertyChange(parent, fieldName, actionChanged, objectId, objectType);
                    acmPropertyChange.setOldValue(field.get(oldObj));
                    acmPropertyChange.setNewValue(field.get(updatedObj));
                    return acmPropertyChange;
                }
            } else if ((field.get(oldObj) instanceof Comparable)) {
                if (ObjectUtils.compare(Comparable.class.cast(field.get(oldObj)), Comparable.class.cast(field.get(updatedObj))) != 0) {
                    AcmPropertyChange acmPropertyChange = new AcmPropertyChange(parent, fieldName, actionChanged, objectId, objectType);
                    acmPropertyChange.setOldValue(field.get(oldObj));
                    acmPropertyChange.setNewValue(field.get(updatedObj));
                    return acmPropertyChange;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean idMatches(Object oldObj, Object newObj) {
        if (oldObj == null || newObj == null) {
            throw new IllegalArgumentException("oldObj or updated object must not be null");
        }
        if (!oldObj.getClass().equals(newObj.getClass())) {
            throw new IllegalArgumentException("not same type of object! got: oldObj=" + oldObj.getClass().getName() + ", newObj=" + newObj.getClass().getName());
        }

        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());

        if (cfg == null) {
            log.warn("Cfg is null for [{}]", oldObj);
            return false;
        }
        boolean sameObject = true;
        Class clasz = oldObj.getClass();
        for (String id : cfg.getId()) {
            try {
                Field field = FieldUtils.getField(clasz, id, true);
                if (field == null) {
                    throw new IllegalArgumentException("field name=[" + id + "] not exist in " + oldObj.getClass().getName());
                }
                field.setAccessible(true);
                if (field.getType().isPrimitive()) {
                    if (field.get(oldObj) != field.get(newObj)) {
                        sameObject = false;
                    }
                } else if (field.get(oldObj) instanceof Comparable) {
                    if (ObjectUtils.compare(Comparable.class.cast(field.get(oldObj)), Comparable.class.cast(field.get(newObj))) != 0) {
                        sameObject = false;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return sameObject;
    }

    public void setConfigurationMap(String jsonConfiguration) {
        ObjectMapper mapper = new ObjectMapper();
        //objects are stored in map for more efficient access
        Map<String, AcmDiffBeanConfiguration> configurationMap = new HashMap<>();
        try {
            List<AcmDiffBeanConfiguration> myObjects = mapper.readValue(jsonConfiguration, mapper.getTypeFactory().constructCollectionType(List.class, AcmDiffBeanConfiguration.class));
            for (AcmDiffBeanConfiguration cfg : myObjects) {
                configurationMap.put(cfg.getClassName(), cfg);
            }
            this.configurationMap = configurationMap;
        } catch (IOException e) {
            log.error("Unable to parse the configuration.", e);
        }
    }

    public AcmObjectChange getObjectChange(String property, Object oldObj, Object newObj) {
        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getCanonicalName());

        AcmObjectChange acmObjectChange = new AcmObjectChange();
        if (oldObj == null && newObj == null) {
            //they are same
            return null;
        }
        Object validObject = oldObj != null ? oldObj : newObj;
        acmObjectChange.setPath(cfg.getPath());
        acmObjectChange.setProperty(property);
        Long objectId = null;
        String objectType = null;
        if (validObject instanceof AcmObject) {
            objectId = AcmObject.class.cast(validObject).getId();
            objectType = AcmObject.class.cast(validObject).getObjectType();
            acmObjectChange.setAffectedObjectId(objectId);
            acmObjectChange.setAffectedObjectType(objectType);
        }

        //check if objects are same types
        if (!oldObj.getClass().equals(newObj.getClass())) {
            throw new IllegalArgumentException(String.format("not same type of object! got: oldObj=%s, newObj=%s", oldObj.getClass().getCanonicalName(), newObj.getClass().getCanonicalName()));
        }

        if (idMatches(oldObj, newObj)) {
            Class clazz = oldObj.getClass();
            for (String fieldName : cfg.getIncludeFields()) {
                Field field = FieldUtils.getField(clazz, fieldName, true);
                if (field.getType().isPrimitive()) {
                    try {
                        if (field.get(oldObj) != field.get(newObj)) {
                            AcmPropertyChange propertyChange = createPropertyChange(objectId, objectType, fieldName, field.get(oldObj), field.get(newObj), cfg.getPath());
                            acmObjectChange.addChange(propertyChange);
                        }
                    } catch (IllegalAccessException e) {
                        log.warn("field [{}] not accessible of class [{}].", fieldName, clazz.getCanonicalName());
                    }
                } else {
                    try {
                        Object oldValue = field.get(oldObj);
                        Object newValue = field.get(newObj);
                        //which object is not null
                        Class validValueClass;
                        if (oldValue == null && newValue == null) {
                            //skip field comparision since both are null and are same
                            continue;
                        } else {
                            //doesn't matter which value is used
                            validValueClass = oldValue != null ? oldValue.getClass() : newValue.getClass();
                        }

                        if (ClassUtils.isPrimitiveOrWrapper(validValueClass) || validValueClass.isAssignableFrom(String.class)) {
                            if (ObjectUtils.compare(Comparable.class.cast(oldValue), Comparable.class.cast(newValue)) != 0) {
                                AcmPropertyChange propertyChange = createPropertyChange(objectId, objectType, fieldName, oldValue, newValue, "");
                                acmObjectChange.addChange(propertyChange);
                            }
                        } else if (oldValue instanceof Collection) {
                            AcmCollectionChange collectionChange = getCollectionChange(oldObj, Collection.class.cast(oldValue), Collection.class.cast(newValue));
                            if (collectionChange != null && !collectionChange.getChanges().isEmpty()) {
                                collectionChange.setProperty(fieldName);
                                acmObjectChange.addChange(collectionChange);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        log.warn("field [{}] not accessible of class [{}].", fieldName, clazz.getCanonicalName());
                    }
                }
            }
        } else {
            log.error("ID don't matched");
        }
        if (acmObjectChange.getChanges().isEmpty()) {
            return null;
        } else {
            return acmObjectChange;
        }
    }

    private AcmPropertyChange createPropertyChange(Long objectId, String objectType, String fieldName, Object oldValue, Object newValue, String path) {
        AcmPropertyChange propertyChange = new AcmPropertyChange(path, fieldName, "propertyChanged");
        propertyChange.setOldValue(oldValue);
        propertyChange.setNewValue(newValue);
        propertyChange.setAffectedObjectId(objectId);
        propertyChange.setAffectedObjectType(objectType);
        return propertyChange;
    }

    public AcmCollectionChange getCollectionChange(Object owner, Collection oldCollection, Collection newCollection) {
        if (oldCollection instanceof List && oldCollection instanceof List) {
            return getListChange(owner, List.class.cast(oldCollection), List.class.cast(newCollection));
        }
        return null;
    }

    private AcmCollectionChange getListChange(Object owner, List oldList, List newList) {
        AcmCollectionChange acmListChange = new AcmCollectionChange();
        Long objectId = null;
        String objectType = null;
        if (owner != null && owner instanceof AcmObject) {
            objectId = AcmObject.class.cast(owner).getId();
            objectType = AcmObject.class.cast(owner).getObjectType();
            acmListChange.setAffectedObjectId(objectId);
            acmListChange.setAffectedObjectType(objectType);
        }
        for (Object oldObj : oldList) {
            boolean found = false;
            for (Object newObj : newList) {
                if (idMatches(oldObj, newObj)) {
                    found = true;
                    AcmObjectChange change = getObjectChange(null, oldObj, newObj);

                    if (change != null) {
                        change.setAffectedObjectId(objectId);
                        change.setAffectedObjectType(objectType);
                        acmListChange.addChange(new AcmCollectionElementChanged(change));
                    }
                }
            }
            if (!found) {
                AcmCollectionElementRemoved elementChange = new AcmCollectionElementRemoved(oldObj);
                elementChange.setAffectedObjectId(objectId);
                elementChange.setAffectedObjectType(objectType);
                acmListChange.addChange(elementChange);
            }
        }

        //check for added new identifications
        for (Object newObj : newList) {
            boolean found = false;
            for (Object oldObj : oldList) {
                if (idMatches(oldObj, newObj)) {
                    found = true;
                }
            }
            if (!found) {
                AcmCollectionElementAdded elementChange = new AcmCollectionElementAdded(newObj);
                elementChange.setAffectedObjectId(objectId);
                elementChange.setAffectedObjectType(objectType);
                acmListChange.addChange(elementChange);
            }
        }
        if (acmListChange.getChanges().isEmpty()) {
            return null;
        } else {
            return acmListChange;
        }
    }
}
