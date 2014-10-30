package com.armedia.acm.data;

import java.util.Date;

/**
 * All ACM JPA entity classes must implement this interface.  An EclipseLink session
 * listener uses it to automatically set the created, creator, modified, and modifier fields.
 */
public interface AcmEntity
{
    String getCreator();
    void setCreator(String creator);
    String getModifier();
    void setModifier(String modifier);

    Date getCreated();
    void setCreated(Date created);
    Date getModified();
    void setModified(Date modified);
}
