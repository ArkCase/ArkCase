package com.armedia.acm.objectdiff.service;

/*-
 * #%L
 * Tool Integrations: Object Diff Util
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.objectdiff.model.AcmChange;
import com.armedia.acm.objectdiff.model.AcmCollectionChange;
import com.armedia.acm.objectdiff.model.AcmCollectionElementAdded;
import com.armedia.acm.objectdiff.model.AcmCollectionElementChange;
import com.armedia.acm.objectdiff.model.AcmCollectionElementModified;
import com.armedia.acm.objectdiff.model.AcmCollectionElementRemoved;
import com.armedia.acm.objectdiff.model.AcmDiff;
import com.armedia.acm.objectdiff.model.AcmDiffBeanConfiguration;
import com.armedia.acm.objectdiff.model.AcmObjectChange;
import com.armedia.acm.objectdiff.model.AcmObjectDiff;
import com.armedia.acm.objectdiff.model.AcmObjectModified;
import com.armedia.acm.objectdiff.model.AcmObjectReplaced;
import com.armedia.acm.objectdiff.model.AcmValueChanged;
import com.armedia.acm.objectdiff.model.interfaces.AcmChangeDisplayable;
import com.armedia.acm.objectonverter.ObjectConverter;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Util class for comparing two objects for changes. Produces diff tree of all changes that are found Only primitive
 * with their wrappers, Classes defined in the acmObjectDiffSettings.json are supported
 */
public class AcmDiffService
{
    private Logger log = LogManager.getLogger(getClass());
    private Map<String, AcmDiffBeanConfiguration> configurationMap = new HashMap<>();
    private ExpressionParser expressionParser = new SpelExpressionParser();
    private ObjectConverter objectConverter;
    private String jsonConfiguration;

    public void initConfigurationMap()
    {
        // objects are stored in map for more efficient access
        Map<String, AcmDiffBeanConfiguration> configurationMap = new HashMap<>();
        List<AcmDiffBeanConfiguration> myObjects = getObjectConverter().getJsonUnmarshaller().unmarshallCollection(getJsonConfiguration(),
                List.class, AcmDiffBeanConfiguration.class);

        for (AcmDiffBeanConfiguration cfg : myObjects)
        {
            configurationMap.put(cfg.getClassName(), cfg);
        }
        this.configurationMap = configurationMap;
    }

    /**
     * checks for two objects which are defined in acmObjectDiffSettings.json, if their ID's matches or not
     *
     * @param oldObj
     *            Old Object
     * @param newObj
     *            New Object
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
            throw new IllegalArgumentException(
                    "not same type of object! got: oldObj=" + oldObj.getClass().getName() + ", newObj=" + newObj.getClass().getName());
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
                }
                else if (field.get(oldObj) instanceof Comparable)
                {
                    if (ObjectUtils.compare(Comparable.class.cast(field.get(oldObj)), Comparable.class.cast(field.get(newObj))) != 0)
                    {
                        sameObject = false;
                    }
                }
            }
            catch (IllegalAccessException e)
            {
                log.error("Field not accessible.", e);
            }

        }
        return sameObject;
    }

    /**
     * Create object change which can be: AcmObjectModified, AcmObjectReplaced
     * <p>
     * AcmObjectModified if created if bean is defined in the config file AcmObjectReplaced is created if ID of
     * oldObject differs from ID of new Object or one of them is null
     *
     * @param oldObj
     *            Old object
     * @param newObj
     *            New object
     * @return AcmDiff
     */
    public AcmDiff compareObjects(Object oldObj, Object newObj)
    {

        Map<Object, List<AcmChange>> alreadyProcessed = new HashMap<>();
        AcmObjectChange acmObjectChange = compareObjects(null, null, oldObj, newObj, alreadyProcessed);
        AcmDiff diff = new AcmObjectDiff(acmObjectChange, getObjectConverter());

        return diff;
    }

