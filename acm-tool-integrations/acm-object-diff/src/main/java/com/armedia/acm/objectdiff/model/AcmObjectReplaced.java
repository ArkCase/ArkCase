package com.armedia.acm.objectdiff.model;

public class AcmObjectReplaced extends AcmObjectChange
{
    private Object oldObject;
    private Object newObject;

    public AcmObjectReplaced(String path, String property)
    {
        setProperty(property);
        setPath(path);
        setAction(AcmDiffConstants.OBJECT_REPLACED);
    }

    public Object getOldObject()
    {
        return oldObject;
    }

    public void setOldObject(Object oldObject)
    {
        this.oldObject = oldObject;
    }

    public Object getNewObject()
    {
        return newObject;
    }

    public void setNewObject(Object newObject)
    {
        this.newObject = newObject;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }
}
