package com.armedia.acm.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by armdev on 10/21/14.
 */
public class AcmObjectChangelist
{
    private List<Object> addedObjects = new CopyOnWriteArrayList<>();
    private List<Object> updatedObjects = new CopyOnWriteArrayList<>();
    private List<Object> deletedObjects = new CopyOnWriteArrayList<>();

    public List<Object> getAddedObjects()
    {
        return addedObjects;
    }

    public void setAddedObjects(List<Object> addedObjects)
    {
        this.addedObjects = addedObjects;
    }

    public List<Object> getUpdatedObjects()
    {
        return updatedObjects;
    }

    public void setUpdatedObjects(List<Object> updatedObjects)
    {
        this.updatedObjects = updatedObjects;
    }

    public List<Object> getDeletedObjects()
    {
        return deletedObjects;
    }

    public void setDeletedObjects(List<Object> deletedObjects)
    {
        this.deletedObjects = deletedObjects;
    }

    @Override
    public String toString()
    {
        return "AcmObjectChangelist{" +
                "addedObjects=" + addedObjects +
                ", updatedObjects=" + updatedObjects +
                ", deletedObjects=" + deletedObjects +
                '}';
    }
}