    /**
     * Create object change which can be: AcmObjectModified, AcmObjectReplaced
     * <p>
     * AcmObjectModified if created if bean is defined in the config file AcmObjectReplaced is created if ID of
     * oldObject differs from ID of new Object or one of them is null
     *
     * @param parentPath
     *            path of the parent object - can be null
     * @param property
     *            if applicable, name of the field in the parent object
     * @param oldObj
     *            Old object
     * @param newObj
     *            New object
     * @return AcmObjectChange or null if there is no change
     */
    private AcmObjectChange compareObjects(String parentPath, String property, Object oldObj, Object newObj,
            Map<Object, List<AcmChange>> alreadyProcessed)
    {

        if (oldObj == null && newObj == null)
        {
            // they can't be compared
            return null;
        }

        boolean idMatches;

        if (oldObj == null || newObj == null || !(idMatches = idMatches(oldObj, newObj)))
        {
            AcmObjectReplaced acmObjectReplaced = createAcmObjectReplaced(parentPath, property, oldObj, newObj);
            return acmObjectReplaced;
        }

        AcmDiffBeanConfiguration cfg = configurationMap.get(oldObj.getClass().getName());
        if (cfg == null)
        {
            // no configuration for given object
            return null;
        }

        // check if objects are same types
        if (!oldObj.getClass().equals(newObj.getClass()))
        {
            throw new IllegalArgumentException(String.format("not same type of object! got: oldObj=%s, newObj=%s",
                    oldObj.getClass().getName(), newObj.getClass().getName()));
        }

        if (idMatches)
        {
            boolean processed = alreadyProcessed.containsKey(oldObj);

            // Since both objects are not null and have same id, they can be compared by fields
            AcmObjectModified acmObjectModified = new AcmObjectModified();
            if (property != null)
            {
                acmObjectModified.setProperty(property);
                acmObjectModified.setPath(parentPath + "." + property);
            }
            else
            {
                String path = cfg.getName();
                if (parentPath != null && parentPath.length() > 0)
                {
                    path = parentPath + "." + path;
                }
                acmObjectModified.setPath(path);
            }
            updateObjectInfo(oldObj, acmObjectModified);

            Class clazz = oldObj.getClass();
            if (processed)
            {
                // skip comparing fields
                acmObjectModified.setChanges(alreadyProcessed.get(oldObj));
            }
            else
            {
                for (String fieldName : cfg.getIncludeFields())
                {
                    Field field = FieldUtils.getField(clazz, fieldName, true);
                    if (field == null)
                    {
                        log.error("field [{}] not found for class [{}].", fieldName, clazz.getName());
                        continue;
                    }
                    try
                    {
                        Object oldValue = field.get(oldObj);
                        Object newValue = field.get(newObj);

                        // which object is not null
                        Class validValueClass;
                        Object validValue;
                        if (oldValue == null && newValue == null)
                        {
                            // skip field comparision since both are null and are same
                            continue;
                        }
                        else
                        {
                            // doesn't matter which value is used for class descriptor
                            validValueClass = oldValue != null ? oldValue.getClass() : newValue.getClass();
                            validValue = oldValue != null ? oldValue : newValue;
                        }

                        // compare primitives, String or Date/Time values
                        if (ClassUtils.isPrimitiveOrWrapper(validValueClass) || String.class.isAssignableFrom(validValueClass)
                                || Date.class.isAssignableFrom(validValueClass)
                                || Temporal.class.isAssignableFrom(validValueClass))
                        {
                            if (ObjectUtils.compare(Comparable.class.cast(oldValue), Comparable.class.cast(newValue)) != 0)
                            {
                                AcmValueChanged valueChanged = createValueChange(acmObjectModified.getPath(), fieldName, oldValue,
                                        newValue);
                                acmObjectModified.addChange(valueChanged);
                            }
                        }
                        // compare objects which are implementation of Collection
                        else if (validValue instanceof Collection)
                        {
                            AcmCollectionChange collectionChange = compareCollections(acmObjectModified.getPath(), fieldName,
                                    Collection.class.cast(oldValue), Collection.class.cast(newValue), alreadyProcessed);
                            if (collectionChange != null && !collectionChange.getChanges().isEmpty())
                            {
                                acmObjectModified.addChange(collectionChange);
                            }
                        }
                        // compare objects defined in the configuration
                        else if (configurationMap.containsKey(validValueClass.getName()))
                        {
                            AcmObjectChange objectChange = compareObjects(acmObjectModified.getPath(), fieldName, oldValue, newValue,
                                    alreadyProcessed);
                            if (objectChange != null)
                            {
                                acmObjectModified.addChange(objectChange);
                            }
                        }
                    }
                    catch (IllegalAccessException e)
                    {
                        log.warn("field [{}] not accessible of class [{}].", fieldName, clazz.getName());
                    }
                }
            }
            if (processed)
            {
                // acm object modified is already generated for same object
                // TODO we should have in configuration whether to return processed object or not
                return null;
            }
            if (acmObjectModified.getChanges().isEmpty())
            {
                // no changes
                return null;
            }
            else
            {
                alreadyProcessed.put(oldObj, acmObjectModified.getChanges());
                return acmObjectModified;
            }
        }
        else
        {
            log.error("ID don't matched");
            return null;
        }

    }

