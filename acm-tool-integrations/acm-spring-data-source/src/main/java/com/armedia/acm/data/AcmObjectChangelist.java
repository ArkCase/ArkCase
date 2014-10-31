package com.armedia.acm.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class AcmObjectChangelist
{
    private List<Object> addedObjects = new ArrayList<>();
    private List<Object> updatedObjects = new ArrayList<>();
    private List<Object> deletedObjects = new ArrayList<>();

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