    /**
     * Creates change if objects are replaced i.e. id is not same
     *
     * @param parentPath
     *            parent path
     * @param property
     *            property
     * @param oldObj
     *            Old Object
     * @param newObj
     *            New Object
     * @return
     */
    private AcmObjectReplaced createAcmObjectReplaced(String parentPath, String property, Object oldObj, Object newObj)
    {
        Object validObject = oldObj != null ? oldObj : newObj;

        String path = parentPath != null ? parentPath + "." + property : getPath(parentPath, validObject);
        AcmObjectReplaced acmObjectReplaced = new AcmObjectReplaced(path, property);
        updateObjectInfo(validObject, acmObjectReplaced);
        acmObjectReplaced.setOldObject(oldObj);
        acmObjectReplaced.setNewObject(newObj);
        updateChangeDisplayable(acmObjectReplaced, oldObj, newObj);
        return acmObjectReplaced;
    }

    private void updateChangeDisplayable(AcmChangeDisplayable acmObjectReplaced, Object oldObj, Object newObj)
    {
        if (oldObj == null && newObj == null)
        {
            // both objects are null, can't update display values
            return;
        }
        Object validObject = oldObj == null ? newObj : oldObj;
        AcmDiffBeanConfiguration cfg = configurationMap.get(validObject.getClass().getName());

        String displayExpression = cfg.getDisplayExpression();

        try
        {
            Expression exp = expressionParser.parseExpression(displayExpression);
            if (oldObj != null)
            {
                EvaluationContext oldObjContext = new StandardEvaluationContext(oldObj);
                acmObjectReplaced.setOldValue(exp.getValue(oldObjContext).toString());
            }
            if (newObj != null)
            {
                EvaluationContext newObjContext = new StandardEvaluationContext(newObj);
                acmObjectReplaced.setNewValue(exp.getValue(newObjContext).toString());
            }
        }
        catch (ParseException e)
        {
            log.warn("Expression is not valid [{}]", displayExpression);
        }
        catch (EvaluationException e)
        {
            log.warn("Expression is not valid [{}]", displayExpression);
        }
    }

    /**
     * AcmCollectionChange is created if some element in new Collection is added, removed or modified.
     * This method should be used only when two collections are of same type i.e. holding objects of same class
     *
     * @param oldCollection
     *            Old collection
     * @param newCollection
     *            New Collection
     * @return AcmCollectionChange
     */
    public AcmCollectionChange compareCollections(Collection oldCollection, Collection newCollection)
    {
        Map<Object, List<AcmChange>> alreadyProcessed = new HashMap<>();
        return compareCollections(null, null, oldCollection, newCollection, alreadyProcessed);
    }

    /**
     * AcmCollectionChange is created if some element in new Collection is added, removed or modified
     *
     * @param parentPath
     *            path of the parent object
     * @param property
     *            field name in the parent object
     * @param oldCollection
     *            Old collection
     * @param newCollection
     *            New Collection
     * @return AcmCollectionChange
     */
    private AcmCollectionChange compareCollections(String parentPath, String property, Collection oldCollection, Collection newCollection,
            Map<Object, List<AcmChange>> alreadyProcessed)
    {
        if ((oldCollection == null || oldCollection.size() < 1) && (newCollection == null || newCollection.size() < 1))
        {
            // they are same
            return null;
        }
        Collection validCollection = oldCollection != null ? oldCollection : newCollection;
        Class collectionType = getCollectionsType(oldCollection, newCollection);
        if (collectionType == null)
        {
            return null;
        }
        if (validCollection instanceof List)
        {
            return createListChange(parentPath, property, List.class.cast(oldCollection), List.class.cast(newCollection), collectionType,
                    alreadyProcessed);
        }
        else if (validCollection instanceof Map)
        {
            return createMapChange(parentPath, property, Map.class.cast(oldCollection), Map.class.cast(newCollection), collectionType);
        }
        // TODO create handling for additional implementations of Collection
        return null;
    }

    private AcmCollectionChange createMapChange(String path, String property, Map oldMap, Map newMap, Class clazz)
    {
        log.error("createMapChange - Not implemented yet!!!!");
        // TODO implementation
        return null;
    }

    /**
     * Implementation of the CollectionChange AcmCollectionChange is created if some element in new List is added,
     * removed or modified
     *
     * @param parentPath
     *            path of the parent object
     * @param property
     *            field name in the parent object
     * @param oldList
     *            Old collection
     * @param newList
     *            New Collection
     * @return AcmCollectionChange
     */
    private AcmCollectionChange createListChange(String parentPath, String property, List oldList, List newList, Class clazz,
            Map<Object, List<AcmChange>> alreadyProcessed)
    {
        // create empty list for comparing if list is empty
        oldList = oldList == null ? new LinkedList() : oldList;
        // create empty list for comparing if list is empty
        newList = newList == null ? new LinkedList() : newList;
        if (clazz == null || !configurationMap.containsKey(clazz.getName()))
        {
            // can't determine type of objects in the list
            return null;
        }
        AcmCollectionChange acmListChange = new AcmCollectionChange(parentPath + "." + property, property);
        for (Object oldObj : oldList)
        {
            String path = getPath(acmListChange.getPath(), oldObj);
            boolean found = false;
            for (Object newObj : newList)
            {
                if (idMatches(oldObj, newObj))
                {
                    found = true;
                    AcmObjectChange change = compareObjects(acmListChange.getPath(), null, oldObj, newObj, alreadyProcessed);
                    if (change != null && change instanceof AcmObjectModified)
                    {
                        change.setPath(path);
                        updateObjectInfo(oldObj, change);
                        // wrap it with AcmCollectionElementModified
                        acmListChange.addChange(new AcmCollectionElementModified((AcmObjectModified) change));
                    }
                }
            }
            if (!found)
            {
                // if not found that means element is removed
                AcmCollectionElementRemoved elementChange = createAcmCollectionElementRemoved(path, oldObj);
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
                String path = getPath(acmListChange.getPath(), newObj);
                AcmCollectionElementAdded elementChange = createAcmCollectionElementAdded(path, newObj);
                acmListChange.addChange(elementChange);
            }
        }
        if (acmListChange.getChanges().isEmpty())
        {
            return null;
        }
        else
        {
            return acmListChange;
        }
    }

    /**
     * Tries to determin which type of objects are inside the collection which needs to be compared. If holds different
     * objects or both collections are null or empty, null is returned, because there is no way to determine.
     *
     * @param oldCollection
     * @param newCollection
     * @return found object type which are elements in the collection
     */
    private Class getCollectionsType(Collection oldCollection, Collection newCollection)
    {
        if (oldCollection == null && newCollection == null)
        {
            // can't be determined
            return null;
        }

        Collection validCollection = oldCollection != null && oldCollection.size() > 0 ? oldCollection : newCollection;
        if (validCollection.size() < 1)
        {
            // can't be determined
            return null;
        }

        Iterator it = validCollection.iterator();

        Class collectionType = null;
        while (it.hasNext())
        {
            Object obj = it.next();
            if (collectionType == null)
            {
                collectionType = obj.getClass();
            }
            else if (!collectionType.equals(obj.getClass()))
            {
                // contains different type of objects
                return null;
            }

        }
        return collectionType;
    }

    /**
     * Constructs path for given parent path and obejct
     *
     * @param parentPath
     * @param obj
     * @return
     */
    private String getPath(String parentPath, Object obj)
    {
        AcmDiffBeanConfiguration cfg = configurationMap.get(obj.getClass().getName());
        String pathSuffix = cfg != null ? cfg.getName() : obj.getClass().getSimpleName();
        if (parentPath == null)
        {
            return pathSuffix;
        }
        else
        {
            return parentPath + "." + pathSuffix;
        }
    }

    /**
     * Creates Value change, if two wrappers of primitives or String are different
     *
     * @param parentPath
     *            path of the parent object
     * @param fieldName
     *            name of the field in the parent object
     * @param oldValue
     *            old value
     * @param newValue
     *            new value
     * @return AcmValueChanged
     */
    private AcmValueChanged createValueChange(String parentPath, String fieldName, Object oldValue, Object newValue)
    {
        AcmValueChanged valueChange = new AcmValueChanged(parentPath + "." + fieldName, fieldName);
        valueChange.setOldValue(oldValue != null ? oldValue.toString() : null);
        valueChange.setNewValue(newValue != null ? newValue.toString() : null);
        return valueChange;
    }

    /**
     * Created collection change - element added
     *
     * @param path
     *            parent path
     * @param addedObject
     *            added object
     * @return
     */
    private AcmCollectionElementAdded createAcmCollectionElementAdded(String path, Object addedObject)
    {
        AcmCollectionElementAdded elementChange = new AcmCollectionElementAdded(addedObject);
        updateObjectInfoForCollectionElement(addedObject, elementChange);
        elementChange.setPath(path);
        updateChangeDisplayable(elementChange, null, addedObject);
        return elementChange;
    }

    /**
     * Created collection change - element removed
     *
     * @param path
     *            parent path
     * @param removedObject
     *            removed object
     * @return
     */
    private AcmCollectionElementRemoved createAcmCollectionElementRemoved(String path, Object removedObject)
    {
        AcmCollectionElementRemoved elementChange = new AcmCollectionElementRemoved(removedObject);
        elementChange.setPath(path);
        updateObjectInfoForCollectionElement(removedObject, elementChange);
        updateChangeDisplayable(elementChange, removedObject, null);
        return elementChange;
    }

    /**
     * update objectId and objectType for AcmObjectChange
     *
     * @param obj
     *            object which is affected
     * @param change
     *            existing AcmObjectChange which needs to be updated
     */
    private void updateObjectInfo(Object obj, AcmObjectChange change)
    {
        if (obj instanceof AcmObject)
        {
            change.setAffectedObjectId(AcmObject.class.cast(obj).getId());
            change.setAffectedObjectType(AcmObject.class.cast(obj).getObjectType());

        }
        else
        {
            log.warn("Object [{}] is not AcmObject", obj);
        }
    }

    /**
     * update objectId and objectType for AcmCollectionElementChange
     *
     * @param obj
     *            object which is affected
     * @param change
     *            existing AcmCollectionElementChange which needs to be updated
     */
    private void updateObjectInfoForCollectionElement(Object obj, AcmCollectionElementChange change)
    {
        if (obj instanceof AcmObject)
        {
            change.setAffectedObjectId(AcmObject.class.cast(obj).getId());
            change.setAffectedObjectType(AcmObject.class.cast(obj).getObjectType());
        }
        else
        {
            log.warn("Object [{}] is not AcmObject", obj);
        }
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public String getJsonConfiguration()
    {
        return jsonConfiguration;
    }

    public void setJsonConfiguration(String jsonConfiguration)
    {
        this.jsonConfiguration = jsonConfiguration;
    }
}
